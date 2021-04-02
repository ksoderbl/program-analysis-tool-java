
package microinstr;

import program.Program;
import machine.Machine;
import machine.Register;


public class SwapRegs extends Microinstruction{

    MicroRegisterOperand reg1;
    MicroRegisterOperand reg2;


    public SwapRegs(MicroRegisterOperand reg1, MicroRegisterOperand reg2){
        super ("SwapRegs");
        this.reg1 = reg1;
        this.reg2 = reg2;
    }

    public void execute(Program program, Machine machine){
        long v1 = reg1.getValue();
        long v2 = reg2.getValue();
        reg1.setValue(v2);
        reg2.setValue(v1);
        /*
          System.out.println("SwapRegs:"+
          Long.toHexString(v1)+ " "+
          Long.toHexString(v2));
        */

    }
}
