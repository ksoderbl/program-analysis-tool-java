/**
 * C55xShiftOperand.java
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

/**
 * This class represents a shift operand. (op1 shift op2)
 */
public class C55xShiftOperand extends Operand {

    private Operand op1, op2;
    private boolean lshift;
    private boolean rndMod;     // is rnd() modifier applied to this shift?
    private boolean hiMod;      // is HI() modifier applied to this shift?
    private boolean unsMod;     // is uns() modifier applied to this shift?
    private boolean saturateMod;      // is saturate() modifier applied to this shift?

    public C55xShiftOperand(Operand op1, boolean lshift, Operand op2) {
        this.op1 = op1;
        this.lshift = lshift;
        this.op2 = op2;
        this.rndMod = false;
        this.hiMod = false;
        this.unsMod = false;
        this.saturateMod = false;
    }

    public boolean isMemoryAccess() {
        if (op1.isMemoryAccess())
            return true;
        if (op2.isMemoryAccess())
            return true;
        return false;
    }

    public void setRndMod() {
        this.rndMod = true;
    }
    public void setHiMod() {
        this.hiMod = true;
    }
    public boolean getHiMod() {
        return this.hiMod;
    }
    public void setUnsMod() {
        this.unsMod = true;
    }
    public void setSaturateMod() {
        this.saturateMod = true;
    }

    public String toString() {
        String s = op1.toString();
        if (lshift)
            s += " << ";
        else
            s += " >> ";
        s += op2.toString();
        if (saturateMod)
            s = "saturate(" + s + ")";
        if (hiMod)
            s = "HI(" + s + ")";
        if (rndMod)
            s = "rnd(" + s + ")";
        if (unsMod)
            s = "uns(" + s + ")";
        return s;
    }
/////////////////////////////////////////////////////////// 
    public Operand getOp1(){
        return op1;        
    }

    public Operand getOp2(){
        return op2;
    }
 
    public boolean getLShift(){
        return lshift;
    }

}
