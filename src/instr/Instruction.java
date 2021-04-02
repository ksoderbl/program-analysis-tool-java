package instr;

//import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import cfg.CFGNode;
import input.Input;
import input.InputLineObject;
import program.Label;
import program.BranchTarget;
import pseudoOp.PseudoOp;
import microinstr.Microinstruction;
import machine.Machine;
import program.TiwariResults;


public class Instruction implements InputLineObject
{
    /** address of instruction */
    private Long addr;

    /** machine code as string */
    private String machineCode;

    /** size of instruction machine code in bytes */
    private int size;

    /** the input file */
    private Input input;

    /** line number */
    private int line;



    /** next instruction in memory after this branch */
    private CFGNode next;

    /** target node of the branch */
    private CFGNode target;


    /** the operations this instruction consists of */
    private ArrayList operations = new ArrayList();

    ////////////////////////////////////////////////////////////////////////        
    /** the amount of executions of this instructions in the simulation */
    private int executions;

    ////////////////////////////////////////////////////////////////////////
    
    


    public Instruction() {
    }


    public String getCode() {
        String code = "";
        int numops = 0;
        Iterator iter = operations.iterator();
        while (iter.hasNext()) {
            Operation op = (Operation)iter.next();
            if (op == null)
                continue;
            if (numops > 0)
                code += " && ";
            code += op.getMnemonic();
            if (op.args != null)
                code += " " + op.getArgs();
            numops++;
        }
        return code;
    }



    public void addOperation(Operation op) {
        operations.add(op);
    }
    public void addOperation(String mnemonic, List args, String syntax) {
        this.addOperation(new Operation(mnemonic, args, syntax));
    }

    public Operation getOperation(int index) {
        try {
            Operation oper = (Operation)operations.get(index);
            return oper;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public ArrayList getOperations() {
        return operations;
    }


    public Long getAddr() {
        return addr;
    }

    public void setAddr(Long addr) {
        this.addr = addr;
    }

    public void setAddr(String addr) {
        this.addr = Long.valueOf(addr, 16); // assume hex
    }


    // e.g "0045aa" or "b506_98"
    public void setMachineCode(String code, int size) {
        this.machineCode = code;
        this.setSize(size);
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    // input file and line
    public void setInput(Input input) {
        this.input = input;
    }

    public Input getInput() {
        return input;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }






    // stuff needed for interface InputLineObject
    public Instruction getInstruction() {
        return this;
    }
    public Label getLabel() {
        return null;
    }
    public PseudoOp getPseudoOp() {
        return null;
    }



    // override in subclasses
    /**
     * @return regs used by the instruction
     */
    public List getUses() {
        return null; //uses;
    }

    /**
     * @return regs defined by the instruction
     */
    public List getDefs() {
        return null; //defs;
    }




    /**
     * @return target node of the branch
     */
    public CFGNode getTarget() {
        return target;
    }

    /**
     * Sets the branch target.
     *
     * @param target target node of the branch
     */
    public void setTarget(CFGNode target) {
        this.target = target;
    }




    /**
     * @return next instruction in memory after this branch
     */
    public CFGNode getNext() {
        return next;
    }

    /**
     * Sets next instruction in memory after this branch.
     *
     * @param next next instruction in memory after this branch
     */
    public void setNext(CFGNode next) {
        this.next = next;
    }

    public boolean isProgramEntry() {
        return false;
    }

    public boolean isProgramExit() {
        return false;
    }








    /**
     * @return contents of the instruction as a String
     */
    public String toString() {
        if (getTarget() == null) {
            return Long.toHexString(getAddr()) + ": " + getCode();
        } else {
            return Long.toHexString(getAddr()) + ": " + getCode()
                + " (branches " /*+ offset + " steps "*/ + " to " +
                getTarget().getName() + ")";
        }
    }

    /**
     * Function for emitting the instruction.
     *
     * @return Instruction as formatted string to emit
     */
    public String emit() {
        return "Instruction.emit: override in subclass";
    }


    // stuff for generic instruction interface
    public boolean isBranch() {
        Iterator iter = operations.iterator();
        while (iter.hasNext()) {
            Operation op = (Operation)iter.next();
            if (op != null && op.isBranch())
                return true;
        }
        return false;
    }

    public BranchTarget getBranchTarget() {
        Iterator iter = operations.iterator();
        BranchTarget bt = null;
        while (iter.hasNext()) {
            Operation op = (Operation)iter.next();
            if (op == null)
                continue;
            bt = op.getBranchTarget();
            if (bt != null)
                return bt;
        }
        return null;
    }

    public boolean isCall() {
        Iterator iter = operations.iterator();
        while (iter.hasNext()) {
            Operation op = (Operation)iter.next();
            if (op != null && op.isCall())
                return true;
        }
        return false;
    }

    public boolean isReturn() {
        Iterator iter = operations.iterator();
        while (iter.hasNext()) {
            Operation op = (Operation)iter.next();
            if (op != null && op.isReturn())
                return true;
        }
        return false;
    }


    public boolean isCondBranch() {
        Iterator iter = operations.iterator();
        while (iter.hasNext()) {
            Operation op = (Operation)iter.next();
            if (op != null && op.isCondBranch())
                return true;
        }
        return false;
    }

    public boolean isCondCall() {
        Iterator iter = operations.iterator();
        while (iter.hasNext()) {
            Operation op = (Operation)iter.next();
            if (op != null && op.isCondCall())
                return true;
        }
        return false;
    }

    public boolean isCondReturn() {
        Iterator iter = operations.iterator();
        while (iter.hasNext()) {
            Operation op = (Operation)iter.next();
            if (op != null && op.isCondReturn())
                return true;
        }
        return false;
    }

    public void setBranch(boolean conditional) {
        throw new IllegalArgumentException("Instruction.setBranch must be "
                                           + "overridden.");
    }
    public void setBranchTarget(String label) {
        throw new IllegalArgumentException("Instruction.setBranchTarget "
                                           + "must be overridden.");
    }
    public void setCall(boolean conditional) {
        throw new IllegalArgumentException("Instruction.setCall must be "
                                           + "overridden.");
    }
    public void setReturn(boolean conditional) {
        throw new IllegalArgumentException("Instruction.setReturn must be "
                                           + "overridden.");
    }

    ////////////////////////////////////////////////////////////////////////
    public void addExecutions(){
        executions++;
    }
    
    public int getExecutions(){
        return executions;
    }

    //must be overridden in implementing classes
    public void addParallelMicroinstr(Microinstruction mi) {;
    }

    //must be overridden in implementing classes
    
    public List getParallelMicroinstrs(Machine machine){
        return null;
    }


    /** results for tiwari energy estimation */
    private TiwariResults tiwariResults;
    public void setTiwariResults(TiwariResults tiwariResults) {
        this.tiwariResults = tiwariResults;
    }
    public TiwariResults getTiwariResults() {
        return tiwariResults;
    }




}
