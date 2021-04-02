
package c55x.instr;

import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.Iterator;
import instr.Instruction;
import instr.Operation;
import instr.Operand;
import main.*;
import microinstr.*;
import machine.Machine;

public class C55xInstruction extends Instruction {

    /** the microcode for a parallel execution of an instruction */
    private List parallelMicroinstrs;



    /**
     * Function for emitting the instruction.
     *
     * @return Instruction as formatted string to emit
     */
    public String emit() {
        C55xOperation[] oper = new C55xOperation[4];

        for (int i = 0; i < 4; i++)
            oper[i] = (C55xOperation)this.getOperation(i);

        String[] emitstring = new String[4];
        String result = "";

        for (int i = 0; i < 4; i++) {
            if (oper[i] != null) {
                emitstring[i] = oper[i].emit();
            }
        }

        result += emitstring[0];

        if (emitstring[1] != null)
            result += " :: " + emitstring[1];
        if (emitstring[2] != null)
            result += " || " + emitstring[2];
        if (emitstring[3] != null)
            result += " :: " + emitstring[3];

        //System.out.println("returning emitstring " + result);
        return result;
    }

    ////////////////////////////////////////////////////////////
    
    public void addParallelMicroinstr(Microinstruction mi) {
        parallelMicroinstrs.add(mi);
    }
    
    public List getParallelMicroinstrs(Machine machine) {
        //System.out.println("getting parallel microinstructions for " + this);
        if (parallelMicroinstrs == null) {
            parallelMicroinstrs = new ArrayList();
            //System.out.println("  need reschedule for " + this);
            this.reschedule(machine);
        }
        return parallelMicroinstrs;
    }
    
    
    private void reschedule(Machine machine){
        ArrayList operations;
        
        Iterator iter;
        operations = this.getOperations();
        //System.out.println("  operations = " + operations);

        if ((operations.get(0) != null) && (operations.get(2) != null)){
            Operation op1 = (Operation)operations.get(0);
            Operation op2 = (Operation)operations.get(2);
            //System.out.println("  op1 = " + op1);
            //System.out.println("  op2 = " + op2);
            machine.setMachineResultOffset(0);
            ArrayList microinstrs1 = (ArrayList)op1.getMicroinstrs(machine);
            machine.setMachineResultOffset(10);
            ArrayList microinstrs2 = (ArrayList)op2.getMicroinstrs(machine);
            ArrayList rescheduledInstrs = new ArrayList();


            // microinstructions from instruction 1
            iter = microinstrs1.iterator();
            while (iter.hasNext()){
                Microinstruction mi = (Microinstruction)iter.next();
                mi.setResultOffset(0); // kps hack, assumes <= 10 temporaries
                if (mi.getName().equals("WriteReg")){
                   // mi.setMicroMemNum(2);
                    rescheduledInstrs.add(mi);
                    System.out.println("changed one!");
                }
                else this.addParallelMicroinstr(mi);
            }
            
            // microinstructions from instruction 2
            iter = microinstrs2.iterator();
            while (iter.hasNext()){
                Microinstruction mi = (Microinstruction)iter.next();
                mi.setResultOffset(10); // kps hack, assumes <= 10 temporaries
                //mi.setMicroMemNum(2);
                       this.addParallelMicroinstr(mi);
            }
            
            
            iter = rescheduledInstrs.iterator();
            while (iter.hasNext()){
                Microinstruction mi = (Microinstruction)iter.next();
                mi.setResultOffset(0); // kps hack, assumes <= 10 temporaries
                //mi.setResultOffset(20);
                this.addParallelMicroinstr(mi);
            }
            
        }
     
           
        // single instruction, just copy to new array
        else if (true){
            Operation op1 = (Operation)operations.get(0);
            machine.setMachineResultOffset(0);
            ArrayList microinstrs = (ArrayList)op1.getMicroinstrs(machine);
            iter = microinstrs.iterator();
            while (iter.hasNext()){
                this.addParallelMicroinstr((Microinstruction)iter.next());
            }
            
            
        }
        
        else {
            System.out.println("No parallel version available");
        }
        
        
    }
    
}
