/**
 * SpacePseudoOp.java
 *
 * @author Kristian Söderblom
 */

package pseudoOp;

/**
 * space pseudo op
 */
public class SpacePseudoOp extends PseudoOp {

    /** Attributes */
    private long size;

    public long getSize() {
        return size;
    }

    /**
     * Constructs a space pseudo op
     *
     * @param size size of space
     * @return the new space pseudo op
     */
    public SpacePseudoOp(long size) {
        this.size = size;
    }

    public String toString() {
        return ".space\t" + size;
    }

}
