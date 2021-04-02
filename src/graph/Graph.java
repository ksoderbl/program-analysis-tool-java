/**
 * Graph.java
 */

package graph;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;


/**
 * A general graph implementation.
 *
 * @author Mikko Reinikainen
 * @see Node
 * @see Edge
 */

public class Graph {

    /** name of this graph */
    private String name;

    /** graph edges */
    private List edges;

    /** graph nodes */
    private List nodes;

    /**
     * Constructs a new graph.
     *
     * @param _name name of the graph
     * @return the new graph
     */
    public Graph(String _name) {
        name = _name;
        nodes = new ArrayList();
        edges = new ArrayList();
    }

    /**
     * @return name of the graph
     */
    public String getName() {
        return name;
    }

    /**
     * @return graph edges
     */
    public List getEdges() {
        return edges;
    }

    /**
     * @return graph nodes
     */
    public List getNodes() {
        return nodes;
    }

    /**
     * Adds the specified edge to this graph.
     *
     * @param edge edge to be added
     * @return true if this graph didn't already contain the specified edge
     */
    public boolean addEdge(Edge edge) {
        return edges.add(edge);
    }

    /**
     * Removes the specified edge from this graph. Note! Does not remove
     * the edge from the start and end node.
     *
     * @param edge edge to be removed
     * @return true if this graph contained the specified edge
     */
    public boolean removeEdge(Edge edge) {
        if (edge != null) {
            return edges.remove(edge);
        } else {
            return false;
        }
    }

    /**
     * Adds the specified node to this graph.
     *
     * @param node node to be added
     * @return true if this graph didn't already contain the specified node
     */
    public boolean addNode(Node node) {
        return nodes.add(node);
    }

    /**
     * Removes the specified node from this graph.
     *
     * @param node node to be removed
     * @return true if this graph contained the specified node
     */
    public boolean removeNode(Node node) {
        return nodes.remove(node);
    }

    /**
     * Return true if this graph contains an edge from Node start to
     * Node end.
     *
     * @param start start node of the edge
     * @param end end node of the edge
     * @return true if this graph contains an edge from Node start to
     *         Node end 
     */
    public boolean containsEdge(Node start, Node end) {
        Iterator iter = getEdges().iterator();
        while (iter.hasNext()) {
            Edge edge = (Edge)iter.next();
            if (edge.getStart().equals(start)
                && edge.getEnd().equals(end)) {
                return true;
            }
        }
        return false;
    }

    public Edge findEdge(Node start, Node end){
         List inBranches = end.getIncomingEdges();
            Iterator edgeIter = inBranches.iterator();
            while (edgeIter.hasNext()){
                Edge edge = (Edge)edgeIter.next();
                if (edge.getStart().equals(start))
                   return edge;
            }
        return null;
    }




    public boolean isBackEdge(Edge edge){
        System.out.print(edge.getStart().getDFSNum()+ " " + edge.getEnd().getDFSNum()+" ");
        System.out.println(edge.getStart().getDFSEndNum()+ " " + edge.getEnd().getDFSEndNum());
        
        if ((edge.getStart().getDFSNum() > edge.getEnd().getDFSNum()) &&
            (edge.getStart().getDFSEndNum() < edge.getEnd().getDFSEndNum()))
            return true;
        return false;
    }
}
