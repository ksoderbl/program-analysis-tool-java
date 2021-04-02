
/**
 * Input.java
 */

package input;


import program.Program;
import machine.Machine;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import cfg.CFG;
import cfg.CFGNode;
import graph.Edge;
import instr.Instruction;
import program.Label;
//import pseudoOp.PseudoOp;

/**
 * A front end that reads a program into the analyzer.
 *
 * @author Mikko Reinikainen
 * @see InputPosition
 */

public abstract class Input {
    /** number of next cfg node */
    static int cfgNodeNum = 0;

    /** name of the input file */
    private String fileName;

    /** list of source files that were used to generate this input */
    private List sourceFiles;

    /** the type of file */
    private String type;

    /**
     * List of objects from parser, one object per input line.
     * The objects can represent e.g. an instruction, a label or
     * a pseudo operation; these implement the InputLineObject interface.
     */
    private List inputLines;


    /**
     * Constructs a new input
     *
     * @param fileName name of the input file
     * @return the new input
     */
    public Input(String fileName) {
        this.fileName = fileName;
        sourceFiles = new ArrayList();
        inputLines = new ArrayList();
    }

    /**
     * @return name of the input file
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Adds a source file to this input
     *
     * @param sourceFile the source file
     */
    public void addSourceFile(SourceFile sourceFile) {
        sourceFiles.add(sourceFile);
    }

    /**
     * Adds a source file to this input
     *
     * @param sourceFile the source file
     */
    public void addSourceFile(String sourceFileName) {
        addSourceFile(new SourceFile(sourceFileName));
    }

    /**
     * @eturn list of source files that were used to generate this input
     */
    public List getSourceFiles() {
        return sourceFiles;
    }

    /**
     * @eturn list of input line objects
     */
    public List getInputLines() {
        return inputLines;
    }

    /**
     * @param program the program to which the input should be loaded
     * @param machine machine for which the program is written
     */
    
    public abstract void readInput(Program program, Machine machine);

    /**
     * Create partial CFG out of this Input.
     * This needs to be called from the readInput.
     */
    protected void createPartialCFG(Program program) {
        CFG cfg = program.getCFG();
        CFGNode prevnode = null, next = null, node = null;
        Iterator iterator = this.getInputLines().iterator();
        ArrayList tmpLabels = new ArrayList();
        Iterator li; // label iterator
        Long ilc = new Long(0); // instruction location counter

        while (iterator.hasNext()) {
            InputLineObject io = (InputLineObject)iterator.next();

            Instruction i = io.getInstruction();
            Label label = io.getLabel();
            //PseudoOp pop = io.getPseudoOp();

            if (i != null) {
                // If we get the addresses from the input, we use those.
                // Otherwise, we start from address 0.
                if (i.getAddr().equals(new Long(0)))
                    i.setAddr(ilc);
                else
                    ilc = i.getAddr();

                if (true) {
                    // kps - name CFG nodes as "n" + a number
                    node = cfg.createNode("n" + cfgNodeNum, i);
                    cfgNodeNum++;
                }
                else {
                    // kps - old code
                    node = cfg.createNode(ilc.toString(), i);
                }

                program.addAddress(ilc, node);

                /*
                 * Got the next instruction.
                 * Handle labels that were prior to this.
                 */
                li = tmpLabels.iterator();
                while (li.hasNext()) {
                    Label l = (Label)li.next();
                    program.addSymbol(l.getName(),
                                      node.getInstruction().getAddr(),
                                      node);
                }
                // ok we've handled these labels, forget about them
                tmpLabels.clear();

                ilc = new Long(ilc.longValue() + i.getSize());

                /*
                 * Possibly create a cfg edge.
                 */
                if (prevnode != null) {
                    // previous instruction
                    Instruction pi = (Instruction)prevnode.getInstruction();
                    
                    if (pi.isBranch()) {
                        Instruction b = pi;

                        b.setNext(node);
                        // for conditional braches, we sometime don't branch,
                        // in that case control may flow from prevnode to node.
                        if (b.isCondBranch())
                            cfg.createEdge(prevnode, node, Edge.EdgeFallTrough);
                    } else
                        cfg.createEdge(prevnode, node, Edge.EdgeFallTrough);
                }

                prevnode = node;
                //System.out.println("Input line: " + i);
            }

            // handle label
            if (label != null) {
                tmpLabels.add(0, label);
            }

            // just an example about how to find a certain PseudoOp.
            /*if (pop != null) {
                //System.out.println("PseudoOp: " + o);
                }*/

        }

        // ok iterated thru the whole list, handle remaining labels
        // which are after the last instruction
        li = tmpLabels.iterator();
        while (li.hasNext()) {
            Label l = (Label)li.next();
            program.addLabel(l.getName(), ilc);
        }
    }


    /**
     * @return input file as a string
     */
    public String toString() {
        return "input file " + getFileName() + ", contains source files "
            + sourceFiles;
    }

    
}
