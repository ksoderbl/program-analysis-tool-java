/**
 * SectionPseudoOp.java
 *
 * @author Kristian Söderblom
 */

package pseudoOp;

/**
 * section pseudo op
 */
public class SectionPseudoOp extends PseudoOp {

    /** Attributes */
    private String name;

    public String getNamel() {
        return name;
    }

    /**
     * Constructs a new section pseudo op
     *
     * @param name the name of the section
     * @return the new pseudo op
     */
    public SectionPseudoOp(String name) {
        this.name = name;
    }

    public String toString() {
        return ".section\t" + name;
    }

}
