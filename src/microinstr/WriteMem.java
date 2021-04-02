
package microinstr;

import program.Program;
import machine.Machine;
import machine.Register;
import c55x.instr.C55xRegisterOperand;

public class WriteMem extends Microinstruction{
    
    long value = 0;
    long offset = 0;
    MicroOperand source;
    MicroOperand dest;
    C55xRegisterOperand op;
    int sourceSize = -1;
    int sourceShift = 0;

    public WriteMem(MicroOperand source, MicroOperand dest){
        super("WriteMem");
        this.source = source;
        this.dest = dest;
    }

    // use this constructor  for HI, LO, pair etc.
    public WriteMem(MicroOperand source, MicroOperand dest, C55xRegisterOperand op){
        super("WriteMem");
        this.source = source;
        this.dest = dest;
        this.op = op;
    }

    // use this constructor to write specific bits from source
    public WriteMem(MicroOperand source, int size, int shift, MicroOperand dest) {
        super("WriteMem");
        this.source = source;
        this.dest = dest;
        this.sourceSize = size;
        this.sourceShift = shift;
    }
        
    public WriteMem(MicroOperand source, MicroOperand dest, long offset){
        super("WriteMem");
        this.source = source;
        this.dest = dest;
        this.offset = offset;
    }

    public void execute(Program program, Machine machine){
        long sourceValue = source.getValue();
        Register mmReg;
        boolean debug = program.getOptions().getDebugMC();

        if (op != null){        
                if (op.getHiMod())
                sourceValue = BitUtils.readData(sourceValue, 16, 16);          
        }

        // kps - same as above, but for shift op, perhaps should have
        // an abstract C55xOperand?
        if (sourceSize != -1) {
            sourceValue = BitUtils.readData(sourceValue, sourceSize, sourceShift);
        }

        if ((mmReg = machine.getMMREG()) != null) {
            if (debug)
                System.out.println(" WriteMem: mmReg:"+BitUtils.valueToString(sourceValue)
                                   +" to "+mmReg.getName());
            mmReg.setValue(sourceValue);
        }
        else {
            if (debug)
                System.out.println(" WriteMem: value:"+BitUtils.valueToString(sourceValue)
                                   +" to 0x"+Long.toHexString(dest.getValue()+offset));
            machine.writeMem(dest.getValue()+offset, sourceValue, machine.getDataBankNum());
        }
        
    }
    
}
