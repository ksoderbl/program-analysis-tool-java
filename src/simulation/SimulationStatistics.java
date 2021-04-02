/* a facade object for maintainance of the simulation statistics */

package simulation;

import machine.MemoryBank;
import machine.Machine;
import program.Program;

public class SimulationStatistics{
    long instrs = 0;
    long operations = 0;
    Program program;
    Machine machine;
    public SimulationStatistics(Program program, Machine machine){
        this.program = program; this.machine = machine;
    }

    public void addExecutedInstrs(){
        instrs++;
    }

    public long getExecutedInstrs(){
        return instrs;
    }
    
    public void addExecutedOperations(){
        operations++;
    }        

    public long getExecutedOperations(){
          return operations;
    }          
    public long getMemReads(){
        MemoryBank mB = machine.getMemoryBank(machine.getDataBankNum());
        return mB.getNumReads();
    }
    
    public long getMemWrites(){
        MemoryBank mB = machine.getMemoryBank(machine.getDataBankNum());
        return mB.getNumWrites();
    }

    public long getInstrFetches(){
        MemoryBank mB = machine.getMemoryBank(machine.getInstructionBankNum());
        return mB.getInstrFetches(); 
    }
    
    public void print(){
        System.out.println("Executed instructions:"+instrs);
        System.out.println("Executed operations:"+operations);
        System.out.println("Instruction Memory reads:"+getMemReads());
        System.out.println("Instruction Memory writes:"+getMemWrites());
    }
}
