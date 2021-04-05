
package traces;


import program.Program;
import cfg.CFG;
import cfg.CFGNode;
import cfg.CFGEdge;
import analysis.Analysis;
import machine.Machine;

import basicblocks.*;
import graph.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import loop.*;



public class TraceAnalysis implements Analysis {



    public void analyze(Program program, Machine machine) {
        
        initTraces(program.getBasicBlocks());
       
        makeTraces(program);
        printTraces(program);

        //Iterator iter = loops.iterator();
        
        


        
    }
    
    
    public void makeTraces(Program program){
        HashMap<String, Trace> traces = new HashMap<String, Trace>();
        Bucket loops = program.getLoops();
        Iterator<LinkedList<Object>> iter = loops.listIterator();
        int traceNum = 0;
        // iterate over loop headers
        while (iter.hasNext()){
            LinkedList<Object> loop = iter.next();

            BasicBlock loopHeader = (BasicBlock)loop.getFirst();
            
            if (loopHeader.isPartOfTrace()) continue;
            Trace trace = new Trace(loopHeader, "t"+traceNum);
            traceNum++;

            BasicBlock nextBB = trace.evaluate(loopHeader);
            loopHeader.setPartOfTrace();
            // add new basicblocks into trace
            while (nextBB != null){ 
                nextBB.setPartOfTrace();
                trace.addBasicBlock(nextBB);
                nextBB = trace.evaluate(nextBB);
            }
        
            traces.put(trace.getName(), trace);
            
        }
        program.setTraces(traces);
    }
    
    
     public static void printTraces(Program program){
        HashMap<String, Trace> traces = program.getTraces();
        Iterator<Trace> iter = traces.values().iterator();
        while (iter.hasNext()){
            Trace trace = iter.next();
            System.out.println("trace:"+trace.getName());
            printTrace(trace);
        }
    }

    public static void printTrace(Trace trace){
        Iterator<BasicBlock> li = trace.getIterator();
        while (li.hasNext()){
            Node node = li.next();
            System.out.println(node.getName());
        }
    }



   
    /** for testing purposes */
    public void initTraces(Graph bb){
        List<Edge> edges;
        List<Node> nodes;
        edges = bb.getEdges();
        Iterator<Edge> edgeIter = edges.iterator(); 
        // sort edges according to their dynamic execution weight
        while (edgeIter.hasNext()){
            BasicBlockEdge bbEdge = (BasicBlockEdge)edgeIter.next();
            bbEdge.setExecutions((int) (Math.random() * 100));
        }

        nodes = bb.getNodes();
        Iterator<Node> nodeIter = nodes.iterator();
        while (nodeIter.hasNext()){
            BasicBlock bbNode = (BasicBlock)nodeIter.next();
            bbNode.setExecutions((int) (Math.random() * 100));
        }
        
        

    }
    
    /* some tests
     * Comparator bbComp = new BBEdgeComparator();
     * PriorityQueue sortedEdges = new PriorityQueue(100, bbComp);
     * sortedEdges.add(bbEdge)
     * while (sortedEdges.size() != 0){
     *            BasicBlockEdge a = (BasicBlockEdge)sortedEdges.poll();
     *            System.out.println(a.getDynamicExecutions());
     *        }
     */


}
