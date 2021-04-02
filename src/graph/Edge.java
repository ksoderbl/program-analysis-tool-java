/**
 * Edge.java
 */

package graph;

import main.*;

/**
 * Edge of a graph.
 *
 * @author Mikko Reinikainen
 * @see Graph
 * @see Node
 */

public class Edge {
    /** possible edge types */
    public static final int EdgeFallTrough = 1;
    public static final int EdgeBranchTaken = 2;
    public static final int EdgeCall = 3;
    public static final int EdgeReturn = 4;

    /** start node of this edge */
    private Node start;

    /** start node of this edge */
    private Node end;

    /**
     * Constructs a new edge.
     *
     * @param _start start node of the edge
     * @param _end end node of the edge
     * @return the new edge
     */
    public Edge(Node _start, Node _end) {
        start = _start;
        end = _end;

        if (start == null) {
            Main.warn("adding edge from null node");
        } else {
            start.addOutgoingEdge(this);
        }

        if (end == null) {
            Main.warn("adding edge to null node");
        } else {
            end.addIncomingEdge(this);
        }
    }

    /**
     * @return start node of this edge
     */
    public Node getStart() {
        return start;
    }

    /**
     * @return end node of this edge
     */
    public Node getEnd() {
        return end;
    }

    /**
     * @return Contents of this edge as a String.
     */
    public String toString() {
        return "[Edge from " + start + " to " + end + "]";
    }
}
