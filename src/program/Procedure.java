/**
 * Procedure.java
 */

package program;

import cfg.CFGNode;
import input.Input;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * A Procedure of the program.
 *
 * @author Mikko Reinikainen
 */

public class Procedure {
    /** name of the procedure */
    private String name;

    /** entry node of the procedure */
    private CFGNode entry;

    /** exit nodes of the procedure */
    private ArrayList exits;

    /** set of procedures called by this procedure */
    private HashSet calls;

    /** size of procedure in bytes */
    private int byteSize = 0;

    /** priority of procedure */
    private int priority = 0;

    /**
     * Constructs a new collection of procedures.
     *
     * @return the new collection of procedures
     */
    protected Procedure(String name, CFGNode entry) {
        this.name = name;
        this.entry = entry;
        exits = new ArrayList();
        calls = new HashSet();
    }

    /**
     * @return name of the procedure
     */
    public String getName() {
        return name;
    }

    /**
     * @return entry node of the procedure
     */
    public CFGNode getEntry() {
        return entry;
    }

    /**
     * @return exit nodes of the procedure
     */
    public List getExits() {
        return exits;
    }

    /**
     * Adds the specified exit node to the procedure, if it didn't
     * already exist.
     *
     * @param exit the specified exit node
     * @return true if the procedure didn't already contain the
     * specified exit, false if the procedure remained unchanged. 
     */
    public boolean addExit(CFGNode exit) {
        if (exits.contains(exit)) {
            return false;
        }
        return exits.add(exit);
    }

    /**
     * @return set of procedures called by this procedure
     */
    public HashSet getCalls() {
        return calls;
    }

    /**
     * @return true if this procedure calls the specified procedure
     */
    public boolean calls(Procedure callee) {
        return calls.contains(callee);
    }

    /**
     * Adds the specified procedure to the set of procedures called by
     * this procedure.
     *
     * @param callee the called procedure
     * @return true if the set of procedures called by this procedure
     *         did not already contain the specified procedure 
     */
    public boolean addCall(Procedure callee) {
        return calls.add(callee);
    }


    public void addByteSize(int size){
        byteSize +=size;
    }

    public int getByteSize(){
        return byteSize;
    }
    
    public void setPriority(int pr){
        priority = pr;
    }
    
    public int getPriority(){
        return priority;
    }

    /**
     * @return description of the procedure as a String
     */
    public String toString() {
        String callsNames = "";
        Iterator iter = calls.iterator();
        if (iter.hasNext()) {
            callsNames = ((Procedure) iter.next()).getName();
        }
        while (iter.hasNext()) {
            callsNames =
                callsNames + ", " + ((Procedure) iter.next()).getName();
        }
        return "[Procedure " + getName() + ", entry=" + entry +", size="+ byteSize
            + ", exits=" + exits + ", calls={" + callsNames + "}]";
    }

    /** energy estimation results */
    private TiwariResults tiwariResults;
    public void setTiwariResults(TiwariResults tiwariResults) {
        this.tiwariResults = tiwariResults;
    }
    public TiwariResults getTiwariResults() {
        return tiwariResults;
    }

}
