package main;

import java.util.Iterator;
import java.util.ArrayList;

public class UserOptions {
    /** dominator analysis? **/
    boolean analyzeDominators = false;
    /** should we performe liveness analysis? */
    //boolean analyzeLiveness = false;
    /** should we perform pipeline analysis? */
    //boolean analyzePipeline = false;
    /** should we perform pipeline hazard analysis? */
    //boolean analyzePipelineHazard = false;

    /** should we perform energy analysis? */
    boolean analyzeEnergy = false;

    /** should we perform scratchpad analysis? */
    boolean analyzeScratchpad = false;
    /** should we perform trace analysis? */
    boolean analyzeTraces = false;

    /** should we perform a dynamic simulation of the CPU? */
    boolean analyzeSimulation = false;
    
    /** should we print the cfg after input? */
    boolean printProgramAfterInput = false;
    /** should we print the cfg after analysis? */
    boolean printProgramAfterAnalyses = false;
    /** should we print the symbol tables? */
    boolean printSymbolTables = false;
    /** should we emit the code? */
    boolean emitCode = false;
    /** should we edit the code? */
    boolean editCode = false;
    // should we perfrom register allocation?
    //boolean registerAllocation = false;
    /** output type */
    String outputType = "b";
    /** file for output of the cfg as a graphviz graph */
    String outputCFG = "output.dot";
    /** input file type */
    String machineArch = "unknown"; // no default, machine must be specified on cmd line
    /** input files */
    ArrayList<String> inputFiles = new ArrayList<String>();
    /** graphviz output */ 
    String graphSize = "a4";
    /** temp storage for programName */
    String programName = null;
    /** Name for output directory */
    String dirName;
    /** Verbose ?? */
    boolean verbose = false;
    
    String version = "0.1";

    String annoFile = "";

    // debug switches 
    /** debug for stabs parsing (may be obsolete) */
    boolean debugStabs = false;
    /** debug for microcode */
    boolean debugMC = false;
    /** test cases for simulation */
    boolean simulationRegression = false;
    /** test number */
    String simulationRegressionTestcase = "";
    /** scratchpad size */
    int SPMSize = 0;
    
    public UserOptions(String args[]){
        this.processUserOptions(args);
    }
    
    public boolean getAnalyzeDominators() {
        return analyzeDominators;
    }
    
    public void setAnalyzeDominators(boolean value){
        analyzeDominators = value;
    }
    
    //    public boolean getAnalyzeLiveness() {
    //        return analyzeLiveness;
    //}
    
    //public boolean getAnalyzePipeline() {
    //        return analyzePipeline;
    //}

    //public boolean getAnalyzePipelineHazard() {
    //        return analyzePipelineHazard;
    //}

    public boolean getAnalyzeScratchpad(){
        return analyzeScratchpad;
    }

    public int getSPMSize(){
        return SPMSize;
    }

    public boolean getAnalyzeSimulation(){
        return analyzeSimulation;
    }

    public boolean getAnalyzeTraces(){
        return analyzeTraces;
    }

    public boolean getAnalyzeEnergy() {
        return analyzeEnergy;
    }
    
    public boolean getEmitCode() {
        return emitCode;
    }

     public boolean getEditCode() {
        return editCode;
    }
    

    public boolean  getPrintProgramAfterAnalyses() {
        return printProgramAfterAnalyses;
    }

    public boolean getPrintProgramAfterInput() {
        return printProgramAfterInput;
    }

    public boolean  getPrintSymbolTables() {
        return printSymbolTables;
    }

    public String getMachineArch() { /* former inputType */
        return machineArch;
    }

    public ArrayList<String> getInputFiles() {
        return inputFiles;
    }

    public String getOutputCFG() {
        return outputCFG; 
    }

    public String getOutputType() {
        return outputType;
    }
    
    public String getGraphSize() {
        return graphSize;
    }
    
    public String getProgramName() {
        return programName;
    }
    
    public String getDirName() {
        return dirName;
    }

    //public boolean getRegisterAllocation() {
    //        return registerAllocation;
    //}

    public boolean getVerbose() {
        return verbose;
    }


    // debug methods start here
    public boolean getDebugStabs() {
        return debugStabs;
    }

    public boolean getDebugMC(){
        return debugMC;
    }

    public void setDebugMC(boolean value){
        debugMC = value;
    }

    public boolean getAnalyzeSimulationRegression(){
        return simulationRegression;
    }
    
    public String getSimulationRegressionTestcase(){
        return simulationRegressionTestcase;
    }
    


    // parse the options and read filename 
    
    private void processUserOptions(String[] args){
        
        // process command line parameters
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-h")
                || args[i].equals("--help")
                || args[i].equals("-help"))
                usage();
            else if (args[i].equals("--version")) {
                version();
            }
            else if (args[i].equals("-v")) {
                verbose = true;
            }

            else if (args[i].equals("-d")) {
                analyzeDominators = true;
            }
            else if (args[i].equals("-sp")) {
                analyzeScratchpad = true;
                i++;
                SPMSize = stringToInt(args[i]);
            }

            else if (args[i].equals("-sim")) {
                analyzeSimulation = true;
            }
            

            else if (args[i].equals("-t")) {
                analyzeTraces = true;
            }

            else if (args[i].equals("-ea")) {
                analyzeEnergy = true;
            }

            else if (args[i].equals("-esp")) {
                analyzeScratchpad = true;
                editCode = true;
            }

            else if (args[i].equals("-e")) {
                editCode = true;
            }
            //else if (args[i].equals("-l")) {
            //                analyzeLiveness = true;
            //}
            //else if (args[i].equals("-p")) {
            //        analyzePipeline = true;
            //}
            //else if (args[i].equals("-ph")) {
            //        analyzePipelineHazard = true;
            //}
            else if (args[i].equals("-i")) {
                printProgramAfterInput = true;
            }
            else if (args[i].equals("-a")) {
                printProgramAfterAnalyses = true;
            }
            else if (args[i].equals("-sym")) {
                printSymbolTables = true;
            }
           
            // these are debug options, may be removed if not needed
            else if (args[i].equals("-ds")) {
                debugStabs = true;
            }
            else if (args[i].equals("-dMC")){
                debugMC = true;
            }

            else if (args[i].equals("-dsim")){
                simulationRegression = true;
                i++;
                simulationRegressionTestcase = args[i];
            }


            // end of debug options

            //else if (args[i].equals("-r")) {
                // Register allocation depends on liveness analysis,
                // so choosing -r always implies -l also.
            //        analyzeLiveness = true;
            //        registerAllocation = true;
            //}
            else if (args[i].length() >= 7
                     && args[i].substring(0, 7).equals("-march=")) {
                machineArch = args[i].substring(7); /* a.k.a. input type */
            }
            else if (args[i].length() >= 2
                       && args[i].substring(0, 2).equals("-O")) {
                outputType = args[i].substring(2);
            }
            else if (args[i].equals("-o")) {
                i++;
                if (i < args.length) {
                    outputCFG = args[i];
                }
                else {
                    System.err.println("Error: missing output file name.");
                    usage();
                }
            }
            else if (args[i].equals("-s")) {
                i++;
                if (i < args.length) {
                    graphSize = args[i];
                }
                else {
                    System.err.println("Error: missing paper size.");
                }
            }
            // emit options
            else if (args[i].length() >= 2
                     && args[i].substring(0, 2).equals("-E")) {
                i++;
                emitCode = true;
                if (i < args.length) {
                    if (isAlphaNumeric(args[i]))
                        dirName = args[i];
                    else { 
                        //Main.warn("no valid emit directory specified, using emitdefaultdir.");
                        dirName = "emitdefaultdir";
                        i--;
                    }
                }
                else dirName = "emitdefaultdir"; // should not happen
            }
            
            else {
                inputFiles.add(args[i]);
            }
        }

        // if no input files are defined, exit
        if (inputFiles.size() == 0)
            Main.fatal("no input files");
        
        // construct program name
        String progName = null;
        Iterator<String> iter = inputFiles.iterator();
        while (iter.hasNext()) {
            if (progName == null) {
                progName = iter.next();
            }
            else {
                progName = progName + ", " + iter.next();
            }
        }
        
        programName = progName;
    }
    

    public void printArgs(){
        Iterator<String> iter = inputFiles.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
    }


        
    private static void usage() {
        //System.out.println(
        //                   "Static program analyzer and transformer of ARM assembly code. This program takes as input one or several files containing ARM assembly code compiled with a version 3 GCC compilier (arm-elf-gcc). The arguments given to arm-elf-gcc to produce these files must include -S (output assembly code) and -gstabs+ (put stabs debug info into the assembly files).");
        
        System.out.println
            ("Usage: " + Main.name + " [-a] [-h] [-i] [-l] [-o file] [-p] {inputfile(s) ...}\n\n"
             + "Options:\n"
             + "-a         Print program after analyses.\n"
             + "-ds        Print debug messages from stabsparser.\n"
             + "-E dir files  Emit files to dir. If dir is omitted, uses defaultemitdir.\n"
             + "-e sp      Edit code to scratchpad.\n"
             + "-h         Print this help.\n"
             + "-i         Print program after input.\n"

             //+ "-l         Perform liveness analysis.\n"
             + "-march=cpu Set input file type. Valid types: arm7 arm9 c55x.\n"
             + "-o file    Output control flow graph of the program as a graphviz graph.\n"
             + "           (default: output.dot)\n"
             //+ "-Otype     Set output type. Valid types: l (liveness), b (basic blocks), c (instruction CFG).\n"
             + "-Otype     Set output type. Valid types: b (basic blocks), c (instruction CFG).\n"
             + "           (default: basic blocks).\n"
             + "-p         Perform pipeline analysis.\n"
             + "-ph        Perform pipeline hazard analysis.\n"
             //+ "-r         Perform register re-allocation.\n"

             + "-s size    Paper size for graph output. Valid sizes: a4 (default), a3.\n\n"
             + "-sp        Perform scratchpad analysis. Does not edit code to use it.\n"
             + "-sym       Print some symbol tables.\n"
             + "-t         Print traces. \n"
             + "-v         Verbose mode (show INFO messages).\n\n"
             + "You must specify at least one input file.\n");
        
        System.exit(0);
    }

    private boolean isAlphaNumeric(String s) {
        final char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {      
            final char c = chars[i];
            if ((c >= 'a') && (c <= 'z')) continue;
            if ((c >= 'A') && (c <= 'Z')) continue;
            if ((c >= '0') && (c <= '9')) continue;
            return false;
        }  
        return true;
    }

    public static int stringToInt(String integer){
        try {
            int a = java.lang.Integer.parseInt(integer);
            return a;
        }
        catch (Exception e){
            Main.fatal("Supplied argument "+integer+" is not an integer.");
            return 0;
        }
    }

    private void version() {
        System.out.println(Main.name + " " + Main.version);
        System.exit(0);
    }
}
