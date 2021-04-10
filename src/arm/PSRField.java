/**
 * PSRField.java
 */

package arm;

import instr.Operand;

/**
 * A PSR field of ARM.
 *
 * @author Juha Tukkinen
 */

public class PSRField extends Operand {

    private String psr;

    public String getPSR() {
        return psr;
    }

    /**
     * Constructs a new PSR field
     *
     * @return the new PSR field
     */
    public PSRField(String psr) {
        this.psr = psr;
    }

    public String toString() {
        return psr;
    }
}
