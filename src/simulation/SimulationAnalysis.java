package simulation;

import java.util.*;
import java.io.*;

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
import microinstr.BitUtils;
import c55x.instr.C55xOperation;
import c55x.C55xMachine;
import input.InputLineObject;

/**
 * Simulation analysis
 *
 * @author Peter Majorin
 * @see Analysis
 */

public class SimulationAnalysis implements Analysis {

    private static final String BP   = "bp";
    private static final String START = "start";
    private static final String HELP = "help";
    private static final String IGNOREBP   = "ignorebp";
    private static final String LOOP = "loop";
    private static final String MEM  = "mem";
    private static final String QUIT = "quit";
    private static final String RUN  = "run";
    private static final String REGS = "regs";
    private static final String REGSBIN = "regsbin";
    private static final String RESET = "reset";
    private static final String SETREG = "setreg";
    private static final String STAT = "stat";
    private static final String WRITEREG = "writereg";
    private static final String WRITEMEM = "writemem";
    private static final String SP   = "sp";
    private static final String PC   = "pc";
    
    private boolean ignoreBP = false;

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
        //        makeParallel(program,machine);
        simulator(program, machine);
        System.out.println("Simulation terminated.");
        machine.getRegisters().printRegisters();
        ss.print();
        return;        
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

        try {
            String arg1, arg2, arg3;

            System.out.print(prompt+"-"+inPos+"> ");

            while (!(QUIT.equals(inputString = br.readLine()))){
                        
                StringTokenizer st = new StringTokenizer(inputString," ");
                arg1 = null; arg2 = null; arg3 = null;

                if (st.countTokens() == 3){
                    arg1 = st.nextToken();
                    arg2 = st.nextToken();
                    arg3 = st.nextToken();
                }
                else if (st.countTokens() == 2){
                    arg1 = st.nextToken();
                    arg2 = st.nextToken();
                }
                
                else if (st.countTokens() == 1){
                    arg1 = st.nextToken();
                }
                
                else if (st.countTokens() == 0){
                    arg1 = "";
                }
        
                if (arg1.equals("start")){
                    // do not use debug mode when running a program straight 
                    // through even if set
                    boolean debugMC = program.getOptions().getDebugMC();
                    program.getOptions().setDebugMC(false);
                    while (machine.simulationRunning()){ 
                        machine.setNextPCAddr(machine.getNextInstructionAddr());
                        if (machine.isBreakpoint(machine.getPCAddr()) && (!ignoreBP)){
                            System.out.println("Program stopped: breakpoint reached at 0x"+
                                               Long.toHexString(machine.getPCAddr()));
                            program.getOptions().setDebugMC(debugMC);
                            break;
                        }
                        
                        Instruction i = mB.readInstruction(machine.getPCAddr());
                        boolean repeats = machine.handleRepeats();
                        
                        executeMicroinstructions(i, program, machine);
                        if (!machine.simulationRunning()) break;
                        machine.setPCAddr(machine.getNextPCAddr());
                            addEdgeExecution(program, i, mB.readInstruction(machine.getPCAddr()));
                    }
                    
                    machine.getRegisters().printRegisters();
                }
                
                     // step through code
                else if (arg1.equals("") && machine.simulationRunning()){
                    
                    inPos++;
                    machine.setNextPCAddr(machine.getNextInstructionAddr());
                    Instruction i = mB.readInstruction(machine.getPCAddr());
                    CFGNode node = program.getNode(new Long(machine.getPCAddr()));
                    String foo = i.toString();
                    if (node != null && node.getProcedure() != null)
                        foo += " (in proc. " + node.getProcedure().getName() + ")";
                    System.out.println(foo);
                    boolean repeats = machine.handleRepeats();
                    executeMicroinstructions(i, program, machine);
                    if (!machine.simulationRunning()) break;
                    machine.setPCAddr(machine.getNextPCAddr());
                    addEdgeExecution(program, i, mB.readInstruction(machine.getPCAddr()));
                }

                else if (arg1.equals(STAT)){
                        ((C55xMachine)machine).printStatusRegs();
                }
                
                else if (arg1.equals(REGS)){
                    if (arg2 == null) {
                        machine.getRegisters().printRegisters();
                        System.out.println("Microinstr temporaries:");
                        machine.printDataResult();
                    }
                    else {
                        try {
                            Register reg = machine.getRegister(arg2);
                            System.out.println(reg.getName()+"="
                                               +BitUtils.valueToString(reg.getValue()));
                        }
                        catch (Exception e) {
                            System.out.println("No register \"" + arg2 + "\"");
                        }
                    }
                }
                
                else if (arg1.equals(REGSBIN)){
                    machine.getRegisters().printRegistersBinary();
                }


                else if (arg1.equals(HELP)){
                    System.out.println("bp:            show breakpoint(s)");
                    System.out.println("continue:      continue execution until next bp");
                    System.out.println("ignorebp:      clear all breakpoints");
                    System.out.println("mem [address,ALL]: prints the values given in a memory location");
                    System.out.println("quit:          exit simulation");
                    System.out.println("setreg:               set registervalue");
                    System.out.println("stat:               view set bits in status registers st0-st3");
                    System.out.println("regs:          show CPU register values");
                    System.out.println("regsbin:       show CPU registers in binary values");
                    System.out.println("reset:         reset execution of program");
                    System.out.println("return:        single step through code");
                    System.out.println("start:         run the program");
                    System.out.println("writemem:      write a value into data memory");
                }
                
                else if (arg1.equals(RESET)){
                    machine.reset(program);
                    //System.out.println(Long.toHexString(machine.getProgramStartAdd()));
                    System.out.println("*** the program execution has been reset");
                }
                
                else if (arg1.equals(MEM)){
                    if (arg2 == null) machine.printChangesMemory();
                    else {
                        MemoryBank mb = machine.getMemoryBank(machine.getDataBankNum());
                        System.out.println("Gives only changes done in memory after starting program");
                        if (!(arg2.startsWith("0x") || arg2.startsWith("0X")))
                            arg2 = "0x" + arg2;
                        try {
                            long value = mb.readMem(Long.decode(arg2));
                            System.out.println("0x"+Long.toHexString(value)
                                               +" ("+BitUtils.extendSigned(value,16,64)+")"
                                               +" Q.15:"+BitUtils.q15ToString(value));
                        }
                        catch (Exception e) {
                            System.out.println("mem: Use 0x or no prefix for hexadecimal numbers");
                        }
                    }
                }
                
                else if (arg1.equals(SP)){
                    Register sp = machine.getRegister(SP);
                    System.out.println("sp = 0x"+Long.toHexString(sp.getValue()));
                }

                else if (arg1.equals(IGNOREBP)){
                    ignoreBP = true;
                    System.out.println("all breakpoints ignored");
                }

                else if (arg1.equals(BP)){
                    if (arg2 == null)
                        machine.printBreakpoints();
                    else {        
                        try {
                            long bp_addr = 0;
                            if (arg2.startsWith("_")) { // bp at named routine
                                bp_addr = program.getAddress(arg2).longValue();
                            }
                            else {
                                if (!(arg2.startsWith("0x") || arg2.startsWith("0X")))
                                    arg2 = "0x" + arg2;
                                bp_addr = Long.decode(arg2);
                            }
                            machine.addBreakpoint(bp_addr);
                        }
                        catch (Exception e){
                            System.out.println("bp: give a hexvalue (aa or 0xaa) or routine name");
                        }
                    }
                }

                else if (arg1.equals("loop")){
                  System.out.println("Number of active loops:"+machine.getNumLoopCounters());
                }

                else if (arg1.equals("setreg") || arg1.equals("sr")){
                    if ((arg2 == null) || (arg3 == null)){
                        System.out.println("setreg: give regname number");
                    }
                    else {
                        try{
                            Register reg = machine.getRegister(arg2);
                            reg.setValue(Long.decode(arg3));
                        }
                        catch (Exception e){
                            System.out.println("setreg: give a decimal or hexvalue (333 or 0x333)"
                                               +", or wrong register name given");        
                        }
                    }
                }

                else if (arg1.equals("writemem") || arg1.equals("wm")){
                  if ((arg2 == null) || (arg3 == null))  System.out.println("writemem: give writemem address value");
                  else {
                   try{
                     if (!(arg2.startsWith("0x") || arg2.startsWith("0X")))
                        arg2 = "0x" + arg2;
                     if (!(arg3.startsWith("0x") || arg3.startsWith("0X")))
                        arg3 = "0x" + arg3;
                         machine.writeMem(Long.decode(arg2), Long.decode(arg3), machine.getDataBankNum());
                 
                   }
                    catch (Exception e){
                        System.out.println("writemem: give a hexvalue (333 or 0x333) as address and value");
                    }

                  }
                }

                else if (arg1.equals("") && !machine.simulationRunning()){
                    break;
                }
                
                else System.out.println("Unknown command: " + inputString);
                
                System.out.print(prompt+"-"+inPos+"> ");
            }
            
        } catch (Exception e) {
            e.printStackTrace();                    
            System.out.println("returning from simulation..");
            return;
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


    public static long parseLong(String number){
        
        try {
            long i = Long.parseLong(number.trim());
            return i;
        } 
        catch (NumberFormatException nfe) {
            System.out.println("NumberFormatException: " + nfe.getMessage());
            return 0;
        }
        
    }

    public static void executeMicroinstructions(Instruction instr, Program program, Machine machine){
        SimulationStatistics simulationStatistics = program.getSimulationStatistics();
        List<Operation> list = instr.getOperations();
        Iterator<Operation> iter = list.iterator();
        boolean skipped = false;
        instr.addExecutions();
        simulationStatistics.addExecutedInstrs();
        
        // calculate operations statistics
        while (iter.hasNext()) {
            Operation op = iter.next();
            if (op == null)
                continue;
            if (program.getOptions().getDebugMC())
                System.out.println(" page:"+((C55xOperation)op).getPageNumber());
            //ArrayList micros = (ArrayList)op.getMicroinstrs(machine);
            simulationStatistics.addExecutedOperations();
            //for (int i = 0; i < micros.size(); i++){
            //           Microinstruction mi = (Microinstruction)micros.get(i);
            //           if (!mi.getName().equals("Smem") && machine.isPartialExecutionMode()) continue;                     
            //        mi.execute(program, machine);
            //}
            
            //machine.setPartialExecutionMode(false);
        }
        
        // all microinstructions are in parallel list whether they are parallel or not
        List<Microinstruction> parallel = instr.getParallelMicroinstrs(machine);
        
        //        System.out.println(parallel.size());
        for (int i = 0; i < parallel.size(); i++){
            Microinstruction mi = parallel.get(i);
            machine.setMachineResultOffset(mi.getResultOffset()); // kps hack
            //System.out.println("res offset before = " + machine.getMachineResultOffset()); // kps hack
            if (machine.isPartialExecutionMode()) { skipped = true; continue;}
            else mi.execute(program, machine);
            //System.out.println("res offset after = " + machine.getMachineResultOffset()); // kps hack
        }

        if (!skipped && machine.isPartialExecutionMode()) { 
           machine.setPCAddr(machine.getNextInstructionAddr());         
           machine.setPCAddr(machine.getNextInstructionAddr());
           machine.setNextPCAddr(machine.getPCAddr());
        }
        machine.setPartialExecutionMode(false);
    } 
 



    public void makeInstructionHashTable(Program program, Machine machine){
        List<Input> inputs = program.getInputs();
        Iterator<Input> iter = inputs.iterator();
        while (iter.hasNext()){
            Input input = iter.next();
            List<InputLineObject> inputLines = input.getInputLines();
            Iterator<InputLineObject> iter2 = inputLines.iterator();
            while (iter2.hasNext()){
                Object o = (Object)iter2.next();
                //                System.out.println(o.toString());

                // get program start address into system
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
    
    /*
    public void makeParallel(Program program, Machine machine){
        ArrayList operations;
        List inputs = program.getInputs();
        Iterator iter = inputs.iterator();
        while (iter.hasNext()){
            Input input = (Input)iter.next();
            List inputLines = input.getInputLines();
            Iterator iter2 = inputLines.iterator();
            while (iter2.hasNext()){
                Object o = (Object)iter2.next();
                if (o instanceof Instruction){
                    Instruction instr = (Instruction)o;
                    operations = instr.getOperations();
                    //                    reschedule(machine, instr);
                }
                
                
            }
        }
        }*/
    
    
    
    
}
