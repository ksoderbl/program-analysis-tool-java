package microinstr;

import program.Program;
import machine.Machine;
import machine.Register;
import machine.RepeatLoop;
import c55x.instr.C55xProgramAddressOperand;
import program.*;

public class Repeat extends Microinstruction{
    
    long counter;
    boolean rpt; // if false repeat single instruction, if true block repeat
    boolean loadRptc; // use if this single repeat loads rptc with CSR
    long endAddr = 0;

    C55xProgramAddressOperand addressOp = null;
    MicroRegisterOperand reg = null;
    
    // use for rpt with a register
    public Repeat(MicroRegisterOperand reg){
        super("Repeat");
        this.reg = reg;
        this.rpt = true;
    }
    

    // use for e.g rpt #23, long argument machine to distinguish constructors
    public Repeat(long counter){
        super("Repeat");
        this.counter = counter;
        this.rpt = true;
    }
    
    // for blockrepeats, better use this always for rptb to get the branchtarget right with labels
    public Repeat(C55xProgramAddressOperand addressOp){
        super("Repeat");
        //this.endAddr = op.getBranchTarget().getAddress(program).longValue();
        this.addressOp = addressOp;
               this.rpt = false;
    }
    


   
    public void execute(Program program, Machine machine){
        boolean debug = program.getOptions().getDebugMC();
        Register loopReg;
        RepeatLoop repeat;
        if (debug) System.out.println(" bloopcounters:"+machine.getNumLoopCounters());

        if (rpt){
            if (reg != null) counter = reg.getValue();
            this.endAddr = machine.getNextInstructionAddr();
            loopReg = machine.getRegister("rptc");
            loopReg.setValue(this.counter); // init rptc
            repeat = new RepeatLoop(loopReg, this.endAddr, this.endAddr);
            //System.out.println("End addr:"+Long.toHexString(this.endAddr));
            machine.addLoopCounter(repeat);
            if (debug)
                System.out.println(" Repeatsingle: rptc loaded with:"+this.counter+ " " + repeat);
            return;
        }
        
        
        // endAddr for repeats can be either a label+offset or only address
        Symbols symbols = program.getLabels();
        if (addressOp.getLabel() != null){
            Long labelAddress = (Long)symbols.get(addressOp.getLabel());
            endAddr = labelAddress.longValue();
            endAddr += addressOp.getOffset();        
        }
        else endAddr = addressOp.getOffset();
        
        switch (machine.getNumLoopCounters()){

        case 0:
            loopReg = machine.getRegister("brc0");
            break;
        case 1:
            Register brs1 = machine.getRegister("brs1");
            loopReg = machine.getRegister("brc1");
            loopReg.setValue(brs1.getValue());
            break;
        default:
            throw new IllegalArgumentException("Repeat: too many repeats for c55x");
        }

        if (debug)
            System.out.println(" Repeat: loopcounters:"+machine.getNumLoopCounters()
                               +" endaddr:"+
                               Long.toHexString(endAddr));
        repeat = new RepeatLoop(loopReg, machine.getNextInstructionAddr(), 
                                endAddr);
        machine.addLoopCounter(repeat);
        if (debug)
            System.out.println(" Repeat: " + repeat);
    }
}
