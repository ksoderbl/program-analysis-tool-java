package microinstr;

import program.Program;
import machine.Machine;
import machine.MemoryBank;
import machine.Register;
import c55x.C55xMachine;
import instr.Instruction;

public class C55xRet extends Microinstruction{


    public C55xRet(){
        super("C55xRet");
    }
    

    public void execute(Program program, Machine machine){
        boolean debug = program.getOptions().getDebugMC();
        Register sp = machine.getRegister("sp");
        long returnAddress = machine.readMem(sp.getValue(), machine.getDataBankNum());

        sp.setValue(sp.getValue() + 1);
        if (debug) System.out.println("Return address: "+Long.toHexString(returnAddress)+" sp:"+
                           Long.toHexString(sp.getValue()));

        if (returnAddress == C55xMachine.EXIT_ADDR){
            machine.setSimulationRunning(false);
            return;
        }

        //        MemoryBank mB = machine.getMemoryBank(machine.getInstructionBankNum());
        machine.setNextPCAddr(returnAddress);
        //      Instruction i = mB.readInstruction(machine.getPCAddr());
        //      machine.setPCAddr(machine.getPCAddr()+i.getSize());        
        
    }
    
}
