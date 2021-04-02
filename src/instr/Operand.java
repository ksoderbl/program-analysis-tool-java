/**
 * Operand.java
 *
 * @author Kristian Söderblom
 */

package instr;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.PrintStream;
import input.Input;
import main.*;
import machine.Register;
import machine.Machine;

/**
 * This class represents an instruction operand.
 */
public abstract class Operand {
    public String toString() {
        return "Operand.toString: override in subclass";
    }

    public List getRegisters() {
        return new ArrayList();
    }

    public long getValue(){
        return 0L;
    }

    public boolean isMemoryAccess() {
        return false;
    }


    public Register getFirstReg(Machine machine){
        throw new NullPointerException("Operand superclass doesn't have any registers");
    }
    
     public Register getSecondReg(Machine machine){
         throw new NullPointerException("Operand superclass doesn't have any registers");
    }


}
