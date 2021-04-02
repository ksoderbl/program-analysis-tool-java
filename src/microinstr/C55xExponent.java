package microinstr;

import program.Program;
import machine.Machine;
import machine.Register;
import c55x.C55xMachine;

public class C55xExponent extends Microinstruction {
    MicroOperand source;
    String type;
    int index = 0;

    public C55xExponent(MicroOperand source, String type, int index) {
        super("C55xExponent");
        this.source = source;
        this.type = type;
        this.index = index; // index to restore result
    }
    
    public void execute(Program program, Machine machine) {
        boolean debug = program.getOptions().getDebugMC();
        long value = source.getValue();
        long result = 0;
        int num_leading_bits = 0;

        if (value == 0L) {
            if (type.equals("NEXP"))
                result = 0x8000L;
            if (type.equals("EXP"))
                result = 0L;
        }
        else {
            if (type.equals("NEXP")) {
                while (value >  0x7fffffffffL) {
                    value >>= 1;
                    num_leading_bits--;
                }
                while (value <= 0x3fffffffffL) {
                    value <<= 1;
                    num_leading_bits++;
                }
                result = 8 - num_leading_bits;
            }
            if (type.equals("EXP")) {
                value = BitUtils.extendSigned(value, 40, 64);
                if (value < 0L)
                    value = -value;
                while (value <= 0x3fffffffffL) {
                    value <<= 1;
                    num_leading_bits++;
                }
                result = num_leading_bits - 8;
            }
        }

        if (debug) System.out.println(" C55xExponent:"
                                      + " type:" + type
                                      + " source:0x"+Long.toHexString(source.getValue())
                                      + "("+source.getValue()+")"
                                      + " result:"+Long.toHexString(result)
                                      + "("+result+")"
                                      + " index:"+(index+machine.getMachineResultOffset()));

        machine.setDataResult(result, 64, index);
    }
    
}
