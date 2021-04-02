package microinstr;

import program.Program;
import machine.Machine;

public class Neg extends Microinstruction{

    MicroOperand dest;
    int index = 0;

    public Neg(MicroOperand dest, int index){
        super("Neg");
        this.dest = dest;
        this.index = index;
    }


    public void execute(Program program, Machine machine){

        machine.setDataResult(-dest.getValue(),64,index);
    }
}
