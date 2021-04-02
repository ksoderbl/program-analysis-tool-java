/**
 * AlignPseudoOp.java
 *
 * @author Kristian Söderblom
 */

package pseudoOp;

/**
 * align pseudo op
 */
public class AlignPseudoOp extends PseudoOp {

    /** Attributes */
    private long alignment;

    public long getAlignment() {
        return alignment;
    }

    /**
     * Constructs a align pseudo op
     *
     * @param alignment alignment
     * @return the new align pseudo op
     */
    public AlignPseudoOp(long alignment) {
        this.alignment = alignment;
    }

    public String toString() {
        return ".align\t" + alignment;
    }

}
