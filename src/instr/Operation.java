
package instr;

import java.util.List;
import java.util.Iterator;
import program.BranchTarget;
import microinstr.Microinstruction;
import machine.Machine;

//
// http://en.wikipedia.org/wiki/VLIW
// In instruction level parallelism (ILP), each instruction can
// contain multiple operations. One operation contains one opcode mnemonic
// and its operands.
// 

public class Operation
{
    String mnemonic; // opcode mnemonic
    List<Operand> args;
    String syntax;


    public String getMnemonic() {
        return mnemonic;
    }

    public List<Operand> getArgs() {
        return args;
    }

    public String getSyntax() {
        return syntax;
    }


    public Operation(String mnemonic, List<Operand> args, String syntax) {
        this.mnemonic = mnemonic;
        this.args = args;
        this.syntax = syntax;
    }

    public boolean hasMemoryAccess() {
        Iterator<Operand> iter = args.iterator();
        while (iter.hasNext()) {
            Operand oper = iter.next();
            if (oper.isMemoryAccess())
                return true;
        }
        return false;
    }

    // must be overridden ;microInstructions of the instruction
    public List<Microinstruction> getMicroinstrs(Machine machine) {
        return null;
    }

    // must be overridden
    public void addMicroinstr(Microinstruction mi) {
        return;
    }



    //
    // STUFF NEEDED FOR CFG
    //
    private boolean branch = false;
    private boolean call = false;
    private boolean ret = false;

    private boolean condBranch = false;
    private boolean condCall = false;
    private boolean condRet = false;

    private BranchTarget branchTarget = null;
    

    public void setBranch(boolean conditional) {
        branch = true;
        if (conditional)
            condBranch = true;
    }
    public void setCall(boolean conditional) {
        call = true;
        branch = true; // call is a sort of branch
        if (conditional) {
            condBranch = true;
            condCall = true;
        }
    }
    public void setReturn(boolean conditional) {
        ret = true;
        if (conditional)
            condRet = true;
    }

    public void setBranchTarget(BranchTarget branchTarget) {
        this.branchTarget = branchTarget;
    }
    public void setBranchTarget(String label) {
        this.branchTarget = new BranchTarget(label);
    }
    public void setBranchTarget(String label, Long offset) {
        this.branchTarget = new BranchTarget(label, offset);
    }
    public void setBranchTarget(Long addr) {
        this.branchTarget = new BranchTarget(addr);
    }

    public BranchTarget getBranchTarget() {
        return branchTarget;
    }


    public boolean isBranch() {
        return branch;
    }
    public boolean isCall() {
        return call;
    }
    public boolean isReturn() {
        return ret;
    }

    public boolean isCondBranch() {
        return condBranch;
    }
    public boolean isCondCall() {
        return condCall;
    }
    public boolean isCondReturn() {
        return condRet;
    }
}
