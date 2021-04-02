package simulation;

import java.util.*;
import java.io.*;

import main.UserOptions;
import program.*;
import machine.*;
import cfg.CFG;
import cfg.CFGNode;
import cfg.CFGEdge;
import analysis.Analysis;
import basicblocks.*;
import graph.*;
import instr.*;
import input.Input;
import pseudoOp.*;
import c55x.dis.*;
import microinstr.Microinstruction;


/**
 * Simulation analysis
 *
 * @author Peter Majorin
 * @see Analysis
 */

public class SimulationRegressionAnalysis implements Analysis {


    /**
     * Performs a dynamic simulation analysis of the program.
     *
     * @param program the program
     * @param machine machine on which the program runs
     */
    public void analyze(Program program, Machine machine) {
        System.out.println("Running simulation for "+machine.getArch());
        SimulationStatistics ss = new SimulationStatistics(program, machine);
        program.setSimulationStatistics(ss);

        makeInstructionHashTable(program, machine);
        simulator(program, machine);
        analyzeResults(program, machine);
        /*        machine.getRegisters().printRegisters();
        ss.print();
        System.out.println(ss.getInstrFetches(machine.getMemoryBank(machine.getInstructionBankNum())));
        */
        return;        
    }

    

    public void analyzeResults(Program program, Machine machine){
        
        // starting address for an array if used in test programs
        
        int DATASTART = 0x7000;

        
        UserOptions options = program.getOptions();
        Registers registers = machine.getRegisters();
        String testcase = options.getSimulationRegressionTestcase();
        
        if (testcase.equals("call1")){
            Register reg = registers.getRegister("t0");
            if (reg.getValue() == 0x8e)
                System.out.println(testcase+" PASSED");
            else
                System.out.println(testcase+" FAIL");
        }
        
        else if (testcase.equals("call2")){
            Register reg = registers.getRegister("t0");
            if (reg.getValue() == 0x456)
                System.out.println(testcase+" PASSED");
            else
                System.out.println(testcase+" FAIL");
        }
        
        else if (testcase.equals("array1")){
            Register reg = registers.getRegister("t0");
            if (reg.getValue() == 0xf)
                System.out.println(testcase+" PASSED");
            else
                System.out.println(testcase+" FAIL");
        }
        
        
        else if (testcase.equals("mac")){
            int test[] = {6, 0x14, 0x2c, 0x50, 0x82};
            MemoryBank mb = machine.getMemoryBank(machine.getDataBankNum());

            long add = mb.readMemDebug(DATASTART);
            
    
            for (int i = 0; i < 5; i++){
                long value = mb.readMemDebug(add+i);
                //                     System.out.println(Long.toHexString(value));

                if (test[i] == value) ;
                else {
                    System.out.println(testcase+" FAILED:"+value);
                    return;
                }
                
            }
            
            System.out.println(testcase+" PASSED");
        }
        
        else if (testcase.equals("qsort")){
            short test[] = {-11, -2, 0, 1, 4, 7, 9, 12, 15, 20480};

            MemoryBank mb = machine.getMemoryBank(machine.getDataBankNum());
            
            long add = mb.readMemDebug(DATASTART);
            
            
            for (int i = 0; i < 10; i++){
                short value = (short)mb.readMemDebug(add+i);
                //                     System.out.println(Long.toHexString(value));
                
                if (test[i] == value) ;
                else {
                    System.out.println(testcase+" FAILED, at "+i+" "+value+" should be:"+test[i]);
                    return;
                }
                
            }

            System.out.println(testcase+" PASSED");
        }
        
        else {
            System.out.println("no testcase for given arg");
        }
        
        
        
    }
    
    public void simulator(Program program, Machine machine){
        
        String inputString = null;
        MemoryBank mB = machine.getMemoryBank(machine.getInstructionBankNum());
        machine.setSimulationRunning(true);        
        int inPos = 0;
        //  open stdin
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String prompt = machine.getArch();
        SimulationStatistics simulationStatistics = program.getSimulationStatistics();
        // init stack, memory, registers and program start address
        machine.reset(program);

        while (true){
            inPos++;
            machine.setNextPCAddr(machine.getNextInstructionAddr());
            Instruction i = mB.readInstruction(machine.getPCAddr());
            //System.out.println(i.toString());
            boolean repeats = machine.handleRepeats();
            executeMicroinstructions(i, program, machine);
            if (!machine.simulationRunning()) break;
            machine.setPCAddr(machine.getNextPCAddr());
            addEdgeExecution(program, i, mB.readInstruction(machine.getPCAddr()));
        }
    }
    

    public static void addEdgeExecution(Program program, Instruction start, Instruction end){
        CFG cfg = program.getCFG();
        CFGNode startNode = program.getNode(start.getAddr());
        CFGNode endNode = program.getNode(end.getAddr());
        CFGEdge edge = (CFGEdge)cfg.findEdge(startNode, endNode);
        if (edge == null) return;
        else edge.addExecutions();
    }


    
    public static void executeMicroinstructions(Instruction instr, Program program, Machine machine){
        SimulationStatistics simulationStatistics = program.getSimulationStatistics();
        List list = instr.getOperations();
        Iterator iter = list.iterator();

        instr.addExecutions();
        while (iter.hasNext()) {
            Operation op = (Operation)iter.next();
            if (op == null)
                continue;
            ArrayList micros = (ArrayList)op.getMicroinstrs(machine);
            simulationStatistics.addExecutedInstrs();
        }
        
               
        ArrayList parallel = (ArrayList)instr.getParallelMicroinstrs(machine);
        
        
        for (int i = 0; i < parallel.size(); i++){
            Microinstruction mi = (Microinstruction)parallel.get(i);
            machine.setMachineResultOffset(mi.getResultOffset());
            mi.execute(program, machine);
        }        
        
        
    }


    public void makeInstructionHashTable(Program program, Machine machine){
        List inputs = program.getInputs();
        Iterator iter = inputs.iterator();
        while (iter.hasNext()){
            Input input = (Input)iter.next();
            List inputLines = input.getInputLines();
            Iterator iter2 = inputLines.iterator();
            while (iter2.hasNext()){
                Object o = (Object)iter2.next();
                //                System.out.println(o.toString());

                if (o instanceof C55xDisSectionPseudoOp){
                    C55xDisSectionPseudoOp c55xPop = (C55xDisSectionPseudoOp)o;
                    if (c55xPop.getSectionType().equals("TEXT")){
                        machine.setPCAddr((c55xPop.getAddr()).longValue());
                        machine.setProgramStartAdd(c55xPop.getAddr().longValue());
                    }
                }

                if (o instanceof C55xDisDataPseudoOp){

                    C55xDisDataPseudoOp c55xPop = (C55xDisDataPseudoOp)o;
                    Long addressValue = c55xPop.getAddr();
                    machine.writeMem(addressValue.longValue(), c55xPop.getDataValue(), 
                                     machine.getDataBankNum());
                    
                }


                if (o instanceof Instruction){
                    machine.addInstruction(((Instruction)o), machine.getInstructionBankNum());
                }
                
            }
        }
    }
    
}
