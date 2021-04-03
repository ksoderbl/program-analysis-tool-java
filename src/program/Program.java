/**
 * Program.java
 */

package program;

import input.Input;

import graph.Graph;

import machine.Machine;
import instr.*;
import arm.*;
import stabs.*;

import cfg.*;
import main.*;
import misc.*;
import input.*;
import pseudoOp.*;
import loop.Bucket;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import simulation.SimulationStatistics;
import basicblocks.BasicBlock;
import traces.Trace;
import graph.Node;

/**
 * Program representation.
 *
 * @author Mikko Reinikainen
 */

public class Program {
    /** control flow graph */
    private CFG cfg;

    /** symbol table that maps addresses to nodes */
    private Symbols addresses;

    /** symbol table that maps labels to addresses */
    private Symbols labels;

    /** inputs that were used to read the program */
    private List<Input> inputs;

    /** procedures of the program */
    private HashMap<Object, Procedure> procedures;

    /** 
     * loops of the program (basicblocks)
     * the first item in the bucket is the loop header
     *  
     */
    private Bucket loops;

    
    /** 
     * traces of the program embedded in Traces objects
     * each trace contains a number of basicblocks
     */
    private HashMap<String, Trace> traces;
    
    /** 
     * Basic blocks of the program. These are only available after basic
     * block analysis.
     *
     * @see basicblocks.BasicBlockAnalysis
     */
    private Graph basicBlocks;

       
    /** storage for types */
    private DebugData debugData;

    /** commandlineoptions from main */
    private UserOptions userOptions;

    /** the machine this program will run on */
    private Machine machine;

    /** the simulation statistics object, provides data about instruction executions, etc. */
    private SimulationStatistics simulationStatistics;


    /** the program entry point: program_entry jumps to this label */
    private String entryPoint;

    /** energy estimation results */
    private TiwariResults tiwariResults;
    public void setTiwariResults(TiwariResults tiwariResults) {
        this.tiwariResults = tiwariResults;
    }
    public TiwariResults getTiwariResults() {
        return tiwariResults;
    }

    /**
     * Constructs a new program
     *
     * @param name name of the program
     * @return the new program
     */
    public Program(UserOptions userOptions, Machine machine) {
        addresses = new Symbols();
        labels = new Symbols();
        inputs = new ArrayList<Input>();
        procedures = new HashMap<Object, Procedure>();
        debugData = new DebugData();
        this.userOptions = userOptions;
        this.machine = machine;
        // note these must come last, as they need the above to be initialized
        cfg = new CFG(userOptions.getProgramName());
    }


    /**
     * @return control flow graph
     */
    public CFG getCFG() {
        return cfg;
    }

    /**
     * @return entry point of program
     */
    public String getEntryPoint() {
        return entryPoint;
    }

    /**
     * @return entry address of program
     */
    public Long getEntryAddress() {
        Symbols symbols = this.getLabels();
        Long address = (Long)symbols.get(this.getEntryPoint());
        return address;
    }



    /**
     * @param entryPoint entry point of program
     */
    public void setEntryPoint(String entryPoint) {
        this.entryPoint = entryPoint;
    }

    /**
     * @return symbol table that maps labels to addresses
     */
    public Symbols getLabels() {
        return labels;
    }

    /**
     * @return symbol table that maps addresses to nodes
     */
    public Symbols getAddresses() {
        return addresses;
    }

    /**
     * @return control flow graph node with the specified label
     */
    public CFGNode getNode(String label) {
        if (labels.get(label) == null) {
            return null;
        } else {
            return (CFGNode) addresses.get(labels.get(label));
        }
    }

    // return address of label
    public Long getAddress(String label) {
        if (labels.get(label) == null)
            return null;
        return (Long) labels.get(label);
    }

    public CFGNode getBranchTargetNode(Instruction instr) {
        Long addr = getBranchTargetAddress(instr);
        //System.out.println("Program.getBranchTargetNode: addr  = " + addr);
        CFGNode node = getNode(addr);
        //System.out.println("Program.getBranchTargetNode: node  = " + node);
        return node;
    }

    public Long getBranchTargetAddress(Instruction instr) {
        BranchTarget bt = instr.getBranchTarget();
        //System.out.println("Program.getBranchTargetAddress: bt    = " + bt);
        //System.out.println("Program.getBranchTargetAddress: instr = " + instr);
        Long addr = bt.getAddress(this);
        //System.out.println("Program.getBranchTargetAddress: addr  = " + addr);
        return addr;
    }

    public String getBranchTargetName(Instruction instr) {
        BranchTarget target = instr.getBranchTarget();
        String label = target.getLabel();
        Long offset = target.getOffset();

        if (offset.longValue() != 0) {
            return label + " " + offset;
        }
        return label;
    }

    /**
     * @return control flow graph node with the specified address
     */
    public CFGNode getNode(Long addr) {
            return (CFGNode) addresses.get(addr);
    }

    /** 
     * Adds a mapping from a label to an address.
     * 
     * @param label label that points to the address
     * @param address address pointed by the label
     */
    public void addLabel(String label, Long address) {
        // add mapping from label to address
        if (labels.containsKey(label)) {
            Main.warn("duplicate label '" + label + "'");
        } else {
            labels.put(label, address);
        }
    }

    /** 
     * Adds a mapping from an address to a control flow graph node.
     *
     * @param address address of the node
     * @param node a control flow graph node
     */
    public void addAddress(Long address, CFGNode node) {
        // add mapping from address to node
        if (addresses.containsKey(address)) {
            if (((CFGNode) addresses.get(address)) != node) {
                Main.warn("address " + address +
                                   " already contains node " + node);
            }
        } else {
            addresses.put(address, node);
        }
    }



    /**
     * Adds a mapping from label to address and from address to node.
     *
     * @param label label of the symbol
     * @param address address of the label
     * @param node CFGNode which the label points to
     */
    public void addSymbol(String label,
                          Long address,
                          CFGNode node) {
        //Main.info("addSymbol: " + label
        //+ "->" + address + "->" + node);

        // add mapping from label to address
        addLabel(label, address);

        // add mapping from address to node
        addAddress(address, node);
    }

    /**
     * @return inputs that were used to read the program
     */
    public List<Input> getInputs() {
        return inputs;
    }

    /**
     * Adds an input to the list of inputs that were used to read the
     * program.
     *
     * @param input the input to be added 
     */
    public void addInput(Input input) {
        inputs.add(input);
    }

    /**
     * @return procedures of the program 
     */
    public HashMap<Object, Procedure> getProcedures() {
        return procedures;
    }

    /**
     * @return loops of the program 
     */
    public Bucket getLoops() {
        return loops;
    }
    

    /**
     * @return debugdata of the program 
     */
    public DebugData getDebugData() {
        return debugData;
    }

    
    public UserOptions getOptions(){
        return userOptions;
    }

    /**
     * Creates a new procedure and adds it to procedures of this program.
     *
     * @param addr address of the procedure
     * @param name name of the procedure
     * @param entry entry node of the procedure
     * @return the new procedure
     */
    public Procedure createProcedure(Long address,
                                     String name,
                                     CFGNode entry) {
        Procedure result = new Procedure(name, entry);
        procedures.put(address, result);
        return result;
    }

    
    /**
     * @return the named label
     */
    /*
    public Label getLabel(String name) {
        Iterator i = labels.keySet().iterator();
        while (i.hasNext()) {
            Label label = (Label)i.next();

            if (label.getName().equals(name)) {
                return label;
            }
        }
        return null;
        }*/

    /**
     * @return the named procedure
     */
    public Procedure getProcedure(Long addr) {
        Procedure proc = procedures.get(addr);
        if (proc == null) {
            //Main.warn("getProcedure: Could not find proc at " + addr);
        }
        return proc;
    }


    
    /**
     * @param Bucket of traces containing basicblocks
     */
    public void setTraces(HashMap<String, Trace> traces) {
        this.traces = traces;
    }

    /**
     * @return Bucket of traces containing basicblocks
     */
    public HashMap<String, Trace> getTraces() {
        return traces;
    }
    

    
    /**
     * @param Bucket of traces containing basicblocks
     */
    public void setLoops(Bucket loops) {
        this.loops = loops;
    }
    

    public void setSimulationStatistics(SimulationStatistics ss){
        this.simulationStatistics = ss;
    }
    
    public SimulationStatistics getSimulationStatistics(){
        return simulationStatistics;
    }
    

    /**
     * @param basicBlocks basic blocks of the program as a graph
     */
    public void setBasicBlocks(Graph basicBlocks) {
        this.basicBlocks = basicBlocks;
    }


    /** 
     * @return program size in bytes 
     */

     public int getByteSize() {
        int size = 0;
        Iterator<Node> iter = this.getBasicBlocks().getNodes().iterator();
        while (iter.hasNext()){
            BasicBlock bb = (BasicBlock)iter.next();
            size += bb.getByteSize();
        }
        return size;
     }

    /**
     * @return basic blocks of the program as a graph
     */
    public Graph getBasicBlocks() {
        return basicBlocks;
    }

    /**
     * @return contents of the program as a String
     */
    public String toString() {

        // add each input to the result
        String result = inputs.size() + " inputs:\n";
        Iterator<Input> inputIter = inputs.iterator();

        while (inputIter.hasNext()) {
            result = result + inputIter.next() + "\n";
        }

        // add cfg and labels to the result
        result = result + cfg + "\nlabels " + labels + "\n";

        // add each procedure to the result
        result = result + procedures.size() + " procedures:\n";
        Iterator<Procedure> procIter = procedures.values().iterator();
        while (procIter.hasNext()) {
            result = result + procIter.next() + "\n";
        }
        return result;
    }

}
