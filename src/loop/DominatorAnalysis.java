package loop;

import java.util.Hashtable;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.ListIterator;
import java.util.Stack;

import program.Program;
import cfg.*;
import analysis.Analysis;
import machine.Machine;
import machine.Registers;
import basicblocks.*;
import graph.*;
import java.io.FileWriter;


/**
 * Dominator analysis
 *
 * @author Peter Majorin
 * @see Analysis
 */

public class DominatorAnalysis implements Analysis {

    /**
     * Performs dominator analysis of the program.
     *
     * @param program the program
     * @param machine machine on which the program runs
     */
    public void analyze(Program program, Machine machine) {
        int nodeNum;
        Node [] dfsNodes;
        Node parent;
        CFG cfg = program.getCFG();
        Graph bb = program.getBasicBlocks();
        //Graph bb = makeTestGraph();
        Tree dominators;
        dfsNodes = dfsTraverse(bb);
  
        //dfsNodes = makeDFSTree();
        /* print DFS tree */
        
        //System.out.println("tablesize "+dfsNodes.length);
        dfsNodes[0].getParent();

        for (int i = 0; i < dfsNodes.length; i++){
            parent = dfsNodes[i].getParent();
            //System.out.print(dfsNodes[i].getName()+" dfsNum: "+dfsNodes[i].getDFSNum()+ " "+dfsNodes[i].getDFSEndNum());
            //if (parent != null) System.out.println(" parent: "+parent.getName());
            //else System.out.println(" parent: null");
        }

        //printBB(bb);
               dominators = dominatorTree(dfsNodes);
        //printBB(bb);
      

        LoopAnalysis.analyzeNaturalLoops(program, bb, dominators);
        try {
            writeOutput(program, machine, "dominators", dominators);
        }

        catch (Exception e){
        }
        
    }


    public static void printBB(Graph bb){
        List nodes = bb.getNodes();
        Iterator nodeIter = nodes.iterator();
        //System.out.println("-------------BasicBlocks---Incoming edges------");
        while (nodeIter.hasNext()){
            Node node = (Node)nodeIter.next();
            List edges = node.getIncomingEdges();
            Iterator edgeIter = edges.iterator();
            //System.out.print(node.getName()+" ");
            while (edgeIter.hasNext()){
                Edge edge = (Edge)edgeIter.next();
                //System.out.print(" "+edge.getStart().getName());
            }
            //System.out.println();
        }

        nodes = bb.getNodes();
        nodeIter = nodes.iterator();
        //System.out.println("-------------BasicBlocks---Outgoing edges------");
        while (nodeIter.hasNext()){
            Node node = (Node)nodeIter.next();
            List edges = node.getOutgoingEdges();
            Iterator edgeIter = edges.iterator();
            //System.out.print(node.getName()+" ");
            while (edgeIter.hasNext()){
                Edge edge = (Edge)edgeIter.next();
                //System.out.print(" "+edge.getEnd().getName());
            }
            //System.out.println();
        }

        //System.out.println("---------------------------------------------");
    }
    




    public Node[] dfsTraverse(Graph g){
        Stack stack = new Stack();
        Hashtable visited = new Hashtable();
        List nodes = g.getNodes();
        Node from;
        //System.out.println("nodes size "+nodes.size());
        Node[] dfsNodes = new Node[nodes.size()];
        int i = 0;
        //int j = 0;

        from = (Node)nodes.get(0);
        //        from.setDFSNum(j++); 

        stack.push(from);
        while (!stack.isEmpty()){
            visited.put(from.getName(), new Boolean(true));
            from.setDFSNum(i);
            dfsNodes[i++] = from;
            Iterator edgeIter = from.getOutgoingEdges().iterator();
            //Enumeration adjEdges = g.getAdj(from);

              while (edgeIter.hasNext()) {
                //while (adjEdges.hasMoreElements()) {
                Edge tempEdge = (Edge)edgeIter.next();
                Node tempNode = tempEdge.getEnd();
                //Node tempNode = ((Edge)(adjEdges.nextElement())).getEndNode();
                if (! visited.containsKey(tempNode.getName())) {
                    tempNode.setParent(tempEdge.getStart());
                    stack.push(tempNode);
                    // start number of DFS
                    //tempNode.setDFSNum(j++); 
                }
            }
            
            // endnumber of DFS
            //from.setDFSEndNum(i);
            //dfsNodes[i++] = from;
            
            from = (Node)(stack.pop());
        }
        return dfsNodes;
    }


    
    public Tree dominatorTree(Node [] dfsNodes){
        /* depth-first search spanning tree */

        Node n, p = null, s, v = null, sprime, y, predn;
        Hashtable semi = new Hashtable();
        /* spanning forest */
        Hashtable ancestor = new Hashtable();
        Hashtable idom = new Hashtable();
        Hashtable sameDom = new Hashtable();
        Tree dominator = new Tree("dominator");
        /* bucket of all nodes that s semidominates */
        Bucket semiDoms = new Bucket();


        for (int i = dfsNodes.length - 1; i > 0; i--){ // Skip over root node
            //System.out.println("i:"+i);
            n = (Node)dfsNodes[i]; p = n.getParent(); s = p;
            //System.out.println("************ n node: "+n.getName() + " dfsNum "+n.getDFSNum());
       
            //    v = v.getParent();
            List predecessors = n.getIncomingEdges();
            Iterator edgeIter = predecessors.iterator();
    

            while (edgeIter.hasNext()){
                Edge edge = (Edge)edgeIter.next();
                v = (Node)edge.getStart();
                
                //System.out.println("v node: "+v.getName());
                if (v.getDFSNum() <= n.getDFSNum())
                    sprime = v;
                else {
                //    System.out.println("------------------------------------- special ----");
                    sprime = (Node)semi.get(ancestorWithLowestSemi(semi, ancestor, v).getName());
                }
                if (sprime.getDFSNum() < s.getDFSNum())
                    s = sprime;
                
            }
        //System.out.println("semiput: "+ n.getName()+" "+s.getName());
            semi.put(n.getName(), s);
        // System.out.println("bucket put (c semidom) "+s.getName()+" item added: "+n.getName());
            semiDoms.addItem(s.getName(), n);
        //  System.out.println("ancestor.put: "+ n.getName()+" "+p.getName());
            ancestor.put(n.getName(), p);
        //  semiDoms.printBucket();

         //   printHashtable(ancestor, "ancestor table");
         //   printHashtable(semi, "semi table");

        
        
            Node bucketNode = p;
            if (bucketNode != null){
                LinkedList lL = (LinkedList)semiDoms.getEntry(bucketNode.getName());
                //System.out.println("attempting to fetch from bucket:"+p.getName());
                if (lL != null) {
                  //  System.out.println("++++++++++++++");
                    ListIterator lI = lL.listIterator();
                    // for each v in bucket 
                    while (lI.hasNext()){
                        v = (Node)lI.next();
                        y = ancestorWithLowestSemi(semi, ancestor, v);
                        //System.out.println("y:"+y.getName()+ " v:"+v.getName());
                        Node n1 = (Node)semi.get(y.getName());
                        Node n2 = (Node)semi.get(v.getName());
                        if (n1.getName().equals(n2.getName())){
                            idom.put(v.getName(), p);
                            Dominator v1 = new Dominator(v.getName());
                            Dominator p1 = new Dominator(p.getName());
                            dominator.addEdge(new Edge(v1, p1));
                            dominator.addNode(p1);
                          //  System.out.println("idom.put: "+v.getName()+ " " + p.getName());
                        }
                        else{
                            sameDom.put(v.getName(), y);
                         //   System.out.println("samedom.put: "+v.getName()+ " " + y.getName());
                        }
                    }
                }
                
            }
            
            semiDoms.deleteEntry(p.getName());
        }
        
        for (int i = 0; i < dfsNodes.length -1 ; i++){
            n = (Node)dfsNodes[i];
            if (sameDom.get(n.getName()) != null){
                v = (Node)sameDom.get(n.getName());
                Node iNode = (Node)idom.get(v.getName());
                Dominator iNode2 = new Dominator(iNode.getName());
                Dominator n2 = new Dominator(n.getName());
                idom.put(n.getName(), iNode);
                dominator.addEdge(new Edge(n2, iNode2));
                dominator.addNode(iNode2);
            }
        }
        
        
        //printHashtable(idom, "idom table");
        
        return dominator;
    }
    
    
    
    public Node ancestorWithLowestSemi(Hashtable semi, Hashtable ancestor, Node v){
        Node u;
        u = v;
        while (ancestor.get(v.getName())!= null){
            //System.out.println("Ancestorwithlowest: "+v.getName());
            Node v1 = (Node)semi.get(v.getName());
            Node u1 = (Node)semi.get(u.getName());
            if (v1.getDFSNum() < u1.getDFSNum())
                u = v;
            //System.out.println("before"+v.getName());
            v = (Node)ancestor.get(v.getName());
          //  System.out.println("after"+v.getName());
        }
        //System.out.println("AncestorExit: lowest semi: "+u.getName());
        return u;
    }

    public void writeOutput(Program program,
                            Machine machine,
                            String size, Tree dominator) throws java.io.IOException{
                                      
        
        FileWriter fw = new FileWriter("dominator.dot");
        
        

        // Enumeration e = nodes.keys();
        
        // write file header
        fw.write("digraph \"" + "foobar" + "\" {\n");
        fw.write("size=\"7.44,10.87\";\n");
        fw.write("margin=0.41;\n");
          fw.write("node [shape=record];\n");
        fw.write("center=1;\n");
        fw.write("rankdir = \"BT\";\n");
        
        List edges = dominator.getEdges();
        Iterator edgeIter = edges.iterator();
        while (edgeIter.hasNext()){
            Edge edge = (Edge)edgeIter.next();
            fw.write(edge.getStart().getName()+ " -> "+ edge.getEnd().getName()+";\n");
        }
        
        
        //for (int i = 0; i < nodes.length; i++){
        //    fw.write(nodes[i].getName()+"\n");
        //}

               //while (e.hasMoreElements()){
        //   String key = (String)e.nextElement();
        //   BasicBlock node = (BasicBlock)nodes.get(key);
        //   fw.write(key+ "->"+ node.getName()+";\n");
        //}
        
        fw.write("}\n");
        fw.close();
    }




    public void printHashtable(Hashtable hT, String tableName){
        Enumeration e = hT.keys();
        System.out.println(tableName);
        while (e.hasMoreElements()){
            String key = (String)e.nextElement();
            Node node = (Node)hT.get(key);
        
            if (node.getParent() == null)
                System.out.println("key:"+key+" name: "+node.getName()+ " dfnum: "+ 
                                   node.getDFSNum()+ " dfEndnum: " +node.getDFSEndNum() + " parent: null");
            else 
                System.out.println("key:"+key+" name: "+node.getName()+ " dfnum: "+ 
                                   node.getDFSNum()+ " dfEndnum: " +node.getDFSEndNum() + " parent: "+node.getParent().getName());

        }
      
        System.out.println();
    }


    public Node[] makeDFSTree(){
        
        Node[] dfsNodes = new Node[13];
        
        Node a = new Node("A");
        Node b = new Node("B");
        Node c = new Node("C");
        Node d = new Node("D"); 
        Node e = new Node("E");
        Node f = new Node("F");
        Node g = new Node("G");
        Node h = new Node("H");
        Node i = new Node("I");
        Node j = new Node("J");
        Node k = new Node("K");
        Node l = new Node("L");
        Node m = new Node("M");
        
        a.setDFSNum(0);
        a.setParent(null);
        b.setDFSNum(1);
        b.setParent(a);
        c.setDFSNum(10);
        c.setParent(a);
        d.setDFSNum(2);
        d.setParent(b);
        e.setDFSNum(11);
        e.setParent(c);
        f.setDFSNum(3);
        f.setParent(d);
        g.setDFSNum(8);
        g.setParent(d);
        h.setDFSNum(12);
        h.setParent(e);
        i.setDFSNum(4);
        i.setParent(f);
        j.setDFSNum(9);
        j.setParent(g);
        k.setDFSNum(7);
        k.setParent(f);
        l.setDFSNum(5);
        l.setParent(i);
        m.setDFSNum(6);
        m.setParent(l);

        dfsNodes[0]=a;
        dfsNodes[1]=b;
        dfsNodes[2]=d;
        dfsNodes[3]=f;
        dfsNodes[4]=i;
        dfsNodes[5]=l;
        dfsNodes[6]=m;
        dfsNodes[7]=k;
        dfsNodes[8]=g;
        dfsNodes[9]=j;
        dfsNodes[10]=c;
        dfsNodes[11]=e;
        dfsNodes[12]=h;

        Edge ab = new Edge(a,b);
        Edge ac = new Edge(a,c);
        Edge bd = new Edge(b,d);
        Edge bg = new Edge(b,g);
        Edge df = new Edge(d,f);
        Edge dg = new Edge(d,g);
        Edge fi = new Edge(f,i);
        Edge fk = new Edge(f,k);
        Edge gj = new Edge(g,j);
        Edge il = new Edge(i,l);
        Edge ji = new Edge(j,i);
        Edge kl = new Edge(k,l);
        Edge lb = new Edge(l,b);
        Edge lm = new Edge(l,m);
        
        /* right branch */
        Edge ce = new Edge(c,e);
        Edge eh = new Edge(e,h);
        Edge hm = new Edge(h,m);
        Edge ec = new Edge(e,c);
        Edge ch = new Edge(c,h);

        

        a.addOutgoingEdge(ab);
        a.addOutgoingEdge(ac);
        b.addIncomingEdge(ab);
        b.addIncomingEdge(lb);
        b.addOutgoingEdge(bd);
        b.addOutgoingEdge(bg);
        
        d.addIncomingEdge(bd);
        d.addOutgoingEdge(df);
        d.addOutgoingEdge(dg);
        
        f.addIncomingEdge(df);
        f.addOutgoingEdge(fi);
        f.addOutgoingEdge(fk);
          
        g.addIncomingEdge(bg);
        g.addIncomingEdge(dg);
        g.addOutgoingEdge(gj);
        
        i.addIncomingEdge(fi);
        i.addOutgoingEdge(il);
          
        j.addOutgoingEdge(ji);
        k.addOutgoingEdge(kl);
        
        l.addOutgoingEdge(lb);
        l.addOutgoingEdge(lm);
        c.addOutgoingEdge(ce);
        e.addOutgoingEdge(eh);
        h.addOutgoingEdge(hm);

        return dfsNodes;
        
    }
    
    

    public Graph makeTestGraph(){

        Graph gr = new Graph("foo");
        Node a = new Node("A");
        Node b = new Node("B");
        Node c = new Node("C");
        Node d = new Node("D");
        Node e = new Node("E");
        Node f = new Node("F");
        Node g = new Node("G");
        Node h = new Node("H");
        Node i = new Node("I");
        Node j = new Node("J");
        Node k = new Node("K");
        Node l = new Node("L");
        Node m = new Node("M");
        

        Edge ab = new Edge(a,b);
        Edge ac = new Edge(a,c);
        Edge bd = new Edge(b,d);
        Edge bg = new Edge(b,g);
        Edge df = new Edge(d,f);
        Edge dg = new Edge(d,g);
        Edge fi = new Edge(f,i);
        Edge fk = new Edge(f,k);
        Edge gj = new Edge(g,j);
        Edge il = new Edge(i,l);
        Edge ji = new Edge(j,i);
        Edge kl = new Edge(k,l);
        Edge lb = new Edge(l,b);
        Edge lm = new Edge(l,m);
        
        /* right branch */
        Edge ce = new Edge(c,e);
        Edge eh = new Edge(e,h);
        Edge hm = new Edge(h,m);
        Edge ec = new Edge(e,c);
        Edge ch = new Edge(c,h);
        
        gr.addEdge(ab);
        gr.addEdge(ac);
        gr.addEdge(bd);
        gr.addEdge(bg);
        gr.addEdge(df);
        gr.addEdge(dg);
        gr.addEdge(fi);
        gr.addEdge(fk);
        gr.addEdge(gj);
        gr.addEdge(il);
        gr.addEdge(ji);
        gr.addEdge(kl);
        gr.addEdge(lb);
        gr.addEdge(lm);

        gr.addEdge(ce);
        gr.addEdge(eh);
        gr.addEdge(hm);
        gr.addEdge(ec);
        gr.addEdge(ch);
        



        //        a.addOutgoingEdge(ab);
        //        a.addOutgoingEdge(ac);


        /*
          a.addOutgoingEdge(ab);
          a.addOutgoingEdge(ac);
          b.addIncomingEdge(ab);
          b.addIncomingEdge(lb);
          b.addOutgoingEdge(bd);
          b.addOutgoingEdge(bg);
          
          d.addIncomingEdge(bd);
          d.addOutgoingEdge(df);
          d.addOutgoingEdge(dg);
          
          f.addIncomingEdge(df);
          f.addOutgoingEdge(fi);
          f.addOutgoingEdge(fk);
          
          g.addIncomingEdge(bg);
          g.addIncomingEdge(dg);
          g.addOutgoingEdge(gj);
          
          i.addIncomingEdge(fi);
          i.addOutgoingEdge(il);
          
          j.addOutgoingEdge(ji);
          k.addOutgoingEdge(kl);
          
          l.addOutgoingEdge(lb);
          l.addOutgoingEdge(lm);
          c.addOutgoingEdge(ce);
          e.addOutgoingEdge(eh);
          h.addOutgoingEdge(hm);
        */

        gr.addNode(a);
        gr.addNode(b);
        gr.addNode(c);
        gr.addNode(d);
        gr.addNode(e);
        gr.addNode(f);
        gr.addNode(g);
        gr.addNode(h);
        gr.addNode(i);
        gr.addNode(j);
        gr.addNode(k);
        gr.addNode(l);
        gr.addNode(m);

        return gr;
    }





 /*
   private static void visit(int numBlocks, Iterator iter){
        Edge e = (Edge)iter.next();
        BasicBlock bb = (BasicBlock)e.getStart();
        bb.setVisited(true);

        Iterator out = bb.getOutgoingEdges().iterator();
        while (out.hasNext()) {
            Edge e2 = (Edge)iter.next();
            BasicBlock node = (BasicBlock)e.getStart();
            Iterator out2 =  node.getOutgoingEdges().iterator();
            e2 = (Edge)iter.next();
            
            while (out2.hasNext()){
                BasicBlock node2 = null;
                if (!node2.getVisited()) visit(numBlocks, iter);
                bb.setDominatorNum(numBlocks);
                numBlocks--;
            }
        }
 */
        // reset states of edges of the cfg
        
        //        iter = cfg.getEdges().iterator();
        //while (iter.hasNext()) {
        //    CFGEdge edge = (CFGEdge) iter.next();
        //    edge.setState(null);
        //}
}
