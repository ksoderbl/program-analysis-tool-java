/**
 * FilePseudoOp.java
 *
 * @author Kristian Söderblom
 */

package pseudoOp;

/**
 * file pseudo op
 */
public class FilePseudoOp extends PseudoOp {

    /** Attributes */
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    /**
     * Constructs a new file pseudo op
     *
     * @param fileName the file name
     * @return the new file pseudo op
     */
    public FilePseudoOp(String fileName) {
        this.fileName = fileName;
    }

    public String toString() {
        return ".file\t\"" + fileName + "\"";
    }

}
