/**
 * C55xEnergyAnalysis.java
 */

package c55x;

import analysis.Analysis;
import basicblocks.BasicBlock;
import basicblocks.BasicBlockEdge;
import cfg.CFG;
import cfg.CFGNode;
import graph.Graph;
import graph.Edge;
import graph.Node;
import machine.Machine;
import program.BranchTarget;
import program.TiwariResults;
import program.Label;
import program.Program;
import program.Procedure;
import program.Symbols;
import input.*;
import pseudoOp.PseudoOp;
import instr.*;
import main.*;
import c55x.instr.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;

/**
 * Energy analysis.
 *
 * @author Kristian Söderblom
 * @see Analysis
 */


//java main/Main -sim -ea -v -Oc -march=c55x c55x/test/fir.dis

public class C55xEnergyAnalysis implements Analysis
{
    // 0   = use minimum value in current range
    // 0.5 = use average value in current range
    // 1.0 = use maximum value in current range

    private double parameterS = 0.15;

    /**
     * Performs Tiwari energy analysis of the program
     *
     * @param program the program
     * @param machine machine on which the program runs
     */
    public void analyze(Program program, Machine machine) {
        double r = parameterS;

        // contributions from bb nodes
        TiwariResults nodeResults = new TiwariResults(machine);
        Graph basicBlocks = program.getBasicBlocks();
        Iterator<Node> iter = basicBlocks.getNodes().iterator();
        while (iter.hasNext()) {
            BasicBlock bb = (BasicBlock) iter.next();
            estimateBasicBlockEnergy(bb, machine, r);
            String bb_str = BBtoString(bb, machine);
            if (bb_str.length() > 0)
                System.out.println(bb_str);
            TiwariResults tr_bb = bb.getTiwariResults();
            nodeResults.add(tr_bb, bb.getExecutions());
            
            // add result to procedure
            CFGNode node = getLastCFGNode(bb);
            Procedure proc = node.getProcedure();
            if (proc != null) {
                TiwariResults tr_proc = proc.getTiwariResults();
                if (tr_proc == null) {
                    tr_proc = new TiwariResults(machine);
                    proc.setTiwariResults(tr_proc);
                }
                tr_proc.add(tr_bb, bb.getExecutions());
            }
        }

        // contributions from bb edges
        TiwariResults edgeResults = new TiwariResults(machine);
        Iterator<Edge> edgeIter = basicBlocks.getEdges().iterator();
        while (edgeIter.hasNext()) {
            BasicBlockEdge edge = (BasicBlockEdge) edgeIter.next();
            estimateBasicBlockEdgeEnergy(edge, machine, r);
            String edge_str = BBEdgetoString(edge);
            if (edge_str.length() > 0)
                System.out.println(edge_str);
            TiwariResults tr_edge = edge.getTiwariResults();
            edgeResults.add(tr_edge, edge.getExecutions());

            // add result to procedure
            BasicBlock b = (BasicBlock)edge.getStart();
            CFGNode node = getLastCFGNode(b);
            Procedure proc = node.getProcedure();
            if (proc != null) {
                TiwariResults tr_proc = proc.getTiwariResults();
                if (tr_proc == null) {
                    tr_proc = new TiwariResults(machine);
                    proc.setTiwariResults(tr_proc);
                }
                tr_proc.add(tr_edge, edge.getExecutions());
            }
        }

        System.out.println("------ Results for s = " + parameterS + " ------");

        TiwariResults programResults = new TiwariResults(machine);
        program.setTiwariResults(programResults);
        programResults.add(nodeResults, 1);
        programResults.add(edgeResults, 1);

        System.out.println("--- CONTRIBUTIONS BY PROCEDURES ---");
        String s = ""; while (s.length() < 20) s += " ";
        System.out.println(s + TiwariResults.getHeaderString());
        Iterator<Procedure> procIter = program.getProcedures().values().iterator();
        while (procIter.hasNext()) {
            Procedure proc = procIter.next();
            String name = proc.getName();
            if (name == null || name.equals("program_entry"))
                continue;
            while (name.length() < 20) name += " ";
            System.out.println(name + proc.getTiwariResults());
        }

        System.out.println("--- CONTRIBUTIONS BY BASIC BLOCKS ---");
        System.out.println("" + TiwariResults.getHeaderString());
        System.out.println("" + nodeResults);
        System.out.println("--- CONTRIBUTIONS BY BASIC BLOCK EDGES ---");
        System.out.println("" + TiwariResults.getHeaderString());
        System.out.println("" + edgeResults);
        System.out.println("--- FINAL TOTAL ENERGY ESTIMATE ---");
        System.out.println("" + TiwariResults.getHeaderString());
        System.out.println("" + program.getTiwariResults());
    }



    /**
     * @return energy estimate for this basic block
     */
    private void estimateBasicBlockEnergy(BasicBlock bb,
                                          Machine machine,
                                          double r)
    {
        List<CFGNode> nodes = bb.getNodes();
        List<String> labels = bb.getLabels();
        String name = bb.getName();
        TiwariResults tr_bb = new TiwariResults(machine);
        bb.setTiwariResults(tr_bb);

        C55xInstruction prev_instr = null;
        // instructions of this basic block
        Iterator<CFGNode> iter = nodes.iterator();
        while (iter.hasNext()) {
            CFGNode node = iter.next();
            Instruction ins = node.getInstruction();
            int cycles = 1;
            double BI = 0.0, OI = 0.0;
            if (ins == null || ins instanceof ProgramEntry || ins instanceof ProgramExit) {
                ; //System.out.println("XXX: 0 base cost for " + ins);
            }
            else {
                C55xInstruction instr = (C55xInstruction)ins;
                cycles = getInstructionCycles(instr);
                BI = getInstructionBaseCurrent(instr,
                                               machine.getClockFrequency(),
                                               r); // base current
                OI = getInstructionOverheadCurrent(prev_instr,
                                                   instr, // overhead I
                                                   machine.getClockFrequency(),
                                                   r);
                prev_instr = instr;
            }

            TiwariResults tr_ins = new TiwariResults(machine);
            ins.setTiwariResults(tr_ins);

            tr_ins.setCycles(cycles);
            tr_ins.setBaseCurrent(BI);
            tr_ins.setOverheadCurrent(OI);
            tr_bb.add(tr_ins, 1);
        }
    }


    private CFGNode getLastCFGNode(BasicBlock bb) {
        List<CFGNode> nodes = bb.getNodes();
        Iterator<CFGNode> iter = nodes.iterator();
        CFGNode node = null;
        while (iter.hasNext())
            node = iter.next();
        return node;
    }

    // 2 following methods could be in some form in BasicBlock.java
    private C55xInstruction getLastInstruction(BasicBlock bb) {
        C55xInstruction start_instr = null;
        List<CFGNode> nodes = bb.getNodes();
        Iterator<CFGNode> iter = nodes.iterator();
        while (iter.hasNext()) {
            CFGNode node = iter.next();
            Instruction ins = node.getInstruction();
            if (ins == null || ins instanceof ProgramEntry || ins instanceof ProgramExit)
                continue;
            start_instr = (C55xInstruction)ins;
        }
        return start_instr;
    }

    private C55xInstruction getFirstInstruction(BasicBlock bb) {
        C55xInstruction end_instr = null;
        List<CFGNode> nodes = bb.getNodes();
        Iterator<CFGNode> iter = nodes.iterator();
        if (iter.hasNext()) {
            CFGNode node = iter.next();
            Instruction ins = node.getInstruction();
            if (!(ins == null || ins instanceof ProgramEntry || ins instanceof ProgramExit))
                end_instr = (C55xInstruction)ins;
        }
        return end_instr;
    }



    private void estimateBasicBlockEdgeEnergy(BasicBlockEdge edge,
                                              Machine machine,
                                              double r)
    {
        BasicBlock bb_start = (BasicBlock)edge.getStart();
        BasicBlock bb_end = (BasicBlock)edge.getEnd();

        C55xInstruction start_instr = getLastInstruction(bb_start);
        C55xInstruction end_instr = getFirstInstruction(bb_end);

        String type;
        boolean branchtaken = false;
        switch (edge.getType()) {
        case Edge.EdgeFallTrough:
            type = "Fall Through";
            break;
        case Edge.EdgeBranchTaken:
            branchtaken = true;
            type = "Branch Taken";
            break;
        case Edge.EdgeCall:
            branchtaken = false;
            type = "Call Edge";
            break;
        case Edge.EdgeReturn:
            branchtaken = false;
            type = "Return Edge";
            break;
        default:
            type = "";
        }
        int cycles = 0;
        double BI = 0.0; // base current
        double OI = 0.0; // overhead current
        cycles = getInstructionCycles(start_instr, branchtaken);

        // cycles = 0 here means that the instruction base current is counted
        // as part of the basic block energy
        if (cycles > 0)
            BI = getInstructionBaseCurrent(start_instr,
                                           machine.getClockFrequency(),
                                           r);
        else {
            //System.out.println("XXX: cycles = " + cycles + " for " + start_instr);
            // cycles = 0: only interested in overhead current
            BI = 0.0;
        }

    
        OI = getInstructionOverheadCurrent(start_instr,
                                           end_instr,
                                           machine.getClockFrequency(),
                                           r);

        TiwariResults tr_edge = new TiwariResults(machine);
        edge.setTiwariResults(tr_edge);
        tr_edge.setCycles(cycles);
        tr_edge.setBaseCurrent(BI);
        tr_edge.setOverheadCurrent(OI);
    }




    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    // return number of cycles within basic block
    private int getInstructionCycles(C55xInstruction instr) {
        int cycles = 0;

        if (instr == null) {
            //System.out.println("XXX: getInstructionCycles returns 1 for null instr");
            return 1; // XXX TODO ????
        }

        for (int i = 0; i < 4; i++) {
            C55xOperation oper = (C55xOperation)instr.getOperation(i);
            if (oper != null) {
                int c = oper.getCycles();
                if (c > cycles) {
                    //System.out.println("cycles for oper \"" + oper.emit() + "\""
                    //                       + " is " + c);
                    cycles = c;
                }
            }
        }
        //System.out.println("cycles for instr \"" + instr.emit() + "\"" + " is " + cycles);
        return cycles;
    }

    // return number of cycles of branch, when taken or not
    private int getInstructionCycles(C55xInstruction instr,
                                     boolean branchtaken) {
        int cycles = 0;

        if (instr == null) {
            //System.out.println("XXX: getInstructionCycles: null instr, returning 0");
            return 0;
        }

        for (int i = 0; i < 4; i++) {
            C55xOperation oper = (C55xOperation)instr.getOperation(i);
            if (oper != null) {
                int c = oper.getCycles(branchtaken);
                if (c > cycles) {
                    /*System.out.println("cycles for oper \"" + oper.emit() + "\""
                      + " is " + c);*/
                    cycles = c;
                }
            }
        }
        //System.out.println("cycles for instr \"" + instr.emit() + "\"" + " is " + cycles);
        return cycles;
    }


    // get base current when executing instruction instr
    // (which might contain many operations)
    private double getInstructionBaseCurrent(C55xInstruction instr,
                                             double clockFrequency,
                                             double r) {
        
        C55xOperation[] oper = new C55xOperation[4];
        double[] I = new double[4];
        boolean MR = false; // some operation has memory access

        if (instr == null) {
            //System.out.println("XXX: getInstructionBaseCurrent: null instr, returning 0");
            return 0.0;
        }

        for (int i = 0; i < 4; i++) {
            oper[i] = (C55xOperation)instr.getOperation(i);
            if (oper[i] != null) {
                I[i] = getOperationBaseCurrent(oper[i], clockFrequency, r);
                if (oper[i].hasMemoryAccess())
                    MR = true;
            }
        }

        //System.out.println("instr: " + instr.emit());
        //System.out.println("boo1: " + I[0] + " " + I[1] + " " + I[2] + " " + I[3]);

        // For implicitly parallel instructions (built-in parallelism),
        // the current consumption was found to be very close to the maximum
        // cost of the two original instructions.
        if (I[0] < I[1])
            I[0] = I[1];
        if (I[2] < I[3])
            I[2] = I[3];
        //System.out.println("boo2: " + I[0] + " " + I[1] + " " + I[2] + " " + I[3]);

        // In addition to base and overhead costs there were measurements for
        // parallel instructions. The measurements were done comparing the
        // maximum base cost of a non-parallel instruction to that of the
        // explicitly parallel instruction pair. For combinations of RR
        // instructions, the overhead of parallelism was found to vary
        // between -0.5 and 1.8 mA, with most occurrences around 1.0 mA.
        // For MR-RR combinations the overhead was between 0.5 and 2.7 mA,
        // with a lot of occurrences around 2.0 mA.
        if (I[0] < I[2])
            I[0] = I[2];

        if (I[2] > 0.0) { // means explicit parallelism
            if (MR)
                I[0] += ((0.5+2.7)/2) * 1e-3 * (clockFrequency / (24 * 1e6)); // MR-RR
            else
                I[0] += ((-0.5+1.8)/2) * 1e-3 * (clockFrequency / (24 * 1e6)); // RR-RR
        }

        //System.out.println("boo3: " + I[0] + " " + I[1] + " " + I[2] + " " + I[3]);
        return I[0];
    }


    // get overhead current when executing instructions instr1, instr2
    // (which might contain many operations)
    private double getInstructionOverheadCurrent(C55xInstruction instr1,
                                                 C55xInstruction instr2,
                                                 double clockFrequency,
                                                 double r) {
        C55xOperation oper1, oper2;
        double maxI = 0.0, I = 0.0, minI = 1e99;
        double sumI = 0.0, avgI = 0.0;
        int n = 0;

        // this is the first instruction in a basic block, we will handle the
        // overhead current using the edges
        if (instr1 == null) {
            //System.out.println("XXX: getInstructionOverheadCurrent: instr1 null, returning 0");
            return 0.0;
        }
        if (instr2 == null) {
            //System.out.println("XXX: getInstructionOverheadCurrent: instr2 null, returning 0");
            return 0.0;
        }

        if (instr1 == instr2) {
            // single repeat loop probably
            //System.out.println("ZZZ: instruction " + instr1
            //+ " and " + instr2 + "have 0 overhead!");
            return 0.0;            
        }

        // parallel instructions
        for (int i = 0; i < 4; i++) {
            // compare each of the positions
            // this will make sure this combination gets 0 overhead:
            // MOV *AR4+ << #16,AC1 || MACR AC0,T0,AC1,AC0
            oper1 = (C55xOperation)instr1.getOperation(i);
            oper2 = (C55xOperation)instr2.getOperation(i);
            if (oper1 != null && oper2 != null) {
                I = getOperationOverheadCurrent(oper1,
                                                oper2,
                                                clockFrequency,
                                                r);
                n++;
                sumI += I;
                if (maxI < I) {
                    maxI = I;
                    //System.out.println("setting maxI to " + maxI);
                }
                if (I < minI) {
                    minI = I;
                    //System.out.println("setting minI to " + minI);
                }
            }
            /*if (oper1 != null) {
                for (int j = 0; j < 4; j++) {
                    oper2 = (C55xOperation)instr2.getOperation(j);
                    if (oper2 != null) {
                        I = getOperationOverheadCurrent(oper1,
                                                        oper2,
                                                        clockFrequency,
                                                        r);
                        n++;
                        sumI += I;
                        if (maxI < I) {
                            maxI = I;
                            //System.out.println("setting maxI to " + maxI);
                        }
                        if (I < minI) {
                            minI = I;
                            //System.out.println("setting minI to " + minI);
                        }
                    }
                }
                }*/

        }
        avgI = sumI / n;
        /*System.out.println("maxI = " + maxI);
        System.out.println("minI = " + minI);
        System.out.println("sumI = " + sumI);
        System.out.println("avgI = " + avgI);*/

        return maxI; // XXX TODO: tweak
    }


    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////

    // get base cost of operation when clock frequency is f
    // r argument must be between 0.0 and 1.0:
    //    0.0 means return minimum value in base cost range
    //    0.5 means return average of min and max values in base cost range
    //    1.0 means return maximum value in base cost range

    // C55x instruction classes according to paper
    // Power Consumption Characterisation of the Texas Instruments
    // TMS320VC5510 DSP, Table 2
    // currents measured at 24 MHz
    private double getOperationBaseCurrent(C55xOperation operation,
                                           double clockFrequency,
                                           double r) {
        int instr_class = operation.getInstructionClass();
        double I = 0.0, maxI = 0.0, minI = 0.0;

        if (instr_class == C55xOperation.ArithmeticalOp) {
            if (operation.hasMemoryAccess()) { // MR
                minI = 15.5;
                maxI = 21.1;
            }
            else { // RR
                minI = 13.4;
                maxI = 15.5;
            }
        }
        else if (instr_class == C55xOperation.BitOp) {
            if (operation.hasMemoryAccess()) { // MR
                minI = 16.4;
                maxI = 18.1;
            }
            else { // RR
                minI = 14.2;
                maxI = 16.1;
            }
        }
        else if (instr_class == C55xOperation.LogicalOp) {
            if (operation.hasMemoryAccess()) { // MR
                minI = 16.9;
                maxI = 19.7;
            }
            else { // RR
                minI = 13.4;
                maxI = 15.7;
            }
        }
        else if (instr_class == C55xOperation.MoveOp) {
            if (operation.hasMemoryAccess()) { // MR
                minI = 15.1;
                maxI = 20.0;
            }
            else { // RR
                minI = 13.5;
                maxI = 15.7;
            }
        }
        else {
            //System.out.println("operation " + operation + " not assigned to any instr class");
            minI = 15.0;
            maxI = 15.0; // unknown instruction class
            throw new NullPointerException("Unknown instruction class for oper " + operation);
        }

        I = minI + r * (maxI - minI);

        //I = 15;
        I *= 1e-3; // values above are milliamperes
        I *= (clockFrequency / (24 * 1e6)); // measurements were done at 24 Mhz
        //System.out.println("I is " + I);

        return I;
    }

    // C55x instruction classes according to paper
    // Power Consumption Characterisation of the Texas Instruments
    // TMS320VC5510 DSP, Table 3
    // currents measured at 24 MHz
    private double getOperationOverheadCurrent(C55xOperation operation1,
                                               C55xOperation operation2,
                                               double clockFrequency, double r) {
        double I = 0.0, maxI = 0.0, minI = 0.0;
        boolean swap = false;

        // this is the first instruction in a basic block, we will handle the
        // overhead current using the edges
        if (operation1 == null)
            return 0.0;
        if (operation1.getPage() == operation2.getPage()) {
            // same instruction: 0 overhead
            //System.out.println("ZZZ: operations " + operation1
            //+ " and " + operation2 + "have 0 overhead!");
            return 0.0;
        }

        int class1 = operation1.getInstructionClass();
        int class2 = operation2.getInstructionClass();

        // the table is symmetric, swap operations in certain cases
        if (class1 == C55xOperation.BitOp
            && class2 == C55xOperation.ArithmeticalOp)
            swap = true;
        if (class1 == C55xOperation.LogicalOp
            && (class2 == C55xOperation.ArithmeticalOp
                || class2 == C55xOperation.BitOp))
            swap = true;
        if (class1 == C55xOperation.MoveOp
            && (class2 == C55xOperation.ArithmeticalOp
                || class2 == C55xOperation.BitOp
                || class2 == C55xOperation.LogicalOp))
            swap = true;
        if (swap) {
            C55xOperation tmpop = operation2;
            operation2 = operation1;
            operation1 = tmpop;
            int tmpclass = class2;
            class2 = class1;
            class1 = tmpclass;
        }

        // if there's a memory access in either of the operations,
        // use the higher MR overhead costs
        if (operation1.hasMemoryAccess() || operation2.hasMemoryAccess()) {
            if (class1 == C55xOperation.ArithmeticalOp) {
                if (class2 == C55xOperation.ArithmeticalOp) {
                    minI = 3.0;
                    maxI = 3.6;
                }
                else if (class2 == C55xOperation.BitOp) {
                    minI = 3.0;
                    maxI = 5.0;
                }
                else if (class2 == C55xOperation.LogicalOp) {
                    minI = 3.0;
                    maxI = 4.0;
                }
                else if (class2 == C55xOperation.MoveOp) {
                    minI = 3.5;
                    maxI = 4.0;
                }
            }
            else if (class1 == C55xOperation.BitOp) {
                if (class2 == C55xOperation.BitOp) {
                    minI = 3.8;
                    maxI = 4.2;
                }
                else if (class2 == C55xOperation.LogicalOp) {
                    minI = 3.0;
                    maxI = 5.0;
                }
                else if (class2 == C55xOperation.MoveOp) {
                    minI = 3.5;
                    maxI = 5.0;
                }
                else {
                    throw new NullPointerException("C55xEnergyAnalysis.java: "+
                                                   "getOperationOverheadCurrent Error 1");
                }
            }
            else if (class1 == C55xOperation.LogicalOp) {
                if (class2 == C55xOperation.LogicalOp) {
                    minI = 3.0;
                    maxI = 4.0;
                }
                else if (class2 == C55xOperation.MoveOp) {
                    minI = 3.5;
                    maxI = 4.0;
                }
                else {
                    throw new NullPointerException("C55xEnergyAnalysis.java: "+
                                                   "getOperationOverheadCurrent Error 2");
                }
            }
            else if (class1 == C55xOperation.MoveOp) {
                if (class2 == C55xOperation.MoveOp) {
                    minI = 3.4;
                    maxI = 3.4;
                }
                else {
                    throw new NullPointerException("C55xEnergyAnalysis.java: "+
                                                   "getOperationOverheadCurrent Error 3");
                }
            }
            else {
                throw new NullPointerException("C55xEnergyAnalysis.java: "+
                                               "getOperationOverheadCurrent Error 4");
            }
        }
        else { // both are RR instructions
            if (class1 == C55xOperation.ArithmeticalOp) {
                if (class2 == C55xOperation.ArithmeticalOp) {
                    minI = 1.1;
                    maxI = 1.1;
                }
                else if (class2 == C55xOperation.BitOp) {
                    minI = 1.0;
                    maxI = 2.0;
                }
                else if (class2 == C55xOperation.LogicalOp) {
                    minI = 1.0;
                    maxI = 2.0;
                }
                else if (class2 == C55xOperation.MoveOp) {
                    minI = 1.5;
                    maxI = 2.5;
                }
            }
            else if (class1 == C55xOperation.BitOp) {
                if (class2 == C55xOperation.BitOp) {
                    minI = 1.0;
                    maxI = 1.1;
                }
                else if (class2 == C55xOperation.LogicalOp) {
                    minI = 1.0;
                    maxI = 2.0;
                }
                else if (class2 == C55xOperation.MoveOp) {
                    minI = 1.5;
                    maxI = 2.3;
                }
                else {
                    throw new NullPointerException("C55xEnergyAnalysis.java: "+
                                                   "getOperationOverheadCurrent Error 5");
                }
            }
            else if (class1 == C55xOperation.LogicalOp) {
                if (class2 == C55xOperation.LogicalOp) {
                    minI = 1.0;
                    maxI = 1.1;
                }
                else if (class2 == C55xOperation.MoveOp) {
                    minI = 1.0;
                    maxI = 2.5;
                }
                else {
                    throw new NullPointerException("C55xEnergyAnalysis.java: "+
                                                   "getOperationOverheadCurrent Error 6");
                }
            }
            else if (class1 == C55xOperation.MoveOp) {
                if (class2 == C55xOperation.MoveOp) {
                    minI = 1.0;
                    maxI = 1.0;
                }
                else {
                    throw new NullPointerException("C55xEnergyAnalysis.java: "+
                                                   "getOperationOverheadCurrent Error 7");
                }
            }
            else {
                throw new NullPointerException("C55xEnergyAnalysis.java: "+
                                               "getOperationOverheadCurrent Error 8");
            }
        }

        I = minI + r * (maxI - minI);
        I *= 1e-3; // values above are milliamperes
        I *= (clockFrequency / (24 * 1e6)); // measurements were done at 24MHz
        //System.out.println("I is " + I);

        return I;
    }

    //////////////////////////////////////////////////////////////////////
    // TO STRING STUFF BELOW
    //////////////////////////////////////////////////////////////////////



    /**
     * @return contents of this basic block as a String
     */
    private String BBtoString(BasicBlock bb, Machine machine) {
        List<String> labels = bb.getLabels();
        List<CFGNode> nodes = bb.getNodes();
        String result = new String(), s1;
        Iterator iter;
        TiwariResults tr_bb_total = new TiwariResults(machine);

        s1 = ""; while (s1.length() < 120) s1 += "="; result += s1 + "\n";
        
        result += "Basic Block " + bb.getName()
            + " (num executions: " + bb.getExecutions() + ")\n";

        String type;
        switch (bb.getType()) {
        case BasicBlock.PROCEDURE_ENTRY: type = "PROCEDURE ENTRY"; break;
        case BasicBlock.PROCEDURE_EXIT:  type = "PROCEDURE EXIT";  break;
        default: type = "PROCEDURE INTERNAL";                           break;
        }
        result += "BB TYPE: " + type + "\n";
        if (labels.size() > 0) {
            result += "LABELS : ";
            iter = labels.iterator();
            while (iter.hasNext()) {
                result += ((String) iter.next()) + ":, ";
            }
            result += "\n";
        }

        result += "NOTE: the 1st instruction has 0 overhead, because it can have several\n";
        result += "previous instructions. The overhead is calculated using the edges.\n";

        // instructions of this basic block
        String s = "INSTRUCTION";
        int len = 40;
        while (s.length() < len)
            s += " ";
        s += TiwariResults.getHeaderString();
        result += s + "\n";
        s1 = ""; while (s1.length() < 120) s1 += "-";
        result += s1 + "\n";

        // loop over all instructions
        iter = nodes.iterator();
        while (iter.hasNext()) {
            CFGNode node = (CFGNode) iter.next();
            if (node.getInstruction() == null) {
                continue;
            }
            Procedure proc = node.getProcedure();
            Instruction instr = node.getInstruction();
            if (instr == null)
                continue;

            tr_bb_total.add(instr.getTiwariResults(), 1); // add up all results

            s = instr.emit();
            while (s.length() < len)
                s += " ";
            s += "" + instr.getTiwariResults();
            result += s;
            //if (proc != null) {
            //        result += "(" + proc.getName() + ")";
            //}
            //if (node.getPreState() != null) {
            //        result += "PRESTATE[" + node.getPreState().toString() + "]";
            //}
            result += "\n";
        }

        s1 = ""; while (s1.length() < 120) s1 += "-"; result += s1 + "\n";

        s = "BB TOTAL: ";
        while (s.length() < len)
            s += " ";
        s += "" + tr_bb_total;
        result += s + "\n";

        s1 = ""; while (s1.length() < 120) s1 += "=";
        result += s1 + "\n\n";

        return result;
    }



    private String BBEdgetoString(BasicBlockEdge edge) {
        //List<CFGNode> nodes = edge.getNodes();
        String result = new String();
        String s1;

        s1 = ""; while (s1.length() < 120) s1 += "<"; result += s1 + "\n";

        result += "Basic block edge " + edge + "\n";

        String type;
        switch (edge.getType()) {
        case Edge.EdgeFallTrough:  type = "Fall Through"; break;
        case Edge.EdgeBranchTaken: type = "Branch Taken"; break;
        case Edge.EdgeCall:        type = "Call Edge";    break;
        case Edge.EdgeReturn:      type = "Return Edge";  break;
        default:                   type = "";             break;
        }

        result += "BB EDGE TYPE: " + type + "\n";
    
        // instructions of this basic block
        BasicBlock bb_start = (BasicBlock)edge.getStart();
        BasicBlock bb_end = (BasicBlock)edge.getEnd();

        C55xInstruction start_instr = getLastInstruction(bb_start);
        C55xInstruction end_instr = getFirstInstruction(bb_end);

        //result += "edge        : " + edge + "\n";
        //result += "start_instr : " + start_instr + "\n";
        //result += "end_instr   : " + end_instr + "\n";

        C55xInstruction instr = start_instr;

        String s = "INSTRUCTIONS AND EDGE";
        int len = 40;
        while (s.length() < len) s += " ";
        s += TiwariResults.getHeaderString();
        result += s + "\n";
        s1 = ""; while (s1.length() < 120) s1 += "-"; result += s1 + "\n";

        if (start_instr != null) {
            s = "" + start_instr.emit();
            while (s.length() < len)
                s += " ";
            s += start_instr.getTiwariResults();
            result += s + "\n";
        }
        else
            result += "null start instr\n";

        if (end_instr != null) {
            s = "" + end_instr.emit();
            while (s.length() < len)
                s += " ";
            s += end_instr.getTiwariResults();
            result += s + "\n";
        }
        else
            result += "null start instr\n";

        s1 = ""; while (s1.length() < 120) s1 += "-"; result += s1 + "\n";
        s = "edge results:";
        while (s.length() < len)
            s += " ";
        s += edge.getTiwariResults();
        result += s + "\n";

        s1 = ""; while (s1.length() < 120) s1 += ">"; result += s1;
        result += "\n\n\n";

        return result;
    }

}

