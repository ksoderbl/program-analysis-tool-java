package scratchpad;
import java.util.Iterator;


public interface CodeAllocationObject{
    
    /* the space the object takes */
    public int getWeight();
    /* the cost of executing this code in object in memory (energy*executions) */
    public int getCost(double spmEnergy, double mainMemEnergy);
    /* for debugging purposes */
    public void setCost(int cost);
    /* name of object, this is the name of basic blocks or procedures, etc. */
    public String getName();
    /* set the allocation for this object to true */
    public void setAllocated();
}
