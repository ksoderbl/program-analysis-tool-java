/**
 * AnnotationInput.java
 */

package input;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import input.Input;
import machine.Machine;
import program.Program;
import cfg.CFG;

import main.*;

/**
 * An annotation reader for annofiles.
 *
 * @author Peter Majorin
 */

public class AnnotationInput extends Input {

    /**
     * Constructs a new GCC ARM machine input
     *
     * @param fileName input file name
     * @return the new input
     */
    public AnnotationInput(String fileName) {
        super(fileName);
    }

    /**
     * Creates a control flow graph based on the GCC ARM machine input file.
     *
     * @param program the program into which the input should be loaded
     * @param machine executing architecture for the program
     */
    public void readInput(Program program, Machine machine) {

        // add this input to the list of inputs that were used to
        // read the program

        //program.addInput(this);

        // open the input file
        FileInputStream in;
        try {
            in = new FileInputStream(getFileName());
        } catch (FileNotFoundException e) {
            Main.warn("Input file '" + getFileName() + "' not found.");
            return;
        }
    }
}
