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
package arm.gas;

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
import emit.*;

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

public class ARMGasEmitter extends Emitter {

    /** Program representation */
    private Program program;

    /** Contains output PrintStreams, which are accessed with their filename */
    private HashMap<String, PrintStream> outputStreams = new HashMap<String, PrintStream>();

    /** Emitter for single instruction */
    private EmitInstruction instrEmitter;

    /**
     * Constructor with program.
     * Constructs all the outputstreams for all input files
     */
    public ARMGasEmitter(Program program) {
        super(program); // superclass makes directory for emit
        this.program = program;
        instrEmitter = new EmitARMInstruction();
        UserOptions options = program.getOptions();
        String directory = options.getDirName();
        Iterator<String> iter = options.getInputFiles().iterator();

        while (iter.hasNext()) {
            String fileName = iter.next();
            PrintStream emitStream = null;
            FileOutputStream fileStream = null;
            if (fileName.length() > 0) {
                try {
                    File outFile = new File(fileName);
                    fileStream = new FileOutputStream(directory+File.separator+outFile.getName());
                    emitStream = new PrintStream(fileStream);
                } catch (FileNotFoundException fnfe) {
                    // should not happen, we are opening new files
                    Main.warn("File " + fileName + " not found.");
                    Main.info("\t Using System.out as output stream.");
                    emitStream = System.out;
                }
            }
            else {
                emitStream = System.out;
            }
            // System.out.println("insert:"+fileName);
            outputStreams.put(fileName, emitStream);
        }
    }

    public void emit(){
        orderedEmit3();
    }

    /** emit from inputlist */
    public void orderedEmit3(){
        PrintStream outputStream = null;
        List<Input> inputs = program.getInputs();
        int n = inputs.size();

        for (int i = 0; i < n; i++){
            Input input = inputs.get(i);
            outputStream = outputStreams.get(input.getFileName());
            List<InputLineObject> lines = input.getInputLines();
            int k = lines.size();
            for (int j = 0; j < k; j++){
                InputLineObject o = lines.get(j);
                if (o instanceof Instruction){
                    Instruction instruction = (Instruction)lines.get(j);
                    outputStream.println("\t" +instrEmitter.emit(instruction));
                }
                else if (o instanceof Label) {
                    outputStream.println(o.toString()+":");
                }
                else if (o instanceof PseudoOp) {
                    outputStream.println("\t"+ o.toString());
                    o.toString();
                    
                }
            }
        }
    }
}
