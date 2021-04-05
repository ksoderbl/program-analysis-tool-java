/**
 * CFGAnalysis.java
 */

package cfg;

import analysis.Analysis;
import machine.Machine;
import program.BranchTarget;
import program.Label;
import program.Program;
import program.Procedure;
import program.Symbols;
import input.*;
import pseudoOp.PseudoOp;
import instr.*;
import main.*;
import graph.Edge;
import graph.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Control flow graph analysis.
 *
 * @author Juha Tukkinen
 * @author Mikko Reinikainen
 * @author Kristian Söderblom
 * @see Analysis
 */

public class CFGAnalysis implements Analysis
{
    /**
     * Performs control flow graph analysis of the program
     *
     * @param program the program
     * @param machine machine on which the program runs
     */
    public void analyze(Program program, Machine machine) {
        CFG cfg = program.getCFG();

        Instruction instr1 = new ProgramEntry(program.getEntryPoint());
        Instruction instr2 = new ProgramExit();

        //Label programEntryLabel = new Label(instr1.getCode());
        //Label programExitLabel = new Label(instr2.getCode());

        Long programEntryAddress = instr1.getAddr();
        //System.out.println("program entry addr = " + programEntryAddress);
        String programEntryName = instr1.getCode();

        CFGNode programEntryNode = cfg.createNode(instr1.getCode(), instr1);
        CFGNode programExitNode  = cfg.createNode(instr2.getCode(), instr2);

        Procedure entryProcedure = program.createProcedure(programEntryAddress,
                                                           programEntryName,
                                                           programEntryNode);

        programEntryNode.getInstruction().setNext(programExitNode);

        program.addSymbol(programEntryName,
                          instr1.getAddr(),
                          programEntryNode);

        // convert labels to actual addresses (tukkinen.ps - 4.1.2)
        convertLabels(program, machine);

        // map nodes to procedures starting from "main" (tukkinen.ps - 4.1.3)
        analyzeProcedure(program, machine, programEntryAddress, programEntryName, null);

        // add return targets (tukkinen.ps - 4.1.4)
        retTargets(program, machine);
    }

    /**
     * Converts labels to respective absolute memory addresses
     * kps - This doesn't convert any labels, it only makes some
     * new cfg edges and sets the corresponding targets in the
     * branch instructions.
     */
    private void convertLabels(Program program, Machine machine) {
        CFG cfg = program.getCFG();
        Symbols labels = program.getLabels();
        Iterator<Node> iter = cfg.getNodes().iterator();

        while (iter.hasNext()) {
            CFGNode node = (CFGNode) iter.next();
            Instruction instr = node.getInstruction();

            if (instr == null)
                continue;

            // Create edge between branch node and target.
            // Set target in branch object.
            if (instr.isBranch()) {
                Instruction branch = instr;

                //String targetLabelName = branch.getTargetLabel();
                //Label targetLabel = program.getBranchTargetLabel(branch);
                //CFGNode target = (CFGNode) program.getNode(targetLabel);
                CFGNode target = program.getBranchTargetNode(instr);

                if (target == null) {
                    Main.warn("CFGAnalysis.convertLabels: Branch " + branch
                              + " jumps to undefined target");
                } else {
                    //Main.info("JUMP EDGE FROM " + node + " TO " + target);
                    int type = Edge.EdgeBranchTaken;
                    if (instr.isCall())
                        type = Edge.EdgeCall;
                    cfg.createEdge(node, target, type);

                    // set target address and node of the branch
                    branch.setTarget(target);
                }
            }
        }
    }

    /**
     * Analyzes which procedure each control flow graph node belongs to.
     */
    private void analyzeProcedure(Program program,
                                  Machine machine,
                                  Long address,
                                  String procname,
                                  Procedure caller) {
        //Main.info("analyzing procedure " + procname);
        CFGNode entry = program.getNode(address);

        if (entry == null) {
            Main.warn("CFGAnalysis.analyzeProcedure: no procedure with entry " + procname);
        } else {
            Procedure proc = program.getProcedure(address);

            if (proc == null) {
                //Main.info("creating procedure "
                //                          + procname + " with entry " + entry);
                proc = program.createProcedure(address, procname, entry);
            }
            if (caller != null) {
                //Main.info("caller is " + caller);
                caller.addCall(proc);
            }
            markProcedure(program, machine, proc, entry);
        }
    }

    private void markProcedure(Program program,
                               Machine machine,
                               Procedure proc,
                               CFGNode node) {
        CFG cfg = program.getCFG();

        //    System.out.println("XXXX markProcedure: "
        // + node + " " + proc);

        if (node == null) {
            Main.warn("CFGAnalysis.markProcedure: encountered a null node while "
                               + "marking procedure " + proc);
            return;
        }
        //    Main.info("marking node " + node
        // + " as belonging to procedure " + proc);
        if (node.getProcedure() != null) {
            // do not mark nodes that are already marked
            return;
        }

        node.setProcedure(proc);

        Instruction instr = node.getInstruction();

        // calculate procedure sizes
        proc.addByteSize(instr.getSize());
        //    System.out.println("checking instr " + instr);

        if (instr == null)
            Main.warn("CFGAnalysis.markProcedure: instr == null");

        if (instr != null) {

            if (instr.isCall()) {
                Instruction call = instr;
                // mark the called procedure
                Long targetAddress = program.getBranchTargetAddress(call);
                String targetName = program.getBranchTargetName(call);

                analyzeProcedure(program, machine, targetAddress, targetName, proc);

                // skip over the procedure call to the next memory
                // in instruction
                markProcedure(program, machine, proc, call.getNext());

            } else if (instr.isReturn()) {

                // System.out.println("instruction " + instr + " is a return");
                // System.out.println("  and jumps to "
                // + node.getOutgoingEdges());

                // mark this node as a return node of procedure proc
                proc.addExit(node);

                if (!instr.isCondReturn()) {
                    // this is an unconditional return, remove all
                    // outgoing edges
                    node.removeOutgoingEdges(cfg);
                }
                // this is a conditional return ?
                markSuccessors(program, machine, proc, node);
            } else {
                markSuccessors(program, machine, proc, node);
            }
        }
    }

    private void markSuccessors(Program program,
                                Machine machine,
                                Procedure proc,
                                CFGNode node) {
        // an instruction that stays inside this procedure
        // iterate through successors of this node
        Iterator<Edge> iter = node.getOutgoingEdges().iterator();

        while (iter.hasNext()) {
            CFGEdge edge = (CFGEdge) iter.next();

            if (edge != null) {
                CFGNode next = (CFGNode) edge.getEnd();

                markProcedure(program, machine, proc, next);
            }
        }
    }


    /**
     * Adds edges from ret instructions to ret targets.
     */
    private void retTargets(Program program, Machine machine) {
        CFG cfg = program.getCFG();
        Iterator<Node> iter = cfg.getNodes().iterator();

        // iterate through all nodes of the cfg
        while (iter.hasNext()) {
            CFGNode node = (CFGNode) iter.next();

            if (node.getInstruction().isCall()) {
                Instruction call = node.getInstruction();
                CFGNode nextNode = call.getNext();
                Long targetAddress = program.getBranchTargetAddress(call);
                Procedure proc = program.getProcedure(targetAddress);

                if (proc == null)
                    continue;

                List<CFGNode> procExits = proc.getExits();

                if (procExits == null)
                    continue;

                Iterator<CFGNode> procExitsIter = procExits.iterator();

                while (procExitsIter.hasNext()) {
                    CFGNode retNode = procExitsIter.next();
                    cfg.createEdge(retNode, nextNode, Edge.EdgeReturn);
                }
            }

        }
    }





}
