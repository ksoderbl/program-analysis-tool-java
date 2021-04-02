package microinstr;

import program.Program;
import machine.Machine;
import machine.Register;
import c55x.C55xMachine;

public class C55xMantissa extends Microinstruction {
    MicroOperand source;
    int index = 0;

    public C55xMantissa(MicroOperand source, int index) {
        super("C55xMantissa");
        this.source = source;
        this.index = index; // index to restore result
    }
    
    public void execute(Program program, Machine machine) {
        boolean debug = program.getOptions().getDebugMC();
        long value = source.getValue();
        long result = 0;

        if (value == 0)
            result = 0;
        else {
            while (value > 0x7fffffff)
                value >>= 1;
            while (value <= 0x3fffffff)
                value <<= 1;
            result = value;
        }

        if (debug) System.out.println(" C55xMantissa:"
                                      + " source:0x"+Long.toHexString(source.getValue())
                                      + "("+source.getValue()+")"
                                      + " result:"+Long.toHexString(result)
                                      + "("+result+")"
                                      + " index:"+(index+machine.getMachineResultOffset()));

        machine.setDataResult(result, 64, index);
    }
    
}
