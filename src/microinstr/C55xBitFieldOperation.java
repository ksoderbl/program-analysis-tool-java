package microinstr;

import program.Program;
import machine.Machine;
import machine.Register;
import c55x.C55xMachine;

public class C55xBitFieldOperation extends Microinstruction {
    MicroOperand source;
    MicroOperand destination;
    String type;
    int index = 0;

    public C55xBitFieldOperation(MicroOperand source, MicroOperand destination,
                                 String type, int index){
        super("C55xBitFieldOperation");
        this.source = source;
        this.destination = destination;
        this.type = type;
        this.index = index; // index to restore result
    }


    
    public void execute(Program program, Machine machine){
        long op1 = source.getValue();
        long op2 = destination.getValue();
      
        if (type.equals("XPA"))
            xpa(program, machine, op1, op2);
        else if (type.equals("XTR")){
            xtr(program, machine, op1, op2);
        }
        else throw new IllegalArgumentException("C55xBitFieldOperation: unknown argument:"+type);
    }
    
    // implement BFXPA here
    public void xpa(Program program, Machine machine, long op1, long op2) {
        throw new NullPointerException("not implemented: " + this);
    }

    public void xtr(Program program, Machine machine, long op1, long op2) {
        boolean debug = program.getOptions().getDebugMC();
        long result = 0;
        int bit = 0;

        for (int i = 0; i < 16; i++) {
            if (BitUtils.testBit(op1, i) == 1) { // bit is 1 in #k16
                // extract bit from ACx
                if (BitUtils.testBit(op2, i) == 1) // bit is 1 in ACx
                    result = BitUtils.setBit(result, bit);
                bit++;
            }
        }

        if (debug) System.out.println(" C55xBitFieldOperation: "
                                      + type + " "
                                      + "source ("+source.getName()+")"
                                      + " destination:0x"+ Long.toHexString(destination.getValue())
                                      + "("+destination.getValue()+")"
                                      + " result:"+Long.toHexString(result)
                                      + "("+result+")"
                                      + " index:"+index);

        machine.setDataResult(result, 64, index);

        //System.out.println("index:"+index+ " result:"+Long.toHexString(machine.getDataResult(0).getValue()));
    }
    

}
