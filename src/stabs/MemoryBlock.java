
package stabs;

/* 
 * @author Peter Majorin
 *
 * a .comm block, which wraps a type in itself
 * This can be a global array or a global struct both can also static
 */


public class MemoryBlock{
    private String name;
    private long size;
    private long alignment;
    private Type type;

    public MemoryBlock(String name, long size, long alignment){
        this.name = name;
        this.size = size;
        this.alignment = alignment;
    }
    
    public String getName(){
        return name;
    }
    
    public long getSize(){
        return size;
    }
    
    public long getAlignment(){
        return alignment;
    }

    public Type getType(){
        return type;
    }

    public void setType(Type type){
        this.type = type;
    }
}


