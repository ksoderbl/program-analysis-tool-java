/**
 * C55xMMRegOperand.java
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
import c55x.instr.C55xRegisterOperand;

/**
 * This class represents a bit operand.
 */
public class C55xMMRegOperand extends Operand {

    private C55xRegisterOperand reg;

    public C55xMMRegOperand(C55xRegisterOperand reg) {
        this.reg = reg;
    }

    public String toString() {
        return "mmap(@" + reg.toString() + ")";
    }
}
