
package microinstr;

import program.Program;
import machine.Machine;
import machine.Register;
import c55x.instr.C55xBitOperand;



public class C55xBitOperation extends Microinstruction {
    String operation;
    int index;
    MicroOperand source;
    MicroOperand dest;
    MicroOperand source2;
    MicroOperand dest2;
  
    public C55xBitOperation(MicroOperand source, MicroOperand dest,
                            String operation, int index) {
        super("C55xBitOperation");
        this.source    = source;
        this.dest      = dest;
        this.operation = operation;
        this.index     = index;
    }

    // BTST
    public C55xBitOperation(MicroOperand source, MicroOperand dest, // which bit to test
                            MicroOperand source2, MicroOperand dest2, // where to set result
                            String operation, int index) {
        super("C55xBitOperation");
        this.source    = source;
        this.dest      = dest;
        this.source2   = source2;
        this.dest2     = dest2;
        this.operation = operation;
        this.index     = index;
    }

    public void execute(Program program, Machine machine){
        boolean debug = program.getOptions().getDebugMC();
        int bitNum = (int)source.getValue();
        long result = 0;
        long value = dest.getValue();

        if (operation.equals("BCLR")) {
            result = BitUtils.clearBit(value, bitNum);
        }        

        else if (operation.equals("BSET")){
            result = BitUtils.setBit(value, bitNum);
        }

        else if (operation.equals("BTST")){
            int bitNum2 = (int)source2.getValue();
            long value2 = dest2.getValue();
            long test = BitUtils.testBit(value, bitNum);
            if (test == 1)
                result = BitUtils.setBit(value2, bitNum2);
            else
                result = BitUtils.clearBit(value2, bitNum2);
        }

        else {
            throw new NullPointerException("unknown operation: " + operation);
        }

        if (source2 == null && debug)
            System.out.println(" C55xBitOperation: "
                               + operation
                               + " src:"
                               + BitUtils.valueToString2(source.getValue())
                               + " dst:"
                               + BitUtils.valueToString2(dest.getValue())
                               + " result:"
                               + BitUtils.valueToString2(result)
                               + " index:"+(index+machine.getMachineResultOffset()));
        if (source2 != null && debug)
            System.out.println(" C55xBitOperation: "
                               + operation
                               + " src:"
                               + BitUtils.valueToString2(source.getValue())
                               + " dst:"
                               + BitUtils.valueToString2(dest.getValue())
                               + " src2:"
                               + BitUtils.valueToString2(source2.getValue())
                               + " dst2:"
                               + BitUtils.valueToString2(dest2.getValue())
                               + " result:"
                               + BitUtils.valueToString2(result)
                               + " index:"+(index+machine.getMachineResultOffset()));

        machine.setDataResult(result, 64, index);
    }

}
