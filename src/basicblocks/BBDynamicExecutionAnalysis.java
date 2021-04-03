
/**
 * edge+node ExecutionBasicBlockAnalysis.java
 */

package basicblocks;

import java.util.Iterator;
import java.util.List;

import analysis.Analysis;
import graph.Graph;
import graph.Edge;
import graph.Node;

import machine.Machine;
import program.Program;
import cfg.CFG;
import cfg.CFGNode;
import cfg.CFGEdge;
import instr.Instruction;

import main.*;

/**
 * Dynamic execution of basic block analysis.
 *
 * @author Peter Majorin
 * @see Analysis
 */

public class BBDynamicExecutionAnalysis implements Analysis {

    /**
     * Performs a post-processing of basic blocks
     * by resolving node and edge execution frequencies from instruction
     * executions from cfg
     *
     * @param program the program
     * @param machine machine on which the program runs
     */

    public void analyze(Program program, Machine machine) {
        //System.out.println("*** Basic Blocks ***");

        CFG cfg = program.getCFG();
        Graph basicBlocks = program.getBasicBlocks();
        Iterator<Node> iter = basicBlocks.getNodes().iterator();
        while (iter.hasNext()){
            
            BasicBlock bb = (BasicBlock)iter.next();
            List<CFGNode> instrList = bb.getNodes();
            CFGNode lastInstrNode = instrList.get(instrList.size()-1);
            Instruction i1 = lastInstrNode.getInstruction();
            //            System.out.println("bb:"+bb.getName()+" "+i1.getExecutions());
            bb.setExecutions(i1.getExecutions());
            
            List<Edge> outBranches = bb.getOutgoingEdges();
            Iterator<Edge> edgeIter = outBranches.iterator();
            
            while (edgeIter.hasNext()){
                BasicBlockEdge bbEdge = (BasicBlockEdge)edgeIter.next();
                BasicBlock bb2 = (BasicBlock)bbEdge.getEnd();
                List<CFGNode> instrList2 = bb2.getNodes();
                CFGNode firstInstrNode = instrList2.get(0);
                Instruction i2 = firstInstrNode.getInstruction();
                CFGEdge cfgEdge = (CFGEdge)cfg.findEdge(lastInstrNode,firstInstrNode);
                bbEdge.setExecutions(cfgEdge.getExecutions());
            }
            
            
        }
        
    }
}
