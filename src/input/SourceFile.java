/**
 * SourceFile.java
 */

package input;

/**
 * Reference to a source file.
 * 
 * @author Mikko Reinikainen
 * @see Input
 */

public class SourceFile {
    /** name of the source file */
    private String fileName;

    /**
     * Constructs a new reference to a source file
     * 
     * @param _fileName name of the source file
     * @return the new reference to a source file
     */
    public SourceFile(String _fileName) {
        fileName = _fileName;
    }

    /**
     * @return name of the source file
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return contents of this instance as a String
     */
    public String toString() {
        return fileName;
    }

}
