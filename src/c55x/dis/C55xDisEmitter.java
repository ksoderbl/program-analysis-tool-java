/**
 * C55xDisEmitter.java
 * 
 * @author Kristian Söderblom
 */
package c55x.dis;

import java.io.PrintStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
import c55x.instr.*;

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

public class C55xDisEmitter extends Emitter {

    /** Program representation */
    private Program program;

    /** Contains output PrintStreams, which are accessed with their filename */
    private HashMap outputStreams = new HashMap();

    /** Contains output names, which are accessed with the original filename */
    private HashMap outputStreamNames = new HashMap();

    /** Emitter for single instruction */
    private EmitInstruction instrEmitter;

    /**
     * Constructor with program.
     * Constructs all the outputstreams for all input files
     */
    public C55xDisEmitter(Program program) {
        super(program); // superclass makes directory for emit
        this.program = program;
        instrEmitter = new EmitC55xInstruction();
        UserOptions options = program.getOptions();
        String directory = options.getDirName();
        Iterator iter = options.getInputFiles().iterator();
        String emitStreamName = "System.out";

        while (iter.hasNext()) {
            String fileName = (String)iter.next();
            PrintStream emitStream = null;
            FileOutputStream fileStream = null;
            if (fileName.length() > 0) {
                try {
                    File outFile = new File(fileName);
                    String tmpname = directory+File.separator+outFile.getName();
                    fileStream = new FileOutputStream(tmpname);
                    emitStream = new PrintStream(fileStream);
                    emitStreamName = tmpname;
                } catch (FileNotFoundException fnfe) {
                    // should not happen, we are opening new files
                    Main.warn("File " + fileName + " not found.");
                    Main.warn("\t Using System.out as output stream.");
                    emitStream = System.out;
                }
            }
            else {
                emitStream = System.out;
            }
            // System.out.println("insert:"+fileName);
            outputStreams.put(fileName, emitStream);
            outputStreamNames.put(fileName, emitStreamName);
        }
    }

    /** emit from inputlist */
    public void emit() {
        List inputs = program.getInputs();

        for (int i = 0; i < inputs.size(); i++) {
            Input input = (Input)inputs.get(i);
            String filename = input.getFileName();
            PrintStream outputStream
                = (PrintStream)outputStreams.get(filename);
            List lines = input.getInputLines();
            Long currentAddr = new Long(0);

            // kps - hacks to get the filename right on the
            // Disassembly line at start of file
            // assume UNIX
            int lastind = filename.lastIndexOf('/');
            // remove directory part of name, works also if lastind == -1
            String tmpname = filename.substring(lastind + 1);
            int firstind = tmpname.indexOf('.');
            if (firstind != -1) {
                tmpname = tmpname.substring(0, firstind);
            }
            //tmpname += ".obj";

            outputStream.println("");
            outputStream.println("Disassembly of " + tmpname + ":");

            for (int j = 0; j < lines.size(); j++) {
                InputLineObject io = (InputLineObject)lines.get(j);
                String es = ""; // string to emit on this line
                String opcode = "";
                int indent = 3;
                boolean useaddr = true;

                Instruction instr = io.getInstruction();
                Label label = io.getLabel();
                PseudoOp pop = io.getPseudoOp();

                if (instr != null) {
                    es = instrEmitter.emit(instr);
                    
                    currentAddr = instr.getAddr();
                    opcode = instr.getMachineCode();
                    //Instruction instr2 = instr.getNextParallelInstruction();
                    // kps - TODO: handle the parallel instruction
                    for (int k = 0; k < indent; k++)
                        es = " " + es;
                }
                if (label != null) {
                    // kps TODO - set currentAddr?
                    es = label.toString()+":";
                    currentAddr = label.getAddr();
                }
                if (pop != null) {
                    es = pop.toString();
                    if (pop instanceof C55xDisDataPseudoOp) {
                        C55xDisDataPseudoOp p = (C55xDisDataPseudoOp)pop;
                        currentAddr = p.getAddr();
                        opcode = p.getData();
                    }
                    if (pop instanceof C55xDisSectionPseudoOp) {
                        C55xDisSectionPseudoOp p = (C55xDisSectionPseudoOp)pop;
                        currentAddr = p.getAddr();
                        useaddr = false;
                        outputStream.println(""); // one empty line
                    }
                }

                if (useaddr) {
                    // construct string with address and opcode
                    String addrstr = Long.toHexString(currentAddr.longValue());
                    while (addrstr.length() < 6)
                        addrstr = "0" + addrstr;
                    addrstr += ": " + opcode;
                    while (addrstr.length() < 22)
                        addrstr += " ";
                    //for (int k = 0; k < indent; k++)
                    //    addrstr += " ";
                    es = addrstr + es;
                }

                // hack to print out isCall etc
                if (false && instr != null) {
                    while (es.length() < 69)
                        es += " ";
                    es += " ; ";
                    if (instr.isCondBranch())
                        es += "CB ";
                    else if (instr.isBranch())
                        es += "UB ";

                    if (instr.isCondCall())
                        es += "CC ";
                    else if (instr.isCall())
                        es += "UC ";

                    if (instr.isCondReturn())
                        es += "CR ";
                    else if (instr.isReturn())
                        es += "UR ";
                    //if (instr.isConditional())
                    //        es += "cond ";
                }

                outputStream.println(es);
            }
            outputStream.flush();
            outputStream.close();
            Main.info("C55xDisEmitter.emit: emitted file " + outputStreamNames.get(filename) + ".");
        }
    }
}
