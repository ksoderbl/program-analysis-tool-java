/**
 * C55xBaddrOperand.java
 *
 * @author Kristian Söderblom
 */

package c55x.instr;

/*
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.PrintStream;
import input.Input;*/
import instr.Operand;

/**
 * This class represents an Baddr operand of C55x.
 */
public class C55xBaddrOperand extends Operand
{
    C55xRegisterOperand reg;
    C55xImmediateOperand imm;
    C55xMemoryAccessOperand ma;

    public C55xBaddrOperand(C55xRegisterOperand reg) {
        this.reg = reg;
        this.imm = null;
        this.ma  = null;
    }
    public C55xBaddrOperand(C55xImmediateOperand imm) {
        this.reg = null;
        this.imm = imm;
        this.ma  = null;
    }
    public C55xBaddrOperand(C55xMemoryAccessOperand ma) {
        this.reg = null;
        this.imm = null;
        this.ma  = ma;
    }

    public C55xRegisterOperand getRegister() {
        return reg;
    }

    public C55xImmediateOperand getImmediate() {
        return imm;
    }

    public C55xMemoryAccessOperand getMemoryAccess() {
        return ma;
    }

    public String toString() {
        if (reg != null)
            return reg.toString();
        if (imm != null) {
            String h = Long.toHexString(imm.getValue());
            while (h.length() < 2)
                h = "0" + h;
            return "@#" + h + "h";
        }
        if (ma != null) {
            return ma.toString();
        }
        return "C55xBaddrOperand BUG";
    }
}
