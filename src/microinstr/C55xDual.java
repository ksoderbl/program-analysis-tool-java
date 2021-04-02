package microinstr;

import program.Program;
import machine.Machine;

public class C55xDual extends Microinstruction{
    
    MicroOperand address;
    MicroOperand reg1;
    MicroOperand reg2;
    int index = 0;
    
    public C55xDual(MicroOperand address, MicroOperand reg1, 
                    MicroOperand reg2){
        super("C55xDual");
        this.address = address;
        this.reg1 = reg1;
        this.reg2 = reg2;

        this.index = index;
    }
    
    public void execute(Program program, Machine machine){

        machine.setDataResult(address.getValue(),64,index);
        
    }
    
}
