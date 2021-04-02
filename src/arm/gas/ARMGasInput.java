/**
 * ARMGasInput.java
 */

package arm.gas;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import input.Input;
import machine.Machine;
import program.Program;

import main.*;

/**
 * A front-end for GCC ARM machine assembly language.
 *
 * @author Juha Tukkinen
 * @author Mikko Reinikainen
 */

public class ARMGasInput extends Input {

    /**
     * Constructs a new GCC ARM machine input
     *
     * @param fileName input file name
     * @return the new input
     */
    public ARMGasInput(String fileName) {
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
        program.addInput(this);

        // open the input file
        FileInputStream in;
        try {
            in = new FileInputStream(getFileName());
        } catch (FileNotFoundException e) {
            Main.warn("Input file '" + getFileName() + "' not found.");
            return;
        }

        // create the parser
        ARMGasParser parser = new ARMGasParser(in, this,
                                               program.getOptions(),
                                               machine);

        // parse the input file
        try {
            parser.ParseInput();
            Main.info("Parsed file '" + getFileName() + "' successfully.");
        }
        catch (ParseException pe) {
            Main.warn("Parsing of file '" + getFileName() + "' failed: " + pe);
        }

        createPartialCFG(program);
    }
}
