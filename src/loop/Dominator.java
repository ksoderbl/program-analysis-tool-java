
package loop;


import graph.Node;


/**
 * Nodes of a dominator tree
 */

public class Dominator extends Node {
    
    /**
     * the dominator number in the dominator tree
     *
     */
    private int number;
    
    public Dominator(String name){
        super(name);
    }



    public int getNumber() {
        return number;
    }
    
    public void setNumber(int num) {
        number = num;
    }
}
