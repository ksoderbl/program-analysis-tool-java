package microinstr;

import program.Program;
import machine.Machine;

public class ReadMemDouble extends Microinstruction{
    
    long offset = 0;
    MicroOperand source;
    MicroOperand dest;
    int sourceSize1 = 0;
    int sourceShift1 = 0;
    int sourceSize2 = 0; 
    int sourceShift2 = 0;

    public ReadMemDouble(MicroOperand source, MicroOperand dest){
        super("WriteMem");
        this.source = source;
        this.dest = dest;
    }
    
    public ReadMemDouble(MicroOperand source, MicroOperand dest, long offset){
        super("WriteMem");
        this.source = source;
        this.dest = dest;
        this.offset = offset;
    }

    public ReadMemDouble(MicroOperand source, int sourceSize1, int sourceShift1, int sourceSize2, int sourceShift2, MicroOperand dest){
        super("WriteMem");
        this.source = source;
        this.dest = dest;
        this.sourceSize1 = sourceSize1;   //first 16 bit number from address
        this.sourceShift1 = sourceShift1; 
        this.sourceSize2 = sourceSize2; //second 16 bit number from address
        this.sourceShift2 = sourceShift2;
    }
        
    public void execute(Program program, Machine machine){
        
        boolean debug = program.getOptions().getDebugMC();

        if (debug) System.out.println(" ReadMemDouble: Reading "+Long.toHexString(source.getValue())
                                      +" to "+Long.toHexString(dest.getValue()));
        long src = source.getValue();

        if (debug && ((src & 0x1L) == 1)) {
            System.out.println(" ReadMemDouble: WARNING: odd Lmem address 0x"
                               +Long.toHexString(src));
        }

        long MSW = machine.readMem(src, machine.getDataBankNum());
        long LSW = machine.readMem(src+1, machine.getDataBankNum());

        if (sourceSize1 > 0)
            MSW = BitUtils.readData(MSW, sourceSize1, sourceShift1);

        if (sourceSize2 > 0)
            LSW = BitUtils.readData(LSW, sourceSize2, sourceShift2);

        if (debug) System.out.println(" ReadMemDouble: " + Long.toHexString(MSW)
                                      + " " + Long.toHexString(LSW));        
        MSW = MSW << 16;
        long value = LSW | MSW;
        if (debug) System.out.println(" ReadMemDouble: final value:"+Long.toHexString(value));
        dest.setValue(value);
    }
    
}
