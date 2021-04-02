/**
 * ShiftRegister.java
 */

package arm;

import machine.Register;

/**
 * A register shift of ARM.
 *
 * @author Kristian Söderblom
 */

public class ShiftRegister {

    private ShiftOperand operand;
    private Register register;

    /**
     * Constructs a new register shift
     *
     * @return the new register shift
     */
    public ShiftRegister(ShiftOperand operand, Register register) {
        this.operand = operand;
        this.register = register;
    }

    public String toString() {
        String result = operand.toString() + " " + register.toString();
        return result;
    }
}
