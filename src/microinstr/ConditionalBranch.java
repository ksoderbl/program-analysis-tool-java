package microinstr;

import program.Program;
import machine.Machine;
import instr.Operation;

public class ConditionalBranch extends Microinstruction{
    
    Operation operation;

    public ConditionalBranch(Operation operation){
        super("ConditionalBranch");
        this.operation = operation;
    }

    public void execute(Program program, Machine machine){
        long addr;
        boolean debug = program.getOptions().getDebugMC();
        addr = operation.getBranchTarget().getAddress(program).longValue();
       
        if (machine.getCondition()){ 
            //System.out.println("ConditionalBranch taken");
            if (debug) System.out.println(" ConditionalBranch to "
                                          + Long.toHexString(addr) + " taken");
            machine.setNextPCAddr(addr);
        }
        else {
            if (debug) System.out.println(" ConditionalBranch not taken");
        }
    }
    
}
