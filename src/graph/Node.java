/**
 * Node.java
 */

package graph;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;


/**
 * Node of a graph.
 *
 * @author Mikko Reinikainen
 * @see Graph
 * @see Edge
 */

public class Node {

    

    /** name of the node */
    private String name;

    /** edges arriving at this node */
    private List<Edge> incomingEdges;

    /** edges leaving this node */
    private List<Edge> outgoingEdges;

    /** parents for tree-graphs */
    private Node parentNode;

    /* for depth-first search in a graph */
    private int dfsNum;

    /* for depth-first search in a graph, end number */
    private int dfsEndNum;
    

    /**
     * Constructs a new node.
     *
     * @param _name name of the node
     * @return the new node
     */
    public Node(String _name) {
        name = _name;
        incomingEdges = new ArrayList<Edge>();
        outgoingEdges = new ArrayList<Edge>();
    }

    /**
     * @return name of the node
     */
    public String getName() {
        return name;
    }

    /**
     * @return edges arriving at this node
     */
    public List<Edge> getIncomingEdges() {
        return incomingEdges;
    }

    /**
     * @return edges leaving this node
     */
    public List<Edge> getOutgoingEdges() {
        return outgoingEdges;
    }

    /**
     * Adds an edge arriving at this node.
     *
     * @param incoming edge arriving at this node
     * @return true if the specified edge was not already an incoming
     *         edge of this node
     */
    public boolean addIncomingEdge(Edge incoming) {
        return incomingEdges.add(incoming);
    }

    /**
     * Removes an edge arriving at this node.
     *
     * @param incoming edge arriving at this node
     * @return true if the specified edge was an incoming edge of this node
     */
    public boolean removeIncomingEdge(Edge incoming) {
        return incomingEdges.remove(incoming);
    }

    /**
     * Adds an edge leaving this node.
     *
     * @param outgoing edge leaving this node
     * @return true if the specified edge was not already an outgoing
     *         edge of this node
     */
    public boolean addOutgoingEdge(Edge outgoing) {
        return outgoingEdges.add(outgoing);
    }

    /**
     * Removes an edge leaving this node.
     *
     * @param outgoing edge leaving this node
     * @return true if the specified edge was an outgoing edge of this node
     */
    public boolean removeOutgoingEdge(Edge outgoing) {
        return outgoingEdges.remove(outgoing);
    }

    /**
     * Removes all edges leaving this node.
     *
     * @return true if any edges were removed
     */
    public boolean removeIncomingEdges(Graph graph) {
        boolean result = !outgoingEdges.isEmpty();

        Iterator<Edge> iter = incomingEdges.iterator();
        while (iter.hasNext()) {
            Edge edge = iter.next();
            edge.getStart().removeOutgoingEdge(edge);
            graph.removeEdge(edge);
            result = true;
        }

        incomingEdges.clear();

        return result;
    }

    /**
     * Removes all edges leaving this node.
     *
     * @return true if any edges were removed
     */
    public boolean removeOutgoingEdges(Graph graph) {
        boolean result = !outgoingEdges.isEmpty();

        Iterator<Edge> iter = outgoingEdges.iterator();
        while (iter.hasNext()) {
            Edge edge = iter.next();
            edge.getEnd().removeIncomingEdge(edge);
            graph.removeEdge(edge);
            result = true;
        }

        outgoingEdges.clear();

        return result;
    }
    public void setParent(Node n){
        parentNode = n;
    }

    public Node getParent(){
        return parentNode;
    }

    public void setDFSNum(int num){
        dfsNum = num;
    }

    public int getDFSNum(){
        return dfsNum;
    }

    public void setDFSEndNum(int num){
        dfsEndNum = num;
    }

    public int getDFSEndNum(){
        return dfsEndNum;
    }
}

