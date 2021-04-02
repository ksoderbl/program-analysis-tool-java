/**
 * PSRField.java
 */

package arm;

/**
 * A PSR field of ARM.
 *
 * @author Juha Tukkinen
 */

public class PSRField {

    private String psr;

    public String getPSR() {
        return psr;
    }

    /**
     * Constructs a new PSR field
     *
     * @return the new PSR field
     */
    public PSRField(String _psr) {
        psr = _psr;
    }

    public String toString() {
        return psr;
    }
}
