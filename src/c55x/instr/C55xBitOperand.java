/**
 * C55xBitOperand.java
 *
 * @author Kristian Söderblom
 */

package c55x.instr;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.PrintStream;
import input.Input;
import instr.Operand;
import c55x.C55xMachine;
import machine.Machine;
import machine.Register;

/**
 * This class represents a bit operand.
 */
public class C55xBitOperand extends Operand {

    private String name;
    private boolean notMod;

    public C55xBitOperand(String name) {
        this.name = name;
        this.notMod = false;
    }

    // emit ! before the bit name?
    public void setNotMod() {
        this.notMod = true;
    }

    public String toString() {
        String s = "";
        if (this.notMod) {
            s += "!";
        }
        s += name;
        return s;
    }

    ///////////////////////////////////
    public String getName(){
        return name;
    }
    
    public int getBit(Machine machine) {
        C55xMachine c55x = (C55xMachine)machine;
        return c55x.getBitByStatusBitName(name);
    }

    public Register getRegister(Machine machine) {
        C55xMachine c55x = (C55xMachine)machine;
        return c55x.getRegisterByStatusBitName(name);
    }
   
}
