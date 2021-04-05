/**
 * Main.java
 */

package main;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import emit.*;
import edit.*;
import input.*;
import instr.*;
import program.*;
import cfg.*;
import pseudoOp.*;
import arm.gas.ARMGasInput;
import arm.gas.ARMGasEmitter;
import arm.ARMMachine;
//import arm.ARM7PipelineHazardAnalysis;
import c55x.C55xMachine;
//import c55x.asm.C55xAsmInput;
import c55x.dis.C55xDisInput;
import c55x.dis.C55xDisEmitter;
import c55x.C55xEnergyAnalysis;
import analysis.Analysis;
import basicblocks.*;
import machine.*;
import loop.*;
import scratchpad.ScratchpadAnalysis;
//import allocationalgorithms.*;
import traces.*;
import simulation.*;

/**
 * Main program that runs the analyzer.
 *
 * @author Mikko Reinikainen
 * @author Juha Tukkinen
 * @author Peter Majorin
 */

public class Main {

    /** name of this java program **/
    public static String name = "program-analysis-tool-java";
    /** version of this program */
    public static double version = 1.0;

    public static UserOptions userOptions = null;

    public static void main(String[] args) {

        /** create userOptions structure */
        userOptions = new UserOptions(args);

        /** machine description */
        Machine machine = null;
        String arch = userOptions.getMachineArch();
        String entry = "main"; // label where program execution starts

        if (arch.equals("arm7")) {
            machine = new ARMMachine("arm7");
        } else if (arch.equals("c55x")) {
            machine = new C55xMachine("c55x");
            entry = "_main";
            //entry = "_c_int00";
        } else {
            Main.fatal("invalid machine arch '" + arch + "'.");
        }
        /** program instance */
        Program program = new Program(userOptions, machine);
        program.setEntryPoint(entry);

        parseInputFiles(program, machine);

        // create CFG
        try {
            CFGAnalysis cfgAnalysis = new CFGAnalysis();
            cfgAnalysis.analyze(program, machine);
        }
        catch (Exception e) {
            Main.warn("CFG analysis failed: " + e);
        }

        // optionally print the symbol tables
        if (userOptions.getPrintSymbolTables()) {
            System.out.println("===================================" +
                               "===================================");
            System.out.println("Symbol table that maps labels to addresses:");
            System.out.println(program.getLabels());
            System.out.println("===================================" +
                               "===================================");
            if (false) {
                System.out.println("Symbol table that maps addresses to nodes:");
                System.out.println(program.getAddresses());
                System.out.println("===================================" +
                                   "===================================");
            }
            
        }

        // create basic blocks
        BasicBlockAnalysis basicBlockAnalysis = new BasicBlockAnalysis();
        basicBlockAnalysis.analyze(program, machine);


        // optionally print the program after input
        if (userOptions.getPrintProgramAfterInput()) {
            System.out.println(program);
        }
        // optionally perform liveness analysis
        //if (userOptions.getAnalyzeLiveness()) {
        //    LivenessAnalysis livenessAnalysis = new LivenessAnalysis();
        //    livenessAnalysis.analyze(program, machine);
        //}

        // optionally perform register allocation
        /*if (userOptions.getRegisterAllocation()) {
            RegisterAllocationAnalysis regAlloc = new RegisterAllocationAnalysis();
            regAlloc.analyze(program, machine);

            }*/
        // optionally perform pipeline analysis
        //if (analyzePipeline) {
        //    PipelineAnalysis pipelineAnalysis = new PipelineAnalysis();
        //    pipelineAnalysis.analyze(program, machine);
        //}
        // optionally perform pipeline hazard analysis
        // zzz need for pipelinehazardanalysis parent class
        /*if (userOptions.getAnalyzePipelineHazard()) {
            Analysis pipeline = null;
            if (userOptions.getMachineArch().equals("arm7")) {
                pipeline = new ARM7PipelineHazardAnalysis();
            }
            else {
                Main.fatal("invalid machine arch for pipeline analysis '"
                           + userOptions.getMachineArch() + "'");
            }
            if (pipeline != null) {
                pipeline.analyze(program, machine);
            }

            }*/
        
        // do a loop analysis
        if (userOptions.getAnalyzeDominators()){
            DominatorAnalysis dominatorAnalysis = new DominatorAnalysis();
            dominatorAnalysis.analyze(program, machine);
        }

        if (userOptions.getAnalyzeSimulation()){
            SimulationAnalysis simA = new SimulationAnalysis();
            simA.analyze(program, machine);
            BBDynamicExecutionAnalysis BBDynA = new BBDynamicExecutionAnalysis();
            BBDynA.analyze(program, machine);
        }


        
        if (userOptions.getAnalyzeScratchpad()){
            Scratchpad sp = new Scratchpad(1000, 8, 50);
            machine.addScratchpad(sp);

         //   SimulationAnalysis simA = new SimulationAnalysis();
         //   simA.analyze(program, machine);
         //   BBDynamicExecutionAnalysis BBDynA = new BBDynamicExecutionAnalysis();
         //   BBDynA.analyze(program, machine);

             ScratchpadAnalysis scratchpadAnalysis = new ScratchpadAnalysis();
            scratchpadAnalysis.analyze(program, machine);
        }
        
        if (userOptions.getAnalyzeTraces()){
            DominatorAnalysis dominatorAnalysis = new DominatorAnalysis();
            dominatorAnalysis.analyze(program, machine);
            TraceAnalysis traceAnalysis = new TraceAnalysis();
            traceAnalysis.analyze(program, machine);
        }
        
        if (userOptions.getAnalyzeSimulationRegression()){
            SimulationRegressionAnalysis simA = new SimulationRegressionAnalysis();
            simA.analyze(program, machine);
        }

        if (userOptions.getAnalyzeEnergy()) {
            Analysis energy = null;
            if (userOptions.getMachineArch().equals("c55x")) {
                energy = new C55xEnergyAnalysis();
            }
            else {
                Main.fatal("invalid machine arch for energy analysis '"
                           + userOptions.getMachineArch() + "'");
            }
            if (energy != null) {
                energy.analyze(program, machine);
            }

        }


        //if (userOptions.getEditCode()){
        //            PadOSEdit pEdit = new PadOSEdit(program, machine);
        //    pEdit.editCode();
        //}

        // optionally print the program after analyses
        if (userOptions.getPrintProgramAfterAnalyses()) {
            System.out.println(program);
        }

        // optionally emit the code 
        if (userOptions.getEmitCode()) {
            Emitter codeEmit = null;
            if (arch.startsWith("arm")) {
                codeEmit = new ARMGasEmitter(program);
            } else if (arch.equals("c55x")) {
                codeEmit = new C55xDisEmitter(program);
            }
            codeEmit.emit();

            /*
              this should be transferred to Emitter before removal -pgm
              if (fileName.length() > 0) {
              try {
              emitStream.flush();
              emitStream.close();
              fileStream.close();
              } catch (IOException ioe) {
              Main.fatal("" + ioe);
              }
              }
              
            */
        }
        
        // optionally output the control flow graph as a graphviz input file
        if (userOptions.getOutputCFG() != null) {
            if (userOptions.getOutputType().equals("b")) {
                // basic block output
                BasicBlockOutput out = new BasicBlockOutput(userOptions.getOutputCFG());
                out.writeOutput(program, machine, userOptions.getGraphSize());
            }
            if (userOptions.getOutputType().equals("c")) {
                // cfg output
                CFGOutput out = new CFGOutput(userOptions.getOutputCFG());
                out.writeOutput(program, machine, userOptions.getGraphSize());
            }

         //   if (userOptions.getOutputType().equals("d")) {
                // dominator output
        //        DominatorOutput out = new DominatorOutput(userOptions.getOutputDominator());
        //        out.writeOutput(program, machine, userOptions.getGraphSize());
        //    }


            //else if (userOptions.getOutputType().equals("l")) {
            //        // liveness output
            //        LivenessOutput out = new LivenessOutput(userOptions.getOutputCFG());
            //        out.writeOutput(program, machine, userOptions.getGraphSize());
            //        
            //        // Liveness State debugging
            //        out.writeStates(program, "states.out");
            //}
        }
        
        if (userOptions.getDebugStabs()) {        
            program.getDebugData().printTypes();
            program.getDebugData().printVariables();            
            program.getDebugData().printMemoryBlocks();
        }
    }

    /** 
     * Read input files and parse them, this is the starter routine, platform
     * specific
     */
    public static void parseInputFiles(Program program, Machine machine) {
        UserOptions userOptions = program.getOptions();

        Iterator<String> iter = userOptions.getInputFiles().iterator();
        
        while (iter.hasNext()) {
            Input input;
            String inputFile = iter.next();
            String arch = userOptions.getMachineArch();

            if ((inputFile.length()) < 3) {
                Main.fatal("invalid input filename' " + inputFile + "'");
            }
            // assembler code input
            else if (inputFile.endsWith(".s")
                     || inputFile.endsWith(".asm")) {
                if (arch.startsWith("arm")) {
                    input = new ARMGasInput(inputFile);
                    input.readInput(program, machine);
                    //} else if (arch.equals("c55x")) {
                    //input = new C55xAsmInput(inputFile);
                    //input.readInput(program, machine);
                } else {
                    Main.fatal("s, asm files not supported for arch " + arch);
                }
            }
            // disassembly input
            else if (inputFile.endsWith(".dis55")
                     || inputFile.endsWith(".dis")) {
                // kps - for TI c55x we can disassemble object (.obj) files
                // and binaries (e.g. a.out) using the dis55 tool
                if (arch.equals("c55x")) {
                    input = new C55xDisInput(inputFile);
                    input.readInput(program, machine);
                }
            }
            // annotation input
            else if (inputFile.endsWith(".anno")){
                input = new AnnotationInput(inputFile);
                System.out.println("annofile:"+inputFile);
            }
            

            else {
                Main.fatal("invalid input file type '" + inputFile + "'");
            }
        }
        
    }

    public static UserOptions getOptions() {
        return userOptions;
    }

    public static void info(String s) {
        if (userOptions.getVerbose())
            System.err.println(name + ": INFO: " + s);
    }

    public static void warn(String s) {
        System.err.println(name + ": WARNING: " + s);
    }

    public static void fatal(String s) {
        System.err.println(name + ": FATAL: " + s);
        System.exit(1);
    }

}
