
/**
 * A scratchpad hardware component. Contains the priorities for
 * different entities to be placed on scratchpad in a Treemap
 *
 * @author Peter Majorin
 *
 */

package machine;

import java.util.Vector;
import java.util.Enumeration;
import basicblocks.BasicBlock;
import stabs.MemoryBlock;
import program.Procedure;
import misc.keyComparator;

public class Scratchpad{
    
    private int baseAdd   = 0;
    private int blockSize = 0;
    private int blocks    = 0;

    /** the next allocation address */
    private int nextAdd = 0;

    /** of type int */
    private Vector basicBlocks = new Vector();
    /** of type int */
    private Vector procedures = new Vector();
    /** of type int */
    private Vector variables = new Vector();
    /** of type int */
    private Vector memoryBlocks = new Vector();

    /** of Procedure, BasicBlock, Variable, or MemoryBlock type */
    private Vector selectedObjects = new Vector();
    
    public Scratchpad(int baseAdd, int blocks, int blockSize){
        
        this.baseAdd = baseAdd;
        this.blockSize = blockSize;
        this.blocks = blocks;
    }
    
    
    public void addProcedure(Procedure proc, int priority){
        proc.setPriority(priority);
        procedures.add(proc);
    }
    
    public void addBasicBlock(BasicBlock bb, int priority){
//        bb.setPriority(priority);
        basicBlocks.add(bb);
    }
   
    public void addMemoryBlock(MemoryBlock mem, int priority){
        memoryBlocks.add( mem);
    }


    public Vector getSelectedObjects(){
        return selectedObjects;
    }

    public void addSelectedObject(Object object){
        selectedObjects.add(object);
    }

    public Object getNextSelectedObject(){
        Object o = selectedObjects.firstElement();
        if (o instanceof Procedure){
            Procedure proc = (Procedure)o;
            nextAdd +=proc.getByteSize();
        }
        if (o instanceof BasicBlock){
            BasicBlock bb = (BasicBlock)o;
            nextAdd +=bb.getByteSize();
        }
        
        selectedObjects.remove(0);
        return o;
    }

    public void printPriorities(){
        Enumeration e = basicBlocks.elements();
        System.out.println("Basic Blocks");
        while (e.hasMoreElements()){
            BasicBlock bb =  (BasicBlock) e.nextElement();
//            System.out.println("bb:" + bb.getName() 
//                               +" priority:"+ bb.getPriority()+ " size:"+bb.getByteSize());
        }

        //        Set set2 = procedures.keySet();
        //iter = set2.iterator();
        System.out.println("Procedures");

        e = procedures.elements();
        while (e.hasMoreElements()){
            Procedure proc = (Procedure) e.nextElement();
            System.out.println("proc:"+ proc.getName()
                               +" priority:"+ proc.getPriority()+ " size:"+proc.getByteSize());
                
        }
        
        
    }
    
    public String toString(){
        return "Scratchpad blocksize:"+blockSize+" blocks:"+blocks+" baseadd:"+baseAdd; 
    }

    public Vector getProcedures(){
        return procedures;
    }

    public Vector getBasicBlocks(){
        return basicBlocks;
    }
    
    public int getBlockSize(){
        return blockSize;
    }

    public int getBlocks(){
        return blocks;
    }

    public int getBaseAdd(){
        return baseAdd;
    }

    public int getNextAdd(){
        return nextAdd;
    }
}
