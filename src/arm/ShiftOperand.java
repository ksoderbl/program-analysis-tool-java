/**
 * ShiftOperand.java
 */

package arm;

import instr.Operand;

/**
 * A shift operand of ARM.
 *
 * @author Juha Tukkinen
 */

public class ShiftOperand extends Operand {

    public static final int LSL = 0;
    public static final int LSR = 1;
    public static final int ASL = 2;
    public static final int ASR = 3;
    public static final int ROR = 4;
    public static final int RRX = 5;

    private int shift;

    public int getShift() {
        return shift;
    }

    /**
     * Constructs a new shift operand
     *
     * @return the new shift operand
     */
    public ShiftOperand(int shift) {
        this.shift = shift;
    }

    public String toString() {
        switch (shift) {
        case LSL:
            return "lsl";
        case LSR:
            return "lsr";
        case ASL:
            return "asl";
        case ASR:
            return "asr";
        case ROR:
            return "ror";
        case RRX:
            return "rrx";
        default:
            return "unknown shift:" + shift;
        }
    }
}
