
package edit;

import java.util.*;

import stabs.MemoryBlock;
import program.*;
import instr.*;
import cfg.*;
import graph.*;
import basicblocks.*;
import machine.Machine;
import analysis.Analysis;
import input.*;
import pseudoOp.*;
import misc.*;

public abstract class Edit{
    
    protected Program program;
    protected Machine machine;
    
    public Edit(Program program, Machine machine){
        this.program = program;
        this.machine = machine;
        //markBasicBlocks();
    }

    /* look for code to place on scratchpad, starter routine, which 
       is specific to the architecture editing the program, thus abstract */

    public abstract void editCode();


    /* look for memory blocks to place on scratchpad, starter routine */
    public abstract void editData();


    /* an all-out optimization */
    public void editCodeAndData(){
        editCode();
        editData();
    }
    


    /* delete a single instr */
    protected void deleteInstr(BasicBlock bb, int index){
        List cfgNodes = bb.getNodes();
        cfgNodes.remove(index);

    }
    
    /** 
     * adds a single instr to basicblocks at index.
     */
    /* broken
    protected void addInstruction(BasicBlock bb, Instruction instr, int index){
        List cfgNodes = bb.getNodes();
        List inputs = program.getInputs();
        CFGNode cfgNode = (CFGNode)cfgNodes.get(index);
        InputPosition inputPos = cfgNode.getInputPosition();
        int i = 0;
        while (inputPos == null){ 
            i++;
            if (cfgNodes.size()-1 < i) return;
            cfgNode =(CFGNode)cfgNodes.get(index+i);
            inputPos = cfgNode.getInputPosition();
            System.out.println(""+i);
        }
        Instruction instruction = cfgNode.getInstruction();
        System.out.println(instruction.getOper());
        Input input = instruction.getInput();
        List inputLines = input.getInputLines();
        int n = inputLines.size();
        for (int j = 0; j < n; j++){
            if (inputLines.get(j) instanceof Instruction){
                Instruction ins2 = (Instruction)(inputLines.get(j));
                if (instruction.equals(ins2)){
                    inputLines.add(j,instr);
                    break;
                }
            }
        }

        //        while (!instruction.equals(inputLines.get(i))) i++;
        //        System.out.println(i);


        System.out.println("file:" +input.getFileName());
        System.out.println("line:" +inputPos.getLine());
        
        //InstructionWrapper iw = new InstructionWrapper(index
        //        CFGNode cfgNode = new CFGNode("foo", null, instr);
        //cfgNodes.add(index, cfgNode);
    }
    */
    
    /**
     * Scan a basicblock bb for instruction of type instr and
     * return the index in a vector
     */
    
    protected int matchInstruction(Instruction cfgInstruction){

        List inputLines = cfgInstruction.getInput().getInputLines();
        int n = inputLines.size();
        
        for (int i = 0; i < n; i++){
            if (inputLines.get(i) instanceof Instruction){
                Instruction objInstruction = (Instruction)(inputLines.get(i));
                if (cfgInstruction.equals(objInstruction)){
                    //System.out.println("buu:"+objInstruction.toString());
                    return i;
                }
            }
        }
        return -1; // should not happen
    }
    

 
    

    /**
     * returns the index in the parseobject list for instruction
     * in cfgNode, cfgNode must be a valid instruction!
     */
    protected int getIndexObjList(CFGNode cfgNode){
        Instruction instruction = cfgNode.getInstruction();
        Input input = instruction.getInput();
        List inputLines = input.getInputLines();
        int n = inputLines.size();
        for (int i = 0; i < n; i++){
            if (inputLines.get(i) instanceof Instruction){
                Instruction ins2 = (Instruction)(inputLines.get(i));
                if (instruction.equals(ins2)) {
                    //  System.out.println(ins2.toString());
                    return i;
                }
            }
        }

        return -1;
    }
   
    
    protected List locateInstr(BasicBlock bb, Instruction instr){

        // kps  - uncommented this, don't know if this works very well
        // e.g. an instruction can have several opcodes
        //        List indices = new ArrayList();
        //        Input input = bb.getInput();
        //        if (input == null) return indices;
        //        List inputLines = input.getInputLines();
        //        int start = bb.getFirst();
        //        int end   = bb.getLast();
        //        for (int i = start; i <= end; i++){
        //            if (inputLines.get(i) instanceof Instruction){
        //                Instruction ins1 = (Instruction)inputLines.get(i);
        //                if (ins1.getOper().equals(instr.getOper()))
        //                    indices.add(new Integer(i));
        //                System.out.println(ins1.toString());
        //            }
        //            
        //        }
        //        
        //        return indices;

        return null;
    }


    /*

      while (iter.hasNext()) {
      BasicBlock node = (BasicBlock)iter.next();
      if (node.getName().equals(bbName)){
      List cfgNodes = node.getNodes();
      // CFGNode cfgNode = new CFGNodeNoInsert("foo", null, instr);
      // cfgNodes.addNode(cfgNode, index);
      return true;
      }
      }
      return false;
      }*/

     public void printProcedures(){
        Iterator iter = program.getProcedures().values().iterator();
        System.out.println("*** Functions ***");
        while (iter.hasNext()){
            Procedure proc = (Procedure)iter.next();
            System.out.println(proc.toString());
        }
        
     }
    
    public void printBasicBlocks(){
        Iterator iter = program.getBasicBlocks().getNodes().iterator();
        System.out.println("*** Basic Blocks ***");
        while (iter.hasNext()){
            BasicBlock bb = (BasicBlock)iter.next();
            System.out.println("Size:"+bb.getByteSize());
            System.out.println(bb.toString());
        }
    }
    
    
    
    /* correct jumps to a function */
    protected void changeIncomingJumps(Procedure procedure, String newTargetLabel){
        CFGNode firstNode = procedure.getEntry();
        List inJumps = firstNode.getIncomingEdges();
        Iterator edgeIter = inJumps.iterator();
        while (edgeIter.hasNext()) {
                //while (adjEdges.hasMoreElements()) {
                Edge edge = (Edge)edgeIter.next();
                CFGNode jumpNode = (CFGNode)edge.getStart();
                Instruction instr = jumpNode.getInstruction();
                System.out.println("****"+newTargetLabel);

                //instr.setTargetLabel(newTargetLabel);
                instr.setBranchTarget(newTargetLabel);

                System.out.println(instr.toString());
        }
        
    }
    
    /** 
     *  Marks the basic blocks with first and last index and input 
     *  for mapping to parseobjectlist 
     */
    /*
    private void markBasicBlocks(){
        Iterator iter = program.getBasicBlocks().getNodes().iterator();
        List cfgNodes;
        CFGNode cfgNode;
        int i;
        while (iter.hasNext()){
            BasicBlock bb = (BasicBlock)iter.next();
            cfgNodes = bb.getNodes();
            for (i = 0; i < cfgNodes.size(); i++){
                cfgNode = (CFGNode)cfgNodes.get(i);
                if ((cfgNode.getInputPosition() != null) &&
                    (cfgNode.getInstruction() instanceof Instruction)){
                    bb.setInput(cfgNode.getInputPosition().getInput());
                    bb.setFirst(getIndexObjList((CFGNode)cfgNodes.get(i)));
                    break;
                }
            }
            
            for (i = cfgNodes.size()-1; i >= 0; i--){
                cfgNode = (CFGNode)cfgNodes.get(i);
                if ((cfgNode.getInputPosition() != null) &&
                    (cfgNode.getInstruction() instanceof Instruction)){
                    bb.setLast(getIndexObjList((CFGNode)cfgNodes.get(i)));
                    break;
                }
            }
            
        }
    }
    */
   


    /* needed when inserting/deleting instrs */
    protected void recomputeAdds(){
        
    }

    /* for edit of RISC-type load/stores */
    protected void editLoadInstr(List loads, BasicBlock bb){
        // kps - might need a rewrite ...
        //        Input input = bb.getInput();
        //        if (input == null) return;
        //        List inputLines = input.getInputLines();
        //        Iterator iter = inputLines.iterator();
        //        while (iter.hasNext()){
        //            Instruction i = (Instruction)iter.next();
        //            List regs = i.getArgs();
        //        }
    }
        //            BasicBlock bb = (BasicBlock)iter.next();
    //    System.out.println(bb.getName());
        //    System.out.println(bb.getFirst() +" "+ bb.getLast());
            
            
    //   Instruction instr = machine.makeLoad(null);
    //    System.out.println(instr.getOper());
    //    loads = locateInstr(bb, instr);
    //    System.out.println("amount of ldrs in this basicblock:"+loads.size());
            
    //        }
//

    //}

    /* same */
    protected void editStoreInstr(Instruction instruction){
        
    }

    /*
    protected void getWordOps(){
        List inputs = program.getInputs();
        Iterator iter = inputs.iterator();
        while (iter.hasNext()){
            Input input = (Input)iter.next();
            List inputLines = input.getInputLines();
            Iterator iter2 = inputLines.iterator();
            while (iter2.hasNext()){
                Object o = (Object)iter2.next();
                if (o instanceof PseudoOp){
                    PseudoOp pOp = (PseudoOp)o;
                    System.out.println(pOp.toString());
                    if (pOp instanceof WordPseudoOp){
                        WordPseudoOp wOp = (WordPseudoOp)pOp;
                        Expression expr = wOp.getExpression();
                        if (expr instanceof IdExpression){
                            IdExpression idExpr = (IdExpression)expr;
                            System.out.println(idExpr.toString());
                            idExpr.setId(""+4343);
                        }
                    }
                }
                
            }
        }
    }
    */
}
