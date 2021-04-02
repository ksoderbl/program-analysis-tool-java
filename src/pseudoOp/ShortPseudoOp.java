/**
 * ShortPseudoOp.java
 *
 * @author Kristian Söderblom
 */

package pseudoOp;

/**
 * short pseudo op
 */
public class ShortPseudoOp extends PseudoOp {

    /** Attributes */
    private long value;

    public long getValue() {
        return value;
    }

    /**
     * Constructs a short pseudo op
     *
     * @param value name of the symbol
     * @return the new short pseudo op
     */
    public ShortPseudoOp(long value) {
        this.value = value;
    }

    public String toString() {
        return ".short\t" + value;
    }

}
