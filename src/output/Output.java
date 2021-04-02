/**
 * Output.java
 */

package output;

import program.Program;
import machine.Machine;

/**
 * Output of analysis.
 *
 * @author Mikko Reinikainen
 */

public abstract class Output {
    /** name of output file */
    private String fileName;

    /**
     * Constructs a new output.
     * 
     * @param fileName name of output file
     * @return the new output
     */
    public Output(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return name of output file
     */
    public String getFileName() {
        return fileName;
    }
  
    /**
     * Writes output to the file.
     *
     * @param program the program
     * @param machine the machine
     * @param size Size of the output. Format of this string depends on
     * the output. Some outputs may ignore this parameter. 
     */
    public abstract void writeOutput(Program program,
                                     Machine machine,
                                     String size);

}
