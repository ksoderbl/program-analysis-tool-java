package microinstr;

import program.Program;
import machine.Machine;
import machine.Register;
import instr.Operation;


public class C55xCall extends Microinstruction{

    Operation op;
    MicroOperand reg;

    public C55xCall(Operation op){
        super("C55xCall");
        this.op = op;
    }

    public C55xCall(MicroOperand reg){
        super("C55xCall");
        this.reg = reg;
    }
    
    
    public void execute(Program program, Machine machine){
        long addr;
        // call with label - would be better if the op was a MicroOperand too -pgm
        if (op !=null) addr = op.getBranchTarget().getAddress(program).longValue();
        // call with reg
        else addr = reg.getValue();
        
        boolean debug = program.getOptions().getDebugMC();
        Register sp = machine.getRegister("sp");
        sp.setValue(sp.getValue() - 1);
        machine.writeMem(sp.getValue(), machine.getNextPCAddr(), machine.getDataBankNum());
        if (debug) System.out.println("Call: "+Long.toHexString(addr));
        machine.setNextPCAddr(addr);        
    }
    
}
