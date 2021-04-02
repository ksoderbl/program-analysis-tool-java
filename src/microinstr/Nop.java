package microinstr;

import program.Program;
import machine.Machine;

public class Nop extends Microinstruction{


    public Nop(){
        super("nop");
    }

    public void execute(Program program, Machine machine){
        return;
    }
}

