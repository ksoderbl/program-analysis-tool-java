
package traces;


import basicblocks.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import graph.Edge;;

/**
 * A trace is a collection of basicblocks that connects 
 * to each other via a branch which each has the highest 
 * dynamic execution frequency among its predecessors in the CFG
 */

public class Trace {
    
    /** Max length of trace in bytes */
    public static final int MAXBYTESIZE = 512;
    
    /** percent of the executions of header node to terminate trace */
    public static final double TERMINATIONFREQ = 0.60;


    /** the size of the trace */
    private int size = 0;

    /** we use a list so we can find first item trivially */
    private List<BasicBlock> basicBlocks;
    
    /** name of the trace */
    private String name;

    public Trace(BasicBlock bb, String name){
        basicBlocks = new ArrayList<BasicBlock>();
        basicBlocks.add(bb);
        this.name = name;
    }
    
    public String getName(){
        return name;

    }

    public Iterator<BasicBlock> getIterator(){
        return basicBlocks.iterator();
    }

    public void addBasicBlock(BasicBlock bb){
        if (bb == null)
            return;
        basicBlocks.add(bb);
        this.addByteSize(bb.getByteSize());
        // System.out.println("Trace size now:"+getSize());
    }
    
    private void addByteSize(int amount){
        size +=amount;
    }
    
    public int getByteSize(){
        return size;
    }

    public BasicBlock getHeader(){
        if (basicBlocks.size() == 0)
            return null;
        else return (BasicBlock)basicBlocks.get(0);
    }



    public BasicBlock evaluate(BasicBlock bb){
        List<Edge> outgoing = bb.getOutgoingEdges();
        Iterator<Edge> iter = outgoing.iterator();
        BasicBlockEdge bestEdge = null;
        double executionFreq;
        if (bb.isPartOfTrace()) return null;
        if (this.getByteSize()+bb.getByteSize() > MAXBYTESIZE)
            return null;
        
        executionFreq = bb.getByteSize() / this.getHeader().getByteSize();
        if (executionFreq < TERMINATIONFREQ) return null;
        
        while (iter.hasNext()) {
            BasicBlockEdge edge = (BasicBlockEdge) iter.next();
            if (bestEdge == null) bestEdge = edge;
            
            if (bestEdge.getExecutions() < edge.getExecutions())
                bestEdge = edge;
        }
        
        BasicBlock bestBB = (BasicBlock)bestEdge.getEnd();
        return bestBB;
    }



    public void computeSize(){
        Iterator iter = basicBlocks.listIterator();
        while (iter.hasNext()){
            BasicBlock bb = (BasicBlock)iter.next();
            
        }
        
    }
    
    
}



