package microinstr;

import program.Program;
import machine.Machine;
import c55x.instr.C55xRegisterOperand;
import machine.Register;

/**
 * extracts bit from a source operand
 */

public class ExtractSignExtend extends Microinstruction {
    MicroOperand source;
    int sourceSize = 0;
    int sourceShift;
    int index = 0;

    // use if you want to read only specific bits in source operand
    public ExtractSignExtend(MicroOperand source,
                             int sourceSize, int sourceShift,
                             int index) {

        super("ExtractSignExtend");
        this.source = source;
        this.sourceSize = sourceSize;
        this.sourceShift = sourceShift;
        this.index = index;
    }

    public void execute(Program program, Machine machine){
        boolean debug = program.getOptions().getDebugMC();
        long src = source.getValue();
        long result;

        result = BitUtils.readData(src, sourceSize, sourceShift); // extract
        result = BitUtils.extendSigned(result, sourceSize, 64);   // sign extend

        machine.setDataResult(result, 64, index);
        if (debug)
            System.out.println(" ExtractSignExtend: "
                               + " source:" + BitUtils.valueToString(src)
                               + " size:" + sourceSize
                               + " shift:" + sourceShift
                               + " result:" + BitUtils.valueToString(result));
    }
    
}
