package microinstr;

import program.Program;
import machine.Machine;
import machine.Register;
import c55x.C55xMachine;

public class C55xMin extends Microinstruction {
    MicroOperand source;
    MicroOperand destination;
    int index = 0;

    public C55xMin(MicroOperand source, MicroOperand destination, int index) {
        super("C55xMin");
        this.source = source;
        this.destination = destination;
        this.index = index; // index to restore result
    }
    
    public void execute(Program program, Machine machine) {
        boolean debug = program.getOptions().getDebugMC();
        long src = source.getValue();
        long dst = destination.getValue();
        long result = 0;

        Register reg1 = source.getRegister();
        Register reg2 = destination.getRegister();

        int bitSize1 = reg1.getBitSize();
        int bitSize2 = reg2.getBitSize();

        if (bitSize1 != bitSize2)
            throw new NullPointerException("MIN_p320 not implemented for unequal size regs");

        // adjust values here if bitsizes don't match

        C55xMachine c55x = (C55xMachine)machine;
        if (src < dst) {
            result = src;
            c55x.clearCarry();
        }
        else {
            result = dst;
            c55x.setCarry();
        }

        if (debug) System.out.println(" C55xMin:"
                                      + " source:0x"+Long.toHexString(source.getValue())
                                      + "("+source.getValue()+")"
                                      + " destination:0x"+ Long.toHexString(destination.getValue())
                                      + "("+destination.getValue()+")"
                                      + " result:"+Long.toHexString(result)
                                      + "("+result+")"
                                      + " carry:"+c55x.getCarry()
                                      + " index:"+(index+machine.getMachineResultOffset()));

        machine.setDataResult(result, 64, index);
    }
    
}
