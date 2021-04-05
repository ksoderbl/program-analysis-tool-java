package loop;

import java.util.*;
import program.Program;
import cfg.CFG;
import cfg.CFGNode;
import cfg.CFGEdge;
import basicblocks.*;
import graph.*;


public class LoopAnalysis{

    private static int loopNum = 0;

    public static void analyzeNaturalLoops(Program program, Graph bb, Tree dominators){
        Bucket naturalLoops = new Bucket();
        List<Edge> edges = bb.getEdges();
        Iterator<Edge> edgeIter = edges.iterator();
        while (edgeIter.hasNext()){
            Edge edge = edgeIter.next();
            if (dominators.dominates(edge.getStart(), edge.getEnd())){
                //System.out.println("loops:"+ edge.getStart().getName() +" "+ edge.getEnd().getName());
                assembleLoop(edge.getStart(), edge.getEnd());
                naturalLoops.addItemList("l"+loopNum, assembleLoop(edge.getStart(), edge.getEnd()));
                loopNum++;
            }
            
            
        }
        program.setLoops(naturalLoops);
        printLoops(program);
        mergeNaturalLoops(program);
        printLoops(program);
        markInnerloops(program);
        printLoops(program);
    }
    
    public static LinkedList<Object> assembleLoop(Node N, Node H){
        LinkedList<Object> body = new LinkedList<Object>();
        Stack<Node> stack = new Stack<Node>();
        Node D;
        body.add(H);
        stack.push(N);
        while (!stack.isEmpty()){
            D = stack.pop();
            if (!body.contains(D)){
                body.add(D);
                /* each predecessor of d */
                //System.out.print("Node: "+D.getName()+ " Edges: ");
                Iterator<Edge> edgeIter = D.getIncomingEdges().iterator();
                while (edgeIter.hasNext()){
                    Edge edge = edgeIter.next();
                    //    System.out.print(edge.getStart().getName()+" ");
                    stack.push(edge.getStart());
                }
            }
            
        }
        //printLoop(body);
        return body;
    }

    public static void mergeNaturalLoops(Program program){
        System.out.println("MergeNaturalLoops");
        Bucket loops = program.getLoops();
        System.out.println("Loops Size:"+loops.size());
        Iterator<LinkedList<Object>> loopIter = loops.listIterator();

        while (loopIter.hasNext()){
          LinkedList<Object> loop = loopIter.next();
          ListIterator<Object> bbIter = loop.listIterator();
          BasicBlock bbLoopHeader = (BasicBlock)bbIter.next();
          checkLoopHeader(loop, loops);
        }
        loops.deleteEmptyEntries(); // remove also empty linkedlists
        System.out.println("loops size: "+loops.size());
    }

    public static void checkLoopHeader(LinkedList<Object> loop1, Bucket loops){
        ListIterator<Object> bbIter = loop1.listIterator();
        BasicBlock bbLoopHeader = (BasicBlock)bbIter.next();
        Iterator<LinkedList<Object>> loopIter = loops.listIterator();
        while (loopIter.hasNext()){
          LinkedList<Object> loop2 = loopIter.next();
          bbIter = loop2.listIterator();
          if (!bbIter.hasNext()) continue;
          BasicBlock bbLoopHeader2 = (BasicBlock)bbIter.next();
          //System.out.println(bbLoopHeader2.getName()+" "+bbLoopHeader.getName());
          if (bbLoopHeader2.getName().equals(bbLoopHeader.getName())){
             System.out.println("mergeing:"+bbLoopHeader2.getName()+" "+bbLoopHeader.getName());
             mergeOneLoop(loop1, loop2);
             //loops.deleteEmptyEntries(); // remove also empty linkedlists
          }                
        }
    }   


    /** 
      * merges one loop with another if both have same headers, *
      * the other loop that is merged is removed from the list  */
    public static void mergeOneLoop(LinkedList<Object> loop1, LinkedList<Object> loop2){
        ListIterator<Object> bbIter1 = loop1.listIterator();
        boolean added = false;
        while (bbIter1.hasNext()){
          BasicBlock bb1 = (BasicBlock)bbIter1.next();
          if (!(objectExists(bb1, loop2))){ 
             loop2.add(bb1); 
             added = true;
             bbIter1.remove();
          }
        }
        if (added) {
         System.out.println("deleted a loop");
         loop1.clear(); //delete always the loop which was merged into loop2
        }
    }

    public static void printLoops(Program program){
        Bucket loops = program.getLoops();
        Iterator<LinkedList<Object>> iter = loops.listIterator();
        while (iter.hasNext()){
            LinkedList<Object> loop = iter.next();
            System.out.println("Loop");
            printLoop(loop);
        }
    }

    public static void printLoop(LinkedList<Object> body){
        int size = 0;
        ListIterator<Object> li = body.listIterator();
        while (li.hasNext()){
            BasicBlock bb = (BasicBlock)li.next();
            size +=bb.getByteSize();
            System.out.println(bb.getName()+" "+bb.getLoopDepth()+" "+bb.getExecutions());
        }
        System.out.println("Size:"+size);
    }

     /** does a given node exist in a linked list? */
    public static boolean objectExists(Object o1, LinkedList<Object> list){
        ListIterator<Object> iter = list.listIterator();
        while (iter.hasNext()){
          Object o2 = iter.next();
          if (o1.equals(o2)) return true;
        }
        return false;
    }

    public static Object getObject(Object o1, LinkedList<Object> list){
        ListIterator<Object> iter = list.listIterator();
        while (iter.hasNext()){
          Object o2 = iter.next();
          if (o1.equals(o2)) return o2;
        }
        return null;
    }

    /** adds loop depth for each basicblock in a loop */
    public static void addLoopDepth(LinkedList<Object> list){
        ListIterator<Object> iter = list.listIterator();
        while (iter.hasNext()){
           BasicBlock bb = (BasicBlock)iter.next();
           bb.addLoopDepth();
        }
    }

    public static void setExecutions(LinkedList<Object> list){
        ListIterator<Object> iter = list.listIterator();
        while (iter.hasNext()){
           BasicBlock bb = (BasicBlock)iter.next();
           int executions = (int)Math.pow(10,bb.getLoopDepth()*2);
           bb.setExecutions(executions);
        }
    }


/*
   public static void isSubSet(LinkedList l1, LinkedList l2){
     ListIterator iter1 = l1.listIterator();
     ListIterator iter2 = l2.listIterator();
        while (iter1.hasNext()){
          Object o2 = (Object)iter1.next();
          if (o1.equals(o2)) return o2;
        }
   }
*/

   public static void markInnerloops(Program program){
     Bucket loops = program.getLoops();        
     Graph basicBlocks = program.getBasicBlocks();
     Iterator<Node> basicBlockiter = basicBlocks.getNodes().iterator();


     Iterator<LinkedList<Object>> loopIter = loops.listIterator();
        while (loopIter.hasNext()){
          LinkedList<Object> loop = loopIter.next();
          addLoopDepth(loop);
        }


     loopIter = loops.listIterator();
        while (loopIter.hasNext()){
          LinkedList<Object> loop = loopIter.next();
          setExecutions(loop);
        }

     while (basicBlockiter.hasNext()){
          BasicBlock bb = (BasicBlock)basicBlockiter.next();
          if (bb.getLoopDepth() == 0) bb.setExecutions(1);
          if (bb.getName().equals("program_entry")) bb.setExecutions(0);
          if (bb.getName().equals("program_exit")) bb.setExecutions(0);
     }
   }
}
