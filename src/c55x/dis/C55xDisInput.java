/**
 * C55xDisInput.java
 */

package c55x.dis;

import c55x.*;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Iterator;

import input.Input;
import input.InputLineObject;
import instr.Instruction;
import instr.Operation;
import machine.Machine;
import program.BranchTarget;
import program.Program;
import program.Label;
import program.Symbols;
import pseudoOp.PseudoOp;

import main.*;

/**
 * A front-end for TI C55x disassembly.
 *
 * @author Kristian Söderblom
 */

public class C55xDisInput extends Input
{
    /**
     * Constructs a new TI C55x assembler input
     *
     * @param fileName input file name
     * @return the new input
     */
    public C55xDisInput(String fileName) {
        super(fileName);
    }

    /**
     * Creates a control flow graph based on the assembler input files.
     *
     * @param program the program into which the input should be loaded
     * @param machine executing architecture for the program
     */
    public void readInput(Program program, Machine machine) {

        // add this input to the list of inputs that were used to
        // read the program
        program.addInput(this);

        // open the input file
        FileInputStream in;

        try {
            in = new FileInputStream(getFileName());
        } catch (Exception e) {
            Main.fatal("Input file '" + getFileName() + "' not found.");
            return;
        }

               // create the parser
        C55xDisParser parser = new C55xDisParser(in, this,
                                                 (C55xMachine)machine);

        // parse the input file
        try {
            parser.ParseInput();
        }
        catch (ParseException pe) {
            Main.fatal("" + getFileName() + ": Parse error: "
                       + pe.getMessage());
        }

        catch (TokenMgrError tme) {
            Main.fatal("" + getFileName() + ": " + tme.getMessage());
        }

        createPartialCFG(program);

        // kps - hack walk thru the Input List
        // and make labels out of the sections
        Iterator iter = this.getInputLines().iterator();
        
        while (iter.hasNext()) {
            InputLineObject io = (InputLineObject)iter.next();
            PseudoOp pop = io.getPseudoOp();

            // Ugly c55x disassembly specific hack.
            // We want a label ___text__
            // where we see the in the disassembly something like
            // TEXT Section .text, 0x168 bytes at 0x100
            // I assume that if we have a TEXT section .FOO
            // we need a label ___FOO__
            if (pop != null && pop instanceof C55xDisSectionPseudoOp) {
                C55xDisSectionPseudoOp s = (C55xDisSectionPseudoOp)pop;
                
                if (s.getSectionType().equals("TEXT")) {
                    String name = s.getName();
                    // remove leading '.'
                    name = name.substring(1, name.length());
                    name = "___" + name + "__";
                    Label label = new Label(name);
                    Long address = s.getAddr();
                    label.setAddr(address);
                    program.addLabel(label.getName(), address);
                    Main.info("C55xDisInput: added label " + label
                              + " to program.");
                }
            }
        } // while

        //////////////////////////////////////////////////////////////
        // Handle some other control flow instructions.
        // The reason these are handled here and not immediately
        // when the operations are instantiated in C55xMachine, are
        // that they have forward references that need to be resolved.
        //////////////////////////////////////////////////////////////

        // Make an arraylist, containing only instructions,
        // i.e. this does not contain the labels and pseudoops
        ArrayList instructions = new ArrayList();
        iter = this.getInputLines().iterator();
        while (iter.hasNext()) {
            InputLineObject io = (InputLineObject)iter.next();
            Instruction instr = io.getInstruction();
            if (instr != null) {
                instructions.add(instr);
            }
        }
        Main.info("Program has " + instructions.size() + " instructions.");

        //////////////////////////////////////////////////////////////
        // handle xcc
        // xccpart is always considered to go to the next instruction,
        // because that is partially executed if there is a pointer
        // modification. If there is no pointer modification, it
        // doesn't hurt that we're not splitting up the basic block
        // due to this.
        //////////////////////////////////////////////////////////////

        // Let's find branch targets for the xcc family of instructions.
        // xcc is on page 599-... in spru374g.pdf
        Instruction instr = null;
        Operation xccoper = null;
        int index;
        int xccindex = -1; // index of last xcc instruction

        for (int i = 0; i < instructions.size(); i++) {
            instr = (Instruction)instructions.get(i);
            index = i;

            if (xccindex == -1) {
                // xcc might be operation 0
                Operation oper = instr.getOperation(0);
                Operation oper2 = instr.getOperation(2);
                if (oper.getMnemonic().equals("XCC")) {
                    xccoper = oper;
                    xccindex = index;
                    if (oper2 != null) {
                        // Something is parallel with xcc, then only
                        // that is conditionally executed after which
                        // we execute the next instruction normally.
                        // We don't branch to xccindex + 2.
                        xccindex = -1;
                    }
                }                
                // xcc might also be operation 2
                if (xccindex == -1 && oper2 != null) {
                    if (oper2.getMnemonic().equals("XCC")) {
                        xccoper = oper2;
                        xccindex = index;
                    }
                }
            }
            else {
                // looking for xcc branch target
                if (index == xccindex + 2) {
                    Long addr = instr.getAddr();
                    xccoper.setBranch(true); // true: conditional
                    xccoper.setBranchTarget(addr);
                    xccoper = null;
                    xccindex = -1;
                }
            }
        } // for

        //////////////////////////////////////////////////////////////
        // Handle rpt family of instructions that repeat only the
        // following instruction or paralleled instructions.
        //////////////////////////////////////////////////////////////

        // rpt is on page 481-... in spru374g.pdf
        int rptindex = -1; // index of last rpt instruction

        for (int i = 0; i < instructions.size(); i++) {
            instr = (Instruction)instructions.get(i);
            index = i;
        
            if (rptindex == -1) {
                // rpt might be operation 0
                Operation oper = instr.getOperation(0);
                Operation oper2 = instr.getOperation(2);
                if (oper.getMnemonic().equals("RPT")
                    || oper.getMnemonic().equals("RPTADD")
                    || oper.getMnemonic().equals("RPTCC")
                    || oper.getMnemonic().equals("RPTSUB")
                    ) {
                    rptindex = index;
                }                
                // rpt might also be operation 2
                if (rptindex == -1 && oper2 != null) {
                    if (oper2.getMnemonic().equals("RPT")
                        || oper2.getMnemonic().equals("RPTADD")
                        || oper2.getMnemonic().equals("RPTCC")
                        || oper2.getMnemonic().equals("RPTSUB")
                        ) {
                        rptindex = index;
                    }
                }
            }
            else {
                // looking for rpt branch target
                if (index == rptindex + 1) {
                    Long addr = instr.getAddr();
                    Operation oper = instr.getOperation(0);
                    oper.setBranch(true); // true: conditional
                    oper.setBranchTarget(addr);
                    rptindex = -1;
                }
            }
        } // for

        //////////////////////////////////////////////////////////////
        // Handle rptb family of instructions that repeat a block
        // of instructions
        //////////////////////////////////////////////////////////////

        // rptb is on page 489-... in spru374g.pdf

        for (int i = 0; i < instructions.size(); i++) {
            instr = (Instruction)instructions.get(i);
            index = i;

            Operation oper = null;
            Operation oper0 = instr.getOperation(0);
            Operation oper2 = instr.getOperation(2);
            if (oper0.getMnemonic().equals("RPTBLOCAL")
                || oper0.getMnemonic().equals("RPTB")) {
                oper = oper0;
            }
            if (oper == null && oper2 != null) {
                if (oper2.getMnemonic().equals("RPTBLOCAL")
                    || oper2.getMnemonic().equals("RPTB")) {
                    oper = oper2;
                }
            }
            if (oper == null)
                continue;
            //System.out.println("instr = " + instr);
            // The rptb or rptblocal operation has as branch target
            // the last instruction of the repeat block.
            // BranchTarget bt = oper.getBranchTarget();
            Long lastaddr = program.getBranchTargetAddress(instr);
            BranchTarget nullBt = null;
            oper.setBranchTarget(nullBt);
            //System.out.println("lastaddr = " + Long.toHexString(lastaddr.longValue()));

            // Get instruction that is after the rptb or rptblocal,
            // this is the actual branch target.
            Instruction instr2 = (Instruction)instructions.get(i + 1);
            Instruction instr3 = program.getNode(lastaddr).getInstruction();

            Operation operx = instr3.getOperation(0);
            // This operation shouldn't have any branch target if
            // this code was produced by a sane compiler.
            if (operx.getBranchTarget() != null) {
                // problem in c55x/dsplib-2.40/examples/cfft/cfft_t.dis
                System.err.println("C55xDisInput Operation \"" + operx + "\" has branch target.");
                // XXX TODO: this is an error
                //throw new NullPointerException();
            }
            operx.setBranch(true); // true: conditional
            operx.setBranchTarget(instr2.getAddr());

            //System.out.println("instr2 = " + instr2);
            //System.out.println("instr3 = " + instr3);

        } // for

        //System.out.println("SYMBOLS");
        //Symbols l = program.getLabels();
        //System.out.println(l);        

    }
}
