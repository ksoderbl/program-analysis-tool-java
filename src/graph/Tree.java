/**
 * Tree.java
 */

package graph;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;


/**
 * A general Tree implementation. This uses same Nodes as
 * Graph
 *
 * @author Peter Majorin
 * @see Node
 * @see Edge
 */

public class Tree extends Graph {
    
    private Node rootNode = null;
    
    public Tree(String name) {
        super(name);
    }

    public boolean isParent(Node node, Node parentNode){
        while ((node = node.getParent()) != null){
            //System.out.println(node.getName() +" "+ parentNode.getName());
            if (node.getName().equals(parentNode.getName()))
                return true;
            
        }
        return false;
    }

    // this code should actually be made in a dominatortree that extends this Tree */   
    public boolean dominates(Node node, Node parentNode){
        // a node in a dominator tree always dominates itself 
        if (node.getName().equals(parentNode.getName())) return true;
        
        while ((node = node.getParent()) != null){
            //System.out.println(node.getName() +" "+ parentNode.getName());
            if (node.getName().equals(parentNode.getName()))
                return true;
            
        }
        return false;
    }
    
    
    public Node getRootNode(){
        return rootNode;
    }
    
    public void setRootNode(Node node){
        rootNode = node;
    }
    
}
