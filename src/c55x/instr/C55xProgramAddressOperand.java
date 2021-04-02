/**
 * C55xProgramAddressOperand.java
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
 * This class represents a c55x 24-bit program address.
 */
public class C55xProgramAddressOperand extends Operand
{
    private String label = null;
    private long offset = 0;
    private boolean hash;

    // no label => offset is the addr
    public C55xProgramAddressOperand(long addr, boolean hash) {
        this.offset = addr;
        this.hash = hash;
    }

    // label, offset 0
    public C55xProgramAddressOperand(String label) {
        this.label = label;
    }

    public C55xProgramAddressOperand(String label, long offset) {
        this.label = label;
        this.offset = offset;
    }

    public String getLabel() {
        return this.label;
    }
    public Long getOffset() {
        return this.offset;
    }
    

    public String toString() {
        if (label != null) {
            String s = label;
            long of = offset; // don't modify this.offset in toString()
            if (of == 0)
                return s;
            if (of < 0) {
                s += "-";
                of = -of;
            }
            else {
                s += "+";
            }
            s += of;
            return s;
        }
        else {
            String h = Long.toHexString(offset);

            if (hash) {
                while (h.length() < 6)
                    h = "0" + h;
                //if (!h.startsWith("0"))
                //        h = "0" + h;
                return "#0x" + h;
            }
            else {
                return "0x" + h;
            }
        }
    }
}
