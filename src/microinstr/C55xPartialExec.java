package microinstr;

import program.Program;
import machine.Machine;


public class C55xPartialExec extends Microinstruction{
    
    public C55xPartialExec(){
        super("C55xPartialExec");
    }



    public void execute(Program program, Machine machine){
        boolean debug = program.getOptions().getDebugMC();        
        if (!machine.getCondition()){
        machine.setPartialExecutionMode(true);        
        if (debug) System.out.println("Skipping next parallel instruction: Partial execution mode activated");
    }
    }
    
}
