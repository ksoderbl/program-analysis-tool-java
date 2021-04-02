/**
 * BasicBlock.java
 */

package basicblocks;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import graph.Node;
//import program.Label;
import program.Procedure;
import program.TiwariResults;
import cfg.CFGNode;
import input.Input;
import loop.*;
import scratchpad.CodeAllocationObject;

/**
 * A basic block. 
 * 
 * @author Mikko Reinikainen
 * @see BasicBlocks
 * @see Node
 */
public class BasicBlock extends Node implements CodeAllocationObject {

    /** 
     * Labels that point to entry node of this basic block. Elements of
     * the list are of type Label.
     */
    private List labels;

    /**
     * Nodes of this basic block. Elements of the list are of type
     * CFGNode.
     */
    private List nodes;

    /** type of the basic block. Represented as a bit mask. */
    private int type;

    /** size of basicBlock in bytes */
    private int byteSize = 0;

    /** the Input this basicblock belongs to */
    private Input input;

    /** the dynamic executions of this basicblock */
    private int executions = 0;

    /** is this basicblock part of a trace? (used in traceanalysis) */
    private boolean inTrace = false;

    /** has this basicblock been allocated to scratchpad? */
    private boolean allocatedSPM = false;

    /** the loop depth number for this basicblock */
    private int loopDepth;
    /**
     * Bit masks for basic block types.
     */
    public static final int PROCEDURE_ENTRY = 1;
    public static final int PROCEDURE_EXIT = 2;

    /**
     * Constructs a new basic block.
     *
     * @param name name of the basic block
     * @return the new basic block
     */
    public BasicBlock(String name) {
        super(name);
        labels = new ArrayList();
        nodes = new ArrayList();
        type = 0;
    }

    /**
     * @return first label of this basic block
     */
    public List getLabels() {
        return labels;
    }

    /**
     * Adds a control flow graph label to the end of this basic block.
     *
     * @param label the label to be added to this basic block
     */
    public void addLabel(String label) {
        labels.add(label);
    }

    /**
     * @return first node of this basic block
     */
    public List getNodes() {
        return nodes;
    }

    /**
     * Adds a control flow graph node to the end of this basic block.
     *
     * @param node the node to be added to this basic block
     */
    public void addNode(CFGNode node) {
        nodes.add(node);
    }

    /**
     * Adds a control flow graph node to index i at this basic block.
     *
     * @param node the node to be added to this basic block
     */

    public void addNode(CFGNode node, int index) {
        nodes.add(index, node);
    }


    /**
     * @return type of this basic block 
     */
    public int getType() {
        return type;
    }

    /**
     * Sets type of this basic block
     *
     * @param type type of this basic block
     */
    public void setType(int type) {
        this.type = type;
    }

    public void addByteSize(int size){
        byteSize += size;
    }

    public int getByteSize(){
        return byteSize;
    }
    
    public void setInput(Input input){
        this.input = input;
    }
    
    public Input getInput(){
        return input;
    }
    
    public int getExecutions(){
        return executions;
    }
    
    public void setExecutions(int executions){
        this.executions = executions;
    }


    /** energy estimation results */
    private TiwariResults tiwariResults;
    public void setTiwariResults(TiwariResults tiwariResults) {
        this.tiwariResults = tiwariResults;
    }
    public TiwariResults getTiwariResults() {
        return tiwariResults;
    }

    public double getEnergyConsumption() {
        return tiwariResults.getEnergy();
    }
    
    public boolean isPartOfTrace(){
        return inTrace;
    }
    
    public void setPartOfTrace(){
        inTrace = true;
    }

    /* the level of loop nesting, 1 is first level */
    public void setLoopDepth(int depth){
        loopDepth = depth;
    }
   
    public void addLoopDepth(){
        loopDepth++;
    }

    public int getLoopDepth(){
        return loopDepth;
    }
    
    /* for CodeAllocationObject interface*/
    public int getWeight(){
        return byteSize;
    }

    
    public int getCost(double spmEnergy, double mainMemoryEnergy){
        return (int)(executions*nodes.size()*(mainMemoryEnergy - spmEnergy));
    }
    
    public void setCost(int cost){
        executions = cost;
    }

    /* getName() comes from superclass Node */
    public void setAllocated(){
        allocatedSPM = true;
    }

    public boolean getAllocated(){
        return allocatedSPM;
    }

  /* end of codeallocation interface */


    /**
     * @return contents of this basic block as a String
     */
    public String toString() {
        String result = new String();
        Iterator iter;
        // if basic block has labels, split the node into two records
        if (labels.size() > 0) {
            result = result + "{";
            // labels of this basic block
            iter = labels.iterator();
            result = result + this.getName() +" S:"+this.getDFSNum()+ " "+this.getExecutions() +"|";
            while (iter.hasNext()) {
                //result = result + this.getName() +" S:"+this.getDFSNum()+ " E:"+this.getDFSEndNum() +"|";
                result = result + ((String) iter.next()) + ":\t";
            }
            result = result + "|";
        }

        // no labels, just output basicblock name
        else {
            result = result + "{";
            result = result + this.getName() +" S:"+this.getDFSNum()+ " "+this.getExecutions() +"|";
        }


        // instructions of this basic block
        iter = nodes.iterator();
        while (iter.hasNext()) {
            CFGNode node = (CFGNode) iter.next();
            if (node.getInstruction() == null) {
                continue;
            }
            Procedure proc = node.getProcedure();
            result = result + node.getInstruction();
            result =
                result + "(" + node.getInstruction().getLine() + ")";
            if (proc != null) {
                result = result + "(" + proc.getName() + ")";
            }
            if (node.getPreState() != null) {
                result =
                    result + "[" + node.getPreState().toString() + "]";
            }
            result = result + "\\l";
        }

        // end of record
        //if (labels.size() > 0) {
        result = result + "}";
        //}

        return result;
    }
}
