package scratchpad;

import program.Program;
import program.Procedure;
import main.UserOptions;
import basicblocks.BasicBlock;
import basicblocks.BBDynamicExecutionAnalysis;
import machine.Machine;
import machine.Scratchpad;
import analysis.Analysis;
import simulation.SimulationAnalysis;
import java.util.*;
import graph.*;


/**
 * Scratchpad analysis
 *
 * @author Peter Majorin
 * @see Analysis
 */

public class ScratchpadAnalysis implements Analysis {
        public static final double kB16 = 4.0;
        public static final double kB8 = 2.07;
        public static final double kB4 = 1.21;
        public static final double kB2 = 1.07;
        public static final double kB1 = 0.82;

        public static final double b32 = 0.45;
        public static final double b64 = 0.49;
        public static final double b128 = 0.53;
        public static final double b256 = 0.61;
        public static final double b512 = 0.69;

        HashMap<Integer, Double> memoryEnergy = new HashMap<Integer, Double>();

    /**
     * Performs a scratchpad analysis. Doesn't edit code to use the analysis results.
     * Results of the analysis are stored in the scratchpad object.
     *
     * first add all the SP objects in the BlockOptimizationResult, then do the analysis
     * @param program the program
     * @param machine machine on which the program runs
     */
    public void analyze(Program program, Machine machine) {
        UserOptions userOptions = program.getOptions();
        Iterator<Node> iter;
        int SPMSize = program.getOptions().getSPMSize();
        memoryEnergy.put(32, b32);
        memoryEnergy.put(64, b64);
        memoryEnergy.put(128, b128);
        memoryEnergy.put(256, b256);
        memoryEnergy.put(512, b512);
        memoryEnergy.put(1024, kB1);
        memoryEnergy.put(2048, kB2);
        memoryEnergy.put(4096, kB4);
        memoryEnergy.put(8192, kB8);
        memoryEnergy.put(16384, kB16);

//        Random random = new Random(54);
        BlockOptimizationResult result = new BlockOptimizationResult(SPMSize, getEnergy(SPMSize), machine.getMainMemoryEnergy());

        iter = program.getBasicBlocks().getNodes().iterator();
        

        while (iter.hasNext()){
            BasicBlock bb = (BasicBlock)iter.next();
//            bb.setCost(1 + (int)(Math.random() * 100));
            result.addCodeAllocationObject(bb);
        }
        
        /*
        iter = program.getProcedures().values().iterator();
        while (iter.hasNext()){
            Procedure proc = (Procedure)iter.next();
            scratchpad.addProcedure(proc, 1 + (int)(Math.random() * 100));
        }
        */



        System.out.println("SPM size: "+SPMSize+" Energy:"+getEnergy(SPMSize)+" Program size:"+program.getByteSize());

        BlockSolverDP solver = new BlockSolverDP(result);

        solver.solveOptimumBlocks();
        //result.getObjects(scratchpad);

        result.setAllocatedObjects();
        result.print();

        // run the simulator in case we have a static analysis to obtain real execution counts; 
        if (userOptions.getAnalyzeDominators()){
            SimulationAnalysis simA = new SimulationAnalysis();
            simA.analyze(program, machine);
            BBDynamicExecutionAnalysis BBDynA = new BBDynamicExecutionAnalysis();
            BBDynA.analyze(program, machine);
        }

        double fetchEnergy = 0;
        double fetchWithoutSPM = 0;
        Graph basicBlocks = program.getBasicBlocks();
        iter = basicBlocks.getNodes().iterator();

        while (iter.hasNext()){
           BasicBlock bb = (BasicBlock)iter.next();
           if (bb.getAllocated()){ 
                System.out.println(bb.getName());
                fetchEnergy += bb.getExecutions()*getEnergy(SPMSize);
                fetchWithoutSPM += bb.getExecutions()*machine.getMainMemoryEnergy();                
           }
           else {
                fetchEnergy += bb.getExecutions()*machine.getMainMemoryEnergy();
                fetchWithoutSPM += bb.getExecutions()*machine.getMainMemoryEnergy();
            }
        
        }

        System.out.println("Fetching energy with SPM:"+fetchEnergy);
        System.out.println("Fetching energy without SPM:"+fetchWithoutSPM);
        System.out.println("Ratio:"+ fetchWithoutSPM/fetchEnergy);
  }


 public double getEnergy(int SPMSize){
    Double energy = memoryEnergy.get(SPMSize);
    return energy.doubleValue();        
 }

}
