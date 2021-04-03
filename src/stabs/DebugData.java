
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
    private HashMap<String, Type> types;
    // mapping from variable names (keys) -> typeId's 
    private HashMap<String, Variable> variables;
    
    /** storage for global tables & structs of the program  */
    private HashMap<String, MemoryBlock> memoryBlocks;

    public DebugData(){
        types = new HashMap<String, Type>();
        variables = new HashMap<String, Variable>();
        memoryBlocks = new HashMap<String, MemoryBlock>(); 
    }
    
    public void addMemoryBlock(String name, MemoryBlock memBlock){
        memoryBlocks.put(name, memBlock);
    }
    
    
    public HashMap<String, Type> getTypes(){
        return types;
    }
    
    public HashMap<String, Variable> getVariables(){
        return variables;
    }
    
    public HashMap<String, MemoryBlock> getMemoryBlocks(){
        return memoryBlocks;
    }
    
    public void printTypes(){
        Iterator<Type> iter = types.values().iterator();
        System.out.println("*** Types ***");
        while (iter.hasNext()){
            Type pType = iter.next();
            System.out.println(pType.getName());
        }
        
    }
    
    public void printVariables(){
        Iterator<Variable> iter = variables.values().iterator();
        System.out.println("*** Global Variables ***");
        while (iter.hasNext()){
            Variable var = iter.next();
            System.out.println(var.getName());
        }
        
    }
    
     public void printMemoryBlocks(){
        Iterator<MemoryBlock> iter = memoryBlocks.values().iterator();
        System.out.println("*** Memory Blocks ***");
        while (iter.hasNext()){
            MemoryBlock memBlock = iter.next();
            System.out.println(memBlock.getName());
        }
        
    }
    



}
