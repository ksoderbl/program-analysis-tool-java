/**
 * UnknownPseudoOp.java
 *
 * @author Kristian Söderblom
 */

package pseudoOp;

/**
 * unknown pseudo op
 */
public class UnknownPseudoOp extends PseudoOp {

    /** Attributes */
    private String name;

    public String getName() {
        return name;
    }

    /**
     * Constructs a new unknown pseudo op
     *
     * @param name the unknown pseudo op name
     * @return the new pseudo op
     */
    public UnknownPseudoOp(String name) {
        this.name = name;
    }

    public String toString() {
        return "unknown pseudo op: " + name;
    }

}
