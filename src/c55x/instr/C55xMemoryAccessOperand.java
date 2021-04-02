/**
 * C55xMemoryAccessOperand.java
 *
 * @author Kristian Söderblom
 */

package c55x.instr;
/*
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.PrintStream;
import input.Input;
*/
import java.util.List;
import java.util.ArrayList;
import instr.Operand;
import main.*;
import microinstr.*;
import machine.Register;
import machine.Machine;

/**
 * This class represents a memory access operand.
 */
public class C55xMemoryAccessOperand extends Operand
{
    public static final int Cmem = 0;
    public static final int Lmem = 1;
    public static final int Smem = 2;
    public static final int Xmem = 3;
    public static final int Ymem = 4;


    // MISRG p.48
    public static final int NOTMOD            = 0;  // e.g. *cdp
    public static final int POSTINC           = 1;  // e.g. *cdp+
    public static final int POSTDEC           = 2;  // e.g. *cdp-
    public static final int PREINC            = 3;  // e.g. *+cdp
    public static final int PREDEC            = 4;  // e.g. *-cdp

    public static final int PLUSREGOFFSET     = 5; // e.g. *(cdp+t0)
    public static final int MINUSREGOFFSET    = 6; // e.g. *(ar0-t0)
    public static final int REGOFFSET         = 7; // e.g. *ar0(t0)
    public static final int IMMOFFSET         = 8; // e.g. *ar0(#0010h)
    public static final int PREINC_IMMOFFSET  = 9; // e.g. *+ar2(#0013h)

    public static final int MMREG             = 30; // e.g. mmap(@ST3_55)

    public static final int DMA               = 40;  // e.g. @#01h
    public static final int INDIRECT          = 41;  // e.g. *(#00000h)
    public static final int ABS16             = 42;  // e.g. *abs(#00000h)
    public static final int PORT16            = 44;  // e.g. port(#00010h)

    private int mode; // Smem, etc.
    private int pointerModification;
    private String modeName;

    private boolean portMod;     // operand modifier port()
    private boolean dblMod;      // operand modifier dbl()
    private boolean dualMod;     // operand modifier dual()
    private boolean highByteMod; // operand modifier high_byte()
    private boolean lowByteMod;  // operand modifier low_byte()
    private boolean unsMod;      // operand modifier uns()
    private boolean T3EQ;        // assign register T3

    private C55xRegisterOperand reg1, reg2;
    private C55xImmediateOperand imm;

    private void init(String modeName) {
        this.setMode(modeName);
        this.portMod = false;
        this.dblMod = false;
        this.dualMod = false;
        this.highByteMod = false;
        this.lowByteMod = false;
        this.unsMod = false;
        this.T3EQ = false;

        this.pointerModification = -1;
        this.reg1 = null;
        this.reg2 = null;
        this.imm  = null;
    }

    public C55xMemoryAccessOperand(String modeName)
    {
        init(modeName);
    }

    public C55xMemoryAccessOperand(String modeName,
                                   int pointerModification,
                                   C55xRegisterOperand reg1,
                                   C55xRegisterOperand reg2)
    {
        init(modeName);
        this.pointerModification = pointerModification;
        this.reg1 = reg1;
        this.reg2 = reg2;
    }

    public C55xMemoryAccessOperand(String modeName,
                                   int pointerModification,
                                   C55xRegisterOperand reg)
    {
        init(modeName);
        this.pointerModification = pointerModification;
        this.reg1 = reg;
    }

    public C55xMemoryAccessOperand(String modeName,
                                   int pointerModification,
                                   C55xRegisterOperand reg,
                                   C55xImmediateOperand imm)
    {
        init(modeName);
        this.pointerModification = pointerModification;
        this.reg1 = reg;
        this.imm = imm;
    }

    public C55xMemoryAccessOperand(String modeName,
                                   int pointerModification,
                                   C55xImmediateOperand imm)
    {
        init(modeName);
        this.pointerModification = pointerModification;
        this.imm = imm;
    }

    public void setMode(String modeName) {
        if (modeName.equalsIgnoreCase("Cmem"))
            this.mode = Cmem;
        else if (modeName.equalsIgnoreCase("Lmem"))
            this.mode = Lmem;
        else if (modeName.equalsIgnoreCase("Smem"))
            this.mode = Smem;
        else if (modeName.equalsIgnoreCase("Xmem"))
            this.mode = Xmem;
        else if (modeName.equalsIgnoreCase("Ymem"))
            this.mode = Ymem;
        else {
            Main.warn("C55xMemoryAccessOperand: unknown modeName for mode, set with setMode(): " + modeName);
            this.mode = -1; // has to be set using setMode
        }
        this.modeName = modeName;
    }

    public void setPortMod() {
        this.portMod = true;
    }
    public void setDblMod() {
        this.dblMod = true;
    }
    public void setDualMod() {
        this.dualMod = true;
    }
    public void setHighByteMod() {
        this.highByteMod = true;
    }
    public void setLowByteMod() {
        this.lowByteMod = true;
    }
    public void setUnsMod() {
        this.unsMod = true;
    }
    public boolean hasUnsMod() {
        return unsMod;
    }
    public void setT3EQ() {
        this.T3EQ = true;
    }
    public boolean hasT3EQ() {
        return T3EQ;
    }

    public String toString() {
        String s = "";

        switch (pointerModification) {
        case DMA:
            {
                s += "@#";
                String h = Long.toHexString(imm.getValue());
                while (h.length() < 2)
                    h = "0" + h;
                s += h;
                s += "h"; 
            }
            break;
        case INDIRECT:
            {
                s += "*(#";
                String h = Long.toHexString(imm.getValue());
                while (h.length() < 5)
                    h = "0" + h;
                if (!h.startsWith("0"))
                    h = "0" + h;
                s += h;
                s += "h)"; 
            }
            break;
        case ABS16:
            {
                s += "*abs16(#";
                String h = Long.toHexString(imm.getValue());
                while (h.length() < 5)
                    h = "0" + h;
                if (!h.startsWith("0"))
                    h = "0" + h;
                s += h;
                s += "h)"; 
            }
            break;
        case PORT16:
            {
                s += "port(#";
                String h = Long.toHexString(imm.getValue());
                while (h.length() < 5)
                    h = "0" + h;
                if (!h.startsWith("0"))
                    h = "0" + h;
                s += h;
                s += "h)"; 
            }
            break;
        case PREINC:
            s += "*+" + reg1;
            break;
        case PREDEC:
            s += "*-" + reg1;
            break;
        case POSTINC:
            s += "*" + reg1 + "+";
            break;
        case POSTDEC:
            s += "*" + reg1 + "-";
            break;
        case REGOFFSET:
            s += "*" + reg1 + "(" + reg2 + ")";
            break;
        case PLUSREGOFFSET:
            s += "*(" + reg1 + "+" + reg2 + ")";
            break;
        case MINUSREGOFFSET:
            s += "*(" + reg1 + "-" + reg2 + ")";
            break;
        case IMMOFFSET:
            {
                s += "*" + reg1 + "(#";
                long v = imm.getValue();
                if (v < 0) {
                    s += "-";
                    v = -v;
                }
                String h = Long.toHexString(v);
                while (h.length() < 4)
                    h = "0" + h;
                if (!h.startsWith("0"))
                    h = "0" + h;
                s += h;
                s += "h)"; 
            }
            break;
        case PREINC_IMMOFFSET:
            {
                s += "*+" + reg1 + "(#";
                long v = imm.getValue();
                if (v < 0) {
                    s += "-";
                    v = -v;
                }
                String h = Long.toHexString(v);
                while (h.length() < 4)
                    h = "0" + h;
                if (!h.startsWith("0"))
                    h = "0" + h;
                s += h;
                s += "h)"; 
            }
            break;
        case MMREG:
            s += "mmap(@" + reg1 + ")";
            break;

        default: // DEFAULT ;)
            s += "*" + reg1;
            break;
        }

        if (this.highByteMod) {
            s = "high_byte(" + s + ")";
        }
        if (this.lowByteMod) {
            s = "low_byte(" + s + ")";
        }
        if (this.dblMod) {
            s = "dbl(" + s + ")";
        }
        if (this.dualMod) {
            s = "dual(" + s + ")";
        }
        if (this.unsMod) {
            s = "uns(" + s + ")";
        }
        if (this.portMod) {
            s = "port(" + s + ")";
        }
        if (this.T3EQ) {
            s = "T3 = " + s;
        }
        return s;
    }



    ///////////////////////////////////////////////////////////////////////////////

    public List getRegisters() {
        
        List list = new ArrayList();

        if (reg1 != null) {
            List l = reg1.getRegisters();
            list.addAll(l);
        }

        if (reg2 != null) {
            List l = reg2.getRegisters();
            list.addAll(l);
        }
        return list;
    }

    public boolean isMemoryAccess() {
        return true;
    }

    public Register getFirstReg(Machine machine){
        if (reg1 != null)
            return machine.getRegister(reg1.toString());
        return null;
    }
    
    public Register getSecondReg(Machine machine){
        if (reg2 != null)
            return machine.getRegister(reg2.toString());
        return null;
    }
    
    public boolean getDblMod(){ 
        return dblMod;
    }

    public boolean getDualMod() {
        return dualMod;
    }

    public int getAddressingMode(){
        return pointerModification;

    }

    public int getMemoryAccessMode(){
        return mode;
    }

    public C55xRegisterOperand getRegOp1(){
        return reg1;
    }

    public C55xRegisterOperand getRegOp2(){
        return reg2;
    }

    public C55xImmediateOperand getImmediateOp(){
        return imm;
    }
    
}
