/**
 * BasicBlockEdge.java
 */

package basicblocks;

import graph.Node;
import graph.Edge;
import program.TiwariResults;

/**
 * Edge of a basicblock flow graph.
 *
 * @author Peter Majorin
 * @see CFG
 * @see Edge
 */

public class BasicBlockEdge extends Edge {

    /** energy estimation results */
    private TiwariResults tiwariResults;
    public void setTiwariResults(TiwariResults tiwariResults) {
        this.tiwariResults = tiwariResults;
    }
    public TiwariResults getTiwariResults() {
        return tiwariResults;
    }
    /** type of edge */
    private int type;

    /** the dynamic executions going through this Edge */
    private int dynamicExecutions = 0;
    
     /**
     * Constructs a new basicblock edge
     *
     * @param start start node of the edge
     * @param end end node of the edge
     * @return the new control flow graph edge
     */
    protected BasicBlockEdge(BasicBlock start,
                             BasicBlock end,
                             int type) {
        super(start, end);
        this.type = type;
    }

    /**
     * @return type of this edge
     */
    public int getType() {
        return type;
    }

    public int getExecutions(){
        return dynamicExecutions;
    }
    
    public void setExecutions(int executions){
        dynamicExecutions = executions;
    }
    
    public String toString() {
        String edgeString = "" + this.getStart().getName() +
            " -> " + this.getEnd().getName();
        edgeString += " (num executions: " + this.getExecutions() + ")";
        return edgeString;
    }
}
