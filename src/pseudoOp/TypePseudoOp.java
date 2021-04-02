/**
 * TypePseudoOp.java
 *
 * @author Kristian Söderblom
 */

package pseudoOp;

/**
 * type pseudo op
 */
public class TypePseudoOp extends PseudoOp {

    /** Attributes */
    private String id;
    private String type;

    public String getId() {
        return id;
    }
    public String getType() {
        return type;
    }

    /**
     * Constructs a new type pseudo op
     *
     * @param id the relevant id
     * @param type the type of this id
     * @return the new type pseudo op
     */
    public TypePseudoOp(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String toString() {
        return ".type\t" + id + ", %" + type;
    }

}
