/**
 * CFGOutput.java
 */

package cfg;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import graph.Graph;
import graph.Node;
import graph.Edge;

import machine.Machine;
import output.Output;
import program.*;
import instr.*;

import main.*;
import basicblocks.*;

/**
 * An Output that prints the basic blocks of the program as an
 * input file for the graphviz graph drawing program.
 *
 * @author Kristian Söderblom
 * @see Output
 */

public class CFGOutput extends Output {
    /**
     * Constructs a new basic block output.
     *
     * @param fileName output file name
     * @return the new basic block output
     */
    public CFGOutput(String fileName) {
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

            boolean simAnalysis =  program.getOptions().getAnalyzeSimulation();
            String graphSize, margin;
            if (size.equals("a3")) {
                graphSize = "10.87,14.88";
                margin = "0.21";
            } else {
                graphSize = "7.44,10.87";
                margin = "0.41";
            }
            // write file header
            fw.write("digraph \""
                     + " CFG Output of " + machine.getArch()
                     + " program " + program.getCFG().getName() + "\" {\n");
            fw.write("  size=\"" + graphSize + "\";\n"
                     + "  margin=" + margin + ";\n"
                     + "  node [shape=record];\n" +        // default node shape
                     "  center=1;\n");        // center the image

            CFG cfg = program.getCFG();

            if ((cfg == null)
                || (cfg.getNodes() == null)) {
                Main.warn("Program has no CFG");
                return;
            }
            // output cfg nodes (instructions) of the program

            Iterator<Node> nodeIter = cfg.getNodes().iterator();
            while (nodeIter.hasNext()) {
                CFGNode node = (CFGNode) nodeIter.next();
                Instruction instr = node.getInstruction();
                String type = ",color=\"#a0a0a0\"";

                if (instr.isProgramEntry())
                    type = ",color=\"#0040FF\",style=bold";
                if (instr.isProgramExit())
                    type = ",color=\"#FF4000\",style=bold";
                /*
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
                */
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
            iter = cfg.getNodes().iterator();
            while (iter.hasNext()) {
                CFGNode node = (CFGNode) iter.next();
                //if ((node.getType() & BasicBlock.PROCEDURE_ENTRY) != 0) {
                //    fw.write("\"" + node.getName() + "\"; ");
                //}
            }
            fw.write("}\n");
            */

            // output edges between cfg nodes
            Iterator<Edge> edgeIter = program.getCFG().getEdges().iterator();
            while (edgeIter.hasNext()) {
                Edge edge = edgeIter.next();
                if (edge == null) {
                    Main.warn("null edge between cfg nodes");
                } else {
                    if (edge.getStart() == null) {
                        Main.warn("edge from null cfg node");
                    } else if (edge.getEnd() == null) {
                        Main.warn("edge to null cfg node");
                    } else {
                        
                        if (simAnalysis){ fw.write("  " + edge.getStart().getName() +
                                                   " -> " + edge.getEnd().getName() + 
                                                   " [label="+"\" "+((CFGEdge)edge).getExecutions()+
                                                   "\""+"]"+
                                                   ";\n");
                        }
                        
                        else {fw.write("  " + edge.getStart().getName() +
                                       " -> " + edge.getEnd().getName() + 
                                       ";\n");
                        }
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

/* String edgeString = "  " + edge.getStart().getName() +
   " -> " + edge.getEnd().getName();
   if (simAnalysis) edgeString = edgeString + " [label="+"\" "
   +((BasicBlockEdge)edge).getExecutions() 
   +"\""+"]"+ ";\n";
   
*/
