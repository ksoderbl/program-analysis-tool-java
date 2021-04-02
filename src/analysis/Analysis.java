/**
 * Analysis.java
 */

package analysis;

import program.Program;
import machine.Machine;

/**
 * Description of a static program analysis.
 *
 * @author Mikko Reinikainen
 */

public interface Analysis {

    /**
     * Performs an analysis on the program.
     *
     * @param program the program
     * @param machine machine on which the program runs
     */
    public abstract void analyze(Program program, Machine machine);
}
