/**
 * BasicBlockOutput.java
 */

package basicblocks;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import graph.Graph;
import graph.Node;
import graph.Edge;

import machine.Machine;
import output.Output;
import program.Program;
import program.Procedure;

import main.*;

/**
 * An Output that prints the basic blocks of the program as an
 * input file for the graphviz graph drawing program.
 *
 * @author Mikko Reinikainen
 * @see Output
 */

public class BasicBlockOutput extends Output {
    /**
     * Constructs a new basic block output.
     *
     * @param fileName output file name
     * @return the new basic block output
     */
    public BasicBlockOutput(String fileName) {
        super(fileName);
    }

    /**
     * Writes contents of the control flow graph grouped as basic blocks
     * to a file that can be read by the graphviz graph drawing program.
     *
     * @param program the program
     * @param machine the machine
     */
    public void writeOutput(Program program,
                            Machine machine,
                            String size) {
        try {
            FileWriter fw = new FileWriter(getFileName());

            String graphSize, margin;
            boolean simAnalysis =  program.getOptions().getAnalyzeSimulation();

            if (size.equals("a3")) {
                graphSize = "10.87,14.88";
                margin = "0.21";
            } else {
                graphSize = "7.44,10.87";
                margin = "0.41";
            }
            // write file header
            fw.write("digraph \"" + program.getCFG().getName() + "\" {\n");
            fw.write("  size=\"" + graphSize + "\";\n" + "  margin=" + margin + ";\n" + "  node [shape=record];\n" +        // default node shape
                     "  center=1;\n");        // center the image

            Graph basicBlocks = program.getBasicBlocks();

            if ((basicBlocks == null)
                || (basicBlocks.getNodes() == null)) {
                Main.warn("Program has no basic blocks");
                return;
            }
            // output basic blocks of the program

            Iterator iter = basicBlocks.getNodes().iterator();
            while (iter.hasNext()) {
                BasicBlock node = (BasicBlock) iter.next();

                String type;
                switch (node.getType()) {
                case BasicBlock.PROCEDURE_ENTRY:
                    type = ",color=\"#000080\",style=bold";
                    break;
                case BasicBlock.PROCEDURE_EXIT:
                    type = ",color=\"#008000\",style=bold";
                    break;
                case BasicBlock.PROCEDURE_ENTRY | BasicBlock.PROCEDURE_EXIT:
                    type = ",color=\"#008080\",style=bold";
                    break;
                default:
                    type = "";
                }

                // dot User's Manual, January 26, 2006
                // Literal braces, vertical bars and angle brackets
                // must be escaped
                String t = node.toString();
                String s = "";

                for (int i = 0; i < t.length(); i++) {
                    char c = t.charAt(i);
                    if (c == '<')
                        s += "\\<";
                    else if (c == '>')
                        s += "\\>";
                    else
                        s += c;
                }

                fw.write("  " + node.getName()
                         + "[label=\"" + s + "\"" + type +
                         "];\n");
            }

            // force layout of procedure entries on the same row
        /*
            fw.write("  { rank = same; ");
            iter = basicBlocks.getNodes().iterator();
            while (iter.hasNext()) {
                BasicBlock block = (BasicBlock) iter.next();
                if ((block.getType() & BasicBlock.PROCEDURE_ENTRY) != 0) {
                    fw.write("\"" + block.getName() + "\"; ");
                }
            }
            fw.write("}\n");
        */
            // output edges between basic blocks
            iter = program.getBasicBlocks().getEdges().iterator();
            int m = 0;
            while (iter.hasNext()) {
                BasicBlockEdge edge = (BasicBlockEdge) iter.next();
                if (edge == null) {
                    Main.warn("null edge between basic blocks");
                } else {
                    if (edge.getStart() == null) {
                        Main.warn("edge from null basic block");
                    } else if (edge.getEnd() == null) {
                        Main.warn("edge to null basic block");
                    } else {
                        String type;
                        switch (edge.getType()) {
                        case Edge.EdgeFallTrough:
                            type = ",color=\"#000000\"";
                            break;
                        case Edge.EdgeBranchTaken:
                            type = ",color=\"#800000\"";
                            break;
                        case Edge.EdgeCall:
                            type = ",color=\"#000080\"";
                            break;
                        case Edge.EdgeReturn:
                            type = ",color=\"#008000\"";
                            break;
                        default:
                            type = "";
                        }

                        String edgeString = "  " + edge.getStart().getName() +
                            " -> " + edge.getEnd().getName();
                        if (simAnalysis) edgeString = edgeString + " [label="+"\" "
                                             +((BasicBlockEdge)edge).getExecutions() 
                                             +"\""+ type + "]";
                        edgeString = edgeString + ";\n";
                        
                        fw.write(edgeString);
                    }
                }
            }
            
            // end of file
            fw.write("}\n");
            fw.close();
            
    }
        catch(IOException e) {
            Main.warn("error writing output to file " +
                               getFileName()
                               + "\n" + e);
        }
    }
}


// fw.write("  " + edge.getStart().getName() + " -> " +
//                     edge.getEnd().getName() + ";\n");
