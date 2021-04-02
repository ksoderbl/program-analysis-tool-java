package microinstr;

import program.Program;
import machine.Machine;
import instr.Operation;

public class C55xUnconditionalBranch extends Microinstruction{
    
    Operation op;
  
    public C55xUnconditionalBranch(Operation op){
        super("C55xCall");
        this.op = op;
    }


    public void execute(Program program, Machine machine){
            machine.setNextPCAddr(op.getBranchTarget().getAddress(program).longValue()); 
    }
    
}
