/**
 * IdentPseudoOp.java
 *
 * @author Kristian Söderblom
 */

package pseudoOp;

/**
 * ident pseudo op
 */
public class IdentPseudoOp extends PseudoOp {

    /** Attributes */
    private String string;

    public String getString() {
        return string;
    }

    /**
     * Constructs a new ident pseudo op
     *
     * @param string the ident string
     * @return the new pseudo op
     */
    public IdentPseudoOp(String string) {
        this.string = string;
    }

    public String toString() {
        return ".ident\t\"" + string + "\"";
    }
}
