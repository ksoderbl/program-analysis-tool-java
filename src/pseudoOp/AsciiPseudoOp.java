/**
 * AsciiPseudoOp.java
 *
 * @author Kristian Söderblom
 */

package pseudoOp;

/**
 * ascii pseudo op
 */
public class AsciiPseudoOp extends PseudoOp {

    /** Attributes */
    private String string;

    public String getString() {
        return string;
    }

    /**
     * Constructs a new ascii pseudo op
     *
     * @param string the ascii string
     * @return the new pseudo op
     */
    public AsciiPseudoOp(String string) {
        this.string = string;
    }

    public String toString() {
        return ".ascii\t\"" + string + "\"";
    }
}
