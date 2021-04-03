/**
 * CFG.java
 */

package cfg;

import graph.Graph;
import graph.Node;
import graph.Edge;
import java.util.Iterator;
import java.util.List;
import instr.Instruction;


/**
 * A control flow graph.
 *
 * @author Mikko Reinikainen
 * @see Graph
 */

public class CFG extends Graph {

    /**
     * Constructs a new control flow graph
     * 
     * @param name name of the graph
     */
    public CFG(String name) {
        super(name);
    }

    /**
     * Returns a new node that belongs to this control flow graph.
     *
     * @param name name of the node
     * @param instruction instruction of the node
     * @return the new node
     */
    public CFGNode createNode(String name,
                              Instruction instruction) {

        CFGNode node = new CFGNode(name, instruction);
        addNode(node);
        return node;
    }

    /**
     * Returns a new edge between nodes start and end of this control
     * flow graph.
     *
     * @param start start node of edge
     * @param end end node of edge
     * @return the new edge 
     */
    public CFGEdge createEdge(CFGNode start, CFGNode end, int type) {
        CFGEdge edge = new CFGEdge(start, end, type);

        addEdge(edge);
        return edge;
    }

    /**
     * Returns the node of this control flow graph with a specified
     * address.
     * 
     * @param _addr address of the node
     * @return node with the specified address 
     */
    /* kps - unused
    public CFGNode getNode(Integer addr) {
        // XXX zzz THIS IS STUPID...
        Iterator iter = getNodes().iterator();

        while (iter.hasNext()) {
            CFGNode node = (CFGNode) iter.next();

            if (node.getInstruction().getAddr().equals(addr)) {
                return node;
            }
        }

        // no node with that address found
        return null;
    }
    */

    /**
     * @return contents of the control flow graph as a string
     */
    public String toString() {
        String result = "---\nCONTROL FLOW GRAPH " + getName()
            + "\n\n" + getNodes().size() + " nodes:\n";

        // add all nodes to the result string
        Iterator<Node> nodeIter = getNodes().iterator();

        while (nodeIter.hasNext()) {
            CFGNode node = (CFGNode)nodeIter.next();

            result = result + node.toString() + "\n";
        }
        result = result + "\n" + getEdges().size() + " edges:\n";

        // add all edges to the result string
        Iterator<Edge> iter = getEdges().iterator();
        while (iter.hasNext()) {
            CFGEdge edge = (CFGEdge) iter.next();

            result = result + edge.toString() + "\n";
        }

        result = result + "---";

        return result;
    }
}
