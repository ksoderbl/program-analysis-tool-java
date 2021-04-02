
/** 
 * @author Peter Majorin
 *
 * A debugformat independent way to represent debug data
 * we have types, variables and memoryblocks
 * a variable is a gcc defined type, memory blocks
 * are either arrays or structures
 * a variable is an instance of a type, and so is a memoryblock
*/


package stabs;

import java.util.*;


public class DebugData{

    // mapping from typeIds (keys) -> typenames
    private HashMap types;
    // mapping from variable names (keys) -> typeId's 
    private HashMap variables;
    
    /** storage for global tables & structs of the program  */
    private HashMap memoryBlocks;

    public DebugData(){
        types = new HashMap();
        variables = new HashMap();
        memoryBlocks = new HashMap(); 
    }
    
    public void addMemoryBlock(String name, MemoryBlock memBlock){
        memoryBlocks.put(name, memBlock);
    }
    
    
    public HashMap getTypes(){
        return types;
    }
    
    public HashMap getVariables(){
        return variables;
    }
    
    public HashMap getMemoryBlocks(){
        return memoryBlocks;
    }
    
    public void printTypes(){
        Iterator iter = types.values().iterator();
        System.out.println("*** Types ***");
        while (iter.hasNext()){
            Type pType = (Type)iter.next();
            System.out.println(pType.getName());
        }
        
    }
    
    public void printVariables(){
        Iterator iter = variables.values().iterator();
        System.out.println("*** Global Variables ***");
        while (iter.hasNext()){
            Variable var = (Variable)iter.next();
            System.out.println(var.getName());
        }
        
    }
    
     public void printMemoryBlocks(){
        Iterator iter = memoryBlocks.values().iterator();
        System.out.println("*** Memory Blocks ***");
        while (iter.hasNext()){
            MemoryBlock memBlock = (MemoryBlock)iter.next();
            System.out.println(memBlock.getName());
        }
        
    }
    



}
