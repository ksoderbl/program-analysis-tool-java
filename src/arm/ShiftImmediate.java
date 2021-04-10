/**
 * ShiftImmediate.java
 */

package arm;

import instr.Operand;

/**
 * An immediate shift of ARM.
 *
 * @author Kristian Söderblom
 */

public class ShiftImmediate extends Operand {

    private ShiftOperand operand;
    private int numbits;

    /**
     * Constructs a new immediate shift
     *
     * @return the new immediate shift
     */
    public ShiftImmediate(ShiftOperand operand, int numbits) {
        this.operand = operand;
        this.numbits = numbits;
    }

    public String toString() {
        String result = operand.toString() + " #" + numbits;
        return result;
    }
}
