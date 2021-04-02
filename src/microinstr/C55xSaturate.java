package microinstr;

import program.Program;
import machine.Machine;
import c55x.*;
import machine.Register;
import machine.Registers;


public class C55xSaturate extends Microinstruction{

    MicroOperand value;
    

    public C55xSaturate(MicroOperand value){
        super("c55xSaturate");
        this.value = value;
    }



    public void execute(Program program, Machine c55xMachine){

        Registers regs  = c55xMachine.getRegisters();
        Register  st1  = regs.getRegister("st1");
       
        
        long c54CM = BitUtils.testBit(st1.getValue(), C55xMachine.C54CM);
        


        // machine.setDataResult(result);
        // return new Constant(result);
    }

}

