/**
 * BasicBlockAnalysis.java
 */

package basicblocks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import analysis.Analysis;
import graph.Graph;
import graph.Edge;
import graph.Node;

import machine.Machine;
//import program.Label;
import program.Procedure;
import program.Program;
import cfg.CFG;
import cfg.CFGNode;
import cfg.CFGEdge;
import instr.Instruction;

import main.*;

/**
 * Basic block analysis.
 *
 * @author Mikko Reinikainen
 * @see Analysis
 */

public class BasicBlockAnalysis implements Analysis {

    /** basic blocks of the program */
    private Graph basicBlocks;

    /** a mapping from a control flow graph node to a basic block */
    private HashMap nodeToBasicBlock = new HashMap();

    private HashSet visited;

    /**
     * Performs basic block analysis of the program
     *
     * @param program the program
     * @param machine machine on which the program runs
     */
    public void analyze(Program program, Machine machine) {

        visited = new HashSet();
        CFG cfg = program.getCFG();
        basicBlocks = new Graph("Basic blocks of program " + cfg.getName());

        // find entry node of the main procedure
        String firstLabel = "program_entry";
        CFGNode firstNode = (CFGNode) program.getNode(firstLabel);

        if (firstNode == null) {
            Main.fatal("BasicBlockAnalysis: no node with name '"
                       + firstLabel + "' in the cfg");
        }
        // Analyze nodes and build basic blocks by starting from firstNode
        analyzeBasicBlock(basicBlocks, firstNode);

        // add edges between basic blocks
        Iterator iter = cfg.getEdges().iterator();

        while (iter.hasNext()) {
            CFGEdge edge = (CFGEdge) iter.next();
            BasicBlock start = getBasicBlock(edge.getStart());
            BasicBlock end = getBasicBlock(edge.getEnd());
            int type = edge.getType();

            if ((start != null) && (end != null)) {
                if (start == end) {
                    // edge to a node in the same basic block
                    CFGNode entry = (CFGNode) start.getNodes().get(0);

                    if (((CFGNode) edge.getEnd()).equals(entry)) {
                        // only add edges to the entry node of the basic block
                        basicBlocks.addEdge(new BasicBlockEdge(start, end, type));
                    }
                } else {
                    // add edge from basic block start to basic block end
                    basicBlocks.addEdge(new BasicBlockEdge(start, end, type));
                }
            }
        }

        // add labels to basic blocks
        iter = program.getLabels().keySet().iterator();
        while (iter.hasNext()) {
            String label = (String) iter.next();
            CFGNode node = program.getNode(label);
            BasicBlock block = getBasicBlock(node);

            if (block == null) {
                //      Main.warn("label '" + label + "' does not point to any basic block (dead code?)");
            } else if (node == (CFGNode) block.getNodes().get(0)) {
                // label points to entry of this basic block
                block.addLabel(label);
            } else {
                // label points inside this basic block (no jumps to the label)
                // XXX should these labels be stored somewhere?
            }
        }

        // mark procedure entries and exits
        iter = program.getProcedures().values().iterator();
        while (iter.hasNext()) {
            Procedure proc = (Procedure) iter.next();
            BasicBlock block = getBasicBlock(proc.getEntry());

            if (block == null) {
                Main.warn("entry node of procedure " + proc +
                                   " does not have a basic block");
            } else {
                block.setType(block.getType() | BasicBlock.
                              PROCEDURE_ENTRY);
            }

            Iterator exits = proc.getExits().iterator();
            while (exits.hasNext()) {
                CFGNode exitNode = (CFGNode) exits.next();

                block = getBasicBlock(exitNode);
                if (block == null) {
                    Main.warn("exit node " + exitNode +
                                       "\nof procedure " + proc +
                                       "\ndoes not have a basic block");
                } else {
                    block.setType(block.getType() | BasicBlock.
                                  PROCEDURE_EXIT);
                }
            }
        }

        // store basic blocks in the program
        program.setBasicBlocks(basicBlocks);
    }

    public Graph getBasicBlocks() {
        return basicBlocks;
    }

    private void analyzeBasicBlock(Graph basicBlocks,
                                   CFGNode node) {

        // do not analyze visited nodes
        if (visited.contains(node)) {
            // this node has already been visited
            return;
        }
        //    System.out.println("XXX visiting " + node.getName());

        // create a new basic block and add this node to it
        String name = node.getName();
        BasicBlock block = new BasicBlock(name);
        addNode(node, block);

        // list of edges from this node
        List outgoing = node.getOutgoingEdges();

        // calculate contents of the basic block
        while (outgoing.size() == 1) {

            // TODO:
            // 2021-04-02: This code causes regression.sh to fail.
            // However, this code might be correct, I'm not sure at the moment.

            // CFGNode current = (CFGNode) ((CFGEdge) outgoing.
            //                              get(0)).getStart();
            // if (current.getInstruction().isCall()
            //     || current.getInstruction().isReturn())
            //     break;

            // this node has only one successor 
            CFGNode next =
                (CFGNode) ((CFGEdge) outgoing.
                                        get(0)).getEnd();

            // add successor to the basic block if it has only one predecessor
            if (next.getIncomingEdges().size() == 1) {
                addNode(next, block);
                outgoing = next.getOutgoingEdges();
            } else {
                // otherwise it belongs to another basic block
                break;
            }
        }
        // add constructed basic block to the graph
        //    System.out.println("---\nNODE:\n" + name + "\n---\n"); // XXX 
        basicBlocks.addNode(block);

        // recursively analyze outgoing nodes
        Iterator iter = outgoing.iterator();
        while (iter.hasNext()) {
            CFGEdge edge = (CFGEdge) iter.next();
            //      System.out.println(edge.getStart().getName() + "->" + edge.getEnd().getName());
            analyzeBasicBlock(basicBlocks,
                              (CFGNode) (edge.getEnd()));
        }
    }

    private String nodeName(CFGNode node) {
        String procName;
        if (node.getProcedure() == null) {
            procName = "";
        } else {
            procName = node.getProcedure().getName();
        }
        return node.getInstruction().getCode() + " (" + procName + ")\\n";
    }

    private BasicBlock getBasicBlock(Node node) {
        return (BasicBlock) nodeToBasicBlock.get(node);
    }

    private void addNode(CFGNode node, BasicBlock block) {
        //    System.out.println("XXX adding node " + node.getName() + " to basic block "+ block.getName());
        //    System.out.println("(outgoing edges: " + node.getOutgoingEdges() +")");

        // mark this node as visited
        visited.add(node);

        // add node to the specified basic block 
        block.addNode(node);
        Instruction instr = (Instruction)node.getInstruction();
        block.addByteSize(instr.getSize());
        if (nodeToBasicBlock.containsKey(node)) {
            Main.warn("Node " + node +
                               " already belongs to basic block " + block);
        } else {
            nodeToBasicBlock.put(node, block);
        }
    }


}
