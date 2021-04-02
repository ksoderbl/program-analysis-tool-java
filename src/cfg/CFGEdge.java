/**
 * CFGEdge.java
 */

package cfg;

import graph.Node;
import graph.Edge;
import analysis.State;

/**
 * Edge of a control flow graph.
 *
 * @author Mikko Reinikainen
 * @see CFG
 * @see Edge
 */

public class CFGEdge extends Edge {

    /** program state at this edge  */

    /** program state at this edge  */
    private State state;

    /** times this edge has been taken during simulation */
    private int executions = 0;

    /** type of edge */
    private int type;

    /**
     * Constructs a new control flow graph edge
     *
     * @param start start node of the edge
     * @param end end node of the edge
     * @param type type of the edge
     * @return the new control flow graph edge
     */
    protected CFGEdge(CFGNode start, CFGNode end, int type) {
        super(start, end);
        state = null;
        this.type = type;
    }

    /**
     * @return type of this edge
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the program state at this edge.
     *
     * @return program state at this edge
     */
    public State getState() {
        return state;
    }

    /**
     * Sets the program state at this edge
     *
     * @param state program state at this edge
     */
    public void setState(State _state) {
        state = _state;
    }


    public void addExecutions(){
        executions++;
    }

    public int getExecutions(){
        return executions;
    }


    /**
     * @return contents of this edge as a string
     */
    public String toString() {
        String result = "(CFGedge ";

        // start node of edge
        Node node = getStart();
        if (node == null) {
            result = result + "<null node>";
        } else {
            result = result + node.getName();
        }
        result = result + " -> ";

        // end node of edge
        node = getEnd();
        if (node == null) {
            result = result + "<null node>";
        } else {
            result = result + node.getName();
        }

        // state of edge
        result = result + ", state=\"" + state + "\")";

        return result;
    }
}
