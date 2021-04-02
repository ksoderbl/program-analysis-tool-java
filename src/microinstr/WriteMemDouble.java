
package microinstr;

import program.Program;
import machine.Machine;

public class WriteMemDouble extends Microinstruction{
    
    long offset = 0;
    MicroOperand source;
    MicroOperand dest;
    
    public WriteMemDouble(MicroOperand source, MicroOperand dest){
        super("WriteMemDouble");
        this.source = source;
        this.dest = dest;
    }
    
    public WriteMemDouble(MicroOperand source, MicroOperand dest, long offset){
        super("WriteMemDouble");
        this.source = source;
        this.dest = dest;
        this.offset = offset;
    }

    public void execute(Program program, Machine machine){
        long sourceValue = source.getValue();
        boolean debug = program.getOptions().getDebugMC();
        long src = source.getValue();
        long dst = dest.getValue();

        // kps - TODO: this should use offset?

        if (debug)
            System.out.println(" WriteMemDouble: value:0x"+Long.toHexString(sourceValue)
                               + "("+sourceValue+")"
                               +" to 0x"+Long.toHexString(dest.getValue()/*+offset*/)
                               +" offset:0x" + offset);

        long LSW = src & BitUtils.bitMask[16];
        long MSW = src & BitUtils.bitMask[32];
        MSW = MSW >> 16;        

        if (debug && ((dst & 0x1L) == 1))
            System.out.println(" WriteMemDouble: WARNING: odd Lmem address 0x"
                               +Long.toHexString(dst));
        if (debug)
            System.out.println(" WriteMemDouble (cont): MSW:0x" +Long.toHexString(MSW)
                               +" LSW:0x"+Long.toHexString(LSW) 
                               +" destination:0x"+Long.toHexString(dst));
        // kps - use offset here?
        machine.writeMem(dst,   MSW, machine.getDataBankNum());
        machine.writeMem(dst+1, LSW, machine.getDataBankNum());        
    }
    
}
