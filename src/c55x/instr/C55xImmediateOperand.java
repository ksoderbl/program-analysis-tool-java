/**
 * C55xImmediateOperand.java
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
import instr.Operand;

/**
 * This class represents an immediate operand (a constant).
 */
public class C55xImmediateOperand extends Operand {

    private long value;
    private boolean emithash;

    public C55xImmediateOperand(long value) {
        this.value = value;
        this.emithash = true;
    }

    public C55xImmediateOperand(long value, boolean emithash) {
        this.value = value;
        this.emithash = emithash;
    }

    public long getValue() {
        return value;
    }

    public String toString() {
        String s = "" + value;
        if (emithash)
            s = "#" + s;
        return s;
    }
}
