/**
 * Emitter.java
 * 
 * Initial version
 *
 * @date 26.11.2004
 *
 * @author Ilari L.
 * @author Peter Majorin
 */
package emit;

import java.io.*;
import java.util.*;
import instr.*;
import program.*;
import cfg.*;
import input.*;
import misc.*;
import pseudoOp.*;
import main.*;
import basicblocks.BasicBlock;
import graph.Graph;

/**
 * This class contains the functions for emitting the code.
 * The class is used in data-oriented way, it's constructed
 * with the input, that is, the <code>Program</code> object
 * and the code emission is done with appropriate functions.
 * It must be possible to handle all output files corresponding
 * to input files at the same time, since when traversing the 
 * CFG, we cannot be sure  what output file we will be writing, 
 * so all output files must be available.
 */

public abstract class Emitter {

    /** Program representation */
    private Program program;

    private void makeNewDirectory(String dir){
        // Create a directory; all ancestor directories must exist
        boolean success = false;
        File file = new File(dir);
        if (!file.exists()){
            success = file.mkdir();
        }

        else success = true;
        
        if (!success) {
            Main.fatal("Directory creation failed!");
        }
    }


    /**
     * Constructor with program.
     * Constructs all the outputstreams for all input files
     */
    public Emitter(Program program) {
        UserOptions options = program.getOptions();
        String directory = options.getDirName();

        makeNewDirectory(directory);
    }

    public abstract void emit();
}
