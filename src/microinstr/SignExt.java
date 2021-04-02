package microinstr;

import program.Program;
import machine.Machine;
import machine.Register;

public class SignExt extends Microinstruction{

    MicroOperand target;
    MicroOperand condition = null;
    int bit = 0;

    public SignExt(MicroOperand target, int bits, MicroOperand condition, int bit){
        super("SignExt");
        this.target = target;
        this.condition = condition;
        this.bit = bit;
    }
    
    public SignExt(MicroOperand target, int bits){
        super("SignExt");
        this.target = target;
    }


    public void execute(Program program, Machine machine){

        if (condition != null){
            if (BitUtils.testBit(condition.getValue(), this.bit)==0L)
                return;
        }
        
        long value = target.getValue();
        long maskbits;
        

        //        return new Constant(result);
    }

}

