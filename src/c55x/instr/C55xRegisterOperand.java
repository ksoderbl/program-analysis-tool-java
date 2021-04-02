/**
 * C55xRegisterOperand.java
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
import machine.Machine;
import machine.Register;

/**
 * This class represents an instruction operand.
 */
public class C55xRegisterOperand extends Operand {

    private String name;

    private boolean dblMod;      // operand modifier dbl()
    private boolean loMod;       // operand modifier LO()
    private boolean pairMod;     // operand modifier pair()
    private boolean blockMod;    // operand modifier block(), used for swap4 instr

    private boolean unsMod;      // operand modifier uns()
    private boolean rndMod;      // operand modifier rnd()
    private boolean hiMod;       // operand modifier HI()
    private boolean saturateMod; // operand modifier saturate()


    public C55xRegisterOperand(String name) {
        this.name = name;

        this.dblMod = false;
        this.loMod = false;
        this.pairMod = false;
        this.blockMod = false;

        this.unsMod = false;
        this.rndMod = false;
        this.hiMod = false;
        this.saturateMod = false;
    }

    public void setDblMod() {
        this.dblMod = true;
    }
    public void setHiMod() {
        this.hiMod = true;
    }
    public boolean getHiMod() {
        return hiMod;
    }
    public void setLoMod() {
        this.loMod = true;
    }
    public boolean getLoMod() {
        return loMod;
    }

    public void setPairMod() {
        this.pairMod = true;
    }
    public void setBlockMod() {
        this.blockMod = true;
    }
    public void setUnsMod() {
        this.unsMod = true;
   } 
    public void setRndMod() {
        this.rndMod = true;
    }
    public void setSaturateMod() {
        this.saturateMod = true;
    }


    public String toString() {
        String s = name;
        if (this.saturateMod) {
            s = "saturate(" + s + ")";
        }
        if (this.hiMod) {
            s = "HI(" + s + ")";
        }
        if (this.loMod) {
            s = "LO(" + s + ")";
        }
        if (this.dblMod) {
            s = "dbl(" + s + ")";
        }
        if (this.pairMod) {
            s = "pair(" + s + ")";
        }
        if (this.blockMod) {
            s = "block(" + s + ")";
        }
        if (this.rndMod) {
            s = "rnd(" + s + ")";
        }
        if (this.unsMod) {
            s = "uns(" + s + ")";
        }
        return s;
    }

    public List getRegisters() {
        List list = new ArrayList();
        list.add(name);
        return list;
    }
    
    public Register getFirstReg(Machine machine){
        return machine.getRegister(name);
    }
    

}
