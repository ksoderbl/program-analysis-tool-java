/**
 * SizePseudoOp.java
 *
 * @author Kristian Söderblom
 */

package pseudoOp;

import misc.Expression;

/**
 * size pseudo op
 */
public class SizePseudoOp extends PseudoOp {

    /** Attributes */
    private String id;
    private Expression size;

    public String getId() {
        return id;
    }
    public Expression getSize() {
        return size;
    }

    /**
     * Constructs a size pseudo op
     *
     * @param id name of the symbol
     * @param size size
     * @return the new size pseudo op
     */
    public SizePseudoOp(String id, Expression size) {
        this.id = id;
        this.size = size;
    }

    public String toString() {
        return ".size\t" + id + ", " + size;
    }

}
