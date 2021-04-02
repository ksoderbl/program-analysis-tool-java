
package microinstr;

import program.Program;
import machine.Machine;
import machine.Register;
import c55x.C55xMachine;

public class ReadMem extends Microinstruction {

    MicroOperand sourceOp;
    MicroOperand destOp;

    public static final int sxUnsigned = 0;
    public static final int sxSigned = 1;
    public static final int sxUseSXMD = 2; // look at SXMD whether to sign extend sourceop
    int signExtend = sxUnsigned;

    long offset = 0;

    public ReadMem(MicroOperand source, MicroOperand dest) {
        super("ReadMem");
        sourceOp = source;
        destOp = dest;
    }

    public ReadMem(MicroOperand source, MicroOperand dest, long offset) {
        super("ReadMem");
        sourceOp = source;
        destOp = dest;
        this.offset = offset;
    }

    public void setSignExtend(int sx) {
        this.signExtend = sx;
    }


    private long handleSignExtend(Machine machine, long value) {
        if (value < 0) {
            // 64 bits already, no need to extend
            return value;
        }
        if (signExtend == sxUnsigned)
            return value;
        else if (signExtend == sxSigned)
            return BitUtils.extendSigned(value, 16, 64);
        else if (signExtend == sxUseSXMD) {
            C55xMachine c55x = (C55xMachine)machine;
            if (c55x.getSXMD() == 0) { // zero extend
                return value;
            }
            else { // sign extend
                return BitUtils.extendSigned(value, 16, 64);
            }
        }
        else
            throw new NullPointerException("handleSignExtend: unknown sign extend mode "
                                           + signExtend);

    }

    public void execute(Program program, Machine machine) {
        long memValue = 0;
        boolean debug = program.getOptions().getDebugMC();
        Register mmReg;

        if ((mmReg = machine.getMMREG()) != null) {
            memValue = mmReg.getValue();
            //System.out.println("READMEM: read value " + BitUtils.valueToString(memValue));
            //memValue = handleSignExtend(machine, memValue);
            destOp.setValue(memValue);
            if (debug)
                System.out.println(" ReadMem: mmReg:"+BitUtils.valueToString(memValue)
                                   +" name:"+mmReg.getName()
                                   + " to "+destOp.getName());
        }
        else {
            // normal read from memory
            memValue = machine.readMem(sourceOp.getValue()+offset, machine.getDataBankNum());
            //System.out.println("READMEM: read value " + BitUtils.valueToString(memValue));
            memValue = handleSignExtend(machine, memValue);
            destOp.setValue(memValue);
            if (debug)
                System.out.println(" ReadMem: address:0x"
                                   +Long.toHexString(sourceOp.getValue()+offset)
                                   + " value " + BitUtils.valueToString(memValue)
                                   + " to "+destOp.getName());
        }
    }
    
}
