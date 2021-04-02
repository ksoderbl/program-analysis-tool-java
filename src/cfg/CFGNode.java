/**
 * CFGNode.java
 */

package cfg;

import instr.Instruction;
import analysis.State;
import graph.Node;
import program.Procedure;

/**
 * Node of a control flow graph.
 *
 * @author Mikko Reinikainen
 * @see CFG
 * @see Node
 */
public class CFGNode extends Node {

    /** instruction at this node */
    private Instruction instruction;

    /** program state before executing the instruction at this node */
    private State preState;

    /** procedure which this node belongs to */
    private Procedure procedure;

    /** is this node a procedure call? */
    private boolean call = false;

    /**
     * Constructs a new CFGNode
     *
     * @param name name of the node
     * @param instruction instruction at this node
     * @return the new node
     */
    public CFGNode(String name,
                   Instruction instruction) {
        super(name);
        // kps - there's weird "ldr null" instructions, apparently
        // labels are not visible here
        //System.out.println("Creating CFGNode " + name);
        //System.out.println("    Instr: " + instruction);
        this.instruction = instruction;
        procedure = null;
    }

    /**
     * @return instruction at this node
     */
    public Instruction getInstruction() {
        return instruction;
    }

    /**
     * @return program state before executing the instruction at this node
     */
    public State getPreState() {
        return preState;
    }

    /**
     * Sets the program state before executing the instruction at this node
     *
     * @param preState program state
     */
    public void setPreState(State preState) {
        this.preState = preState;
    }

    /**
     * @return procedure which this node belongs to
     */
    public Procedure getProcedure() {
        return procedure;
    }

    /**
     * Sets the procedure which this node belongs to.
     *
     * @param procedure which this node belongs to
     */
    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    /**
     * @return true if this node is a procedure call
     */
    /*
    public boolean isCall() {
        return call;
    }
    */

    /**
     * Set this node to be a procedure call.
     */
    /*
    public void setCall() {
        call = true;
    }
    */

    /**
     * Set this node not to be a procedure call.
     */
    /*
    public void resetCall() {
        call = false;
    }
    */

    /**
     * @return contents of the node as a String
     */
    public String toString() {
        // kps - OLD
        //return "(CFGnode " + getName() + " at " + instruction.getAddr() +
        //    ": '" + instruction + "', preState=" + preState + ")";
        // kps - not as much chars
        return "" + getName() + ": " + instruction;
    }
}
