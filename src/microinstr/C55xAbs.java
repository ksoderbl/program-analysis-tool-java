package microinstr;

import program.Program;
import machine.Machine;
import machine.Register;

public class C55xAbs extends Microinstruction{

    MicroOperand dest;
    int index = 0;

    public C55xAbs(MicroOperand dest, int index){
        super("C55xAbs");
        this.dest = dest;
        this.index = index;
    }
    
        public void execute(Program program, Machine machine){
        boolean debug = program.getOptions().getDebugMC();        
        long result;
        Register reg = dest.getRegister();
        result = BitUtils.extendSigned(dest.getValue(), reg.getBitSize(), 64);       
        if (result < 0) result = - result;
        if (debug) System.out.println("Abs: "+"0x"+Long.toHexString(result)+ " opsize:"+reg.getBitSize());
        machine.setDataResult(result, 64, index);
    }
}
