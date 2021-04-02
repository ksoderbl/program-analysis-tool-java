
package arm.instr;

import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.Iterator;

import arm.ARMMachine;
import instr.Instruction;
import instr.Operation;
import instr.Operand;
import machine.Register;
import main.*;

/**
 * This class represents one machine language instruction
 * Extended from JSPAF to handle code emission.
 */
public class ARMInstruction extends Instruction
{
    /** Attributes */

    /** the opcode of the instruction */
    //private String oper;

    /** original source code */
    private String code;

    /** arguments of instruction */
    //private List args;


    /** regs that the instruction uses */
    private List uses;

    /** regs that the instruction defines */
    private List defs;



    /**
     * Constructs a new instruction
     *
     * @param mnemonic the opcode of the instruction
     * @param args arguments of the instruction
     * @param a hack
     * @return the new instruction
     */
    public ARMInstruction(String mnemonic, List args,
                          int ud, // hack: used to determine uses and defs
                          boolean conditional) {
        super.addOperation(mnemonic, args, "unknown");
        super.setAddr(new Long(0));
        super.setSize(ARMMachine.INSTRUCTIONSIZE);

        this.conditional = conditional;

        //System.out.println("mnem = " + mnemonic);
        //System.out.println("args = " + args);

        /* Ugly hack: Handle uses and defs. */
        if (ud == 1) {
            uses = tail(args);
            defs = head(args);
        }
        else if (ud == 2) {
            uses = head(args);
            defs = tail(args);
        }
        else if (ud == 4) {
            uses = tail(args);
            defs = null;
        }
        else if (ud == 20) {
            try {
                uses = tail(args.subList(1, args.size()));
                defs = head(args.subList(1, args.size()));
            }
            catch (Exception e) {
                Main.fatal("Exception caught for instruction '" + this);
            }
        }
        else if (ud == 30) {
            try {
                uses = head(args.subList(1, args.size()));
                defs = tail(args.subList(1, args.size()));
            }
            catch (Exception e) {
                Main.fatal("Exception caught for instruction '" + this);
            }
        }
        else {
            Main.fatal("ud value " + ud + " not supported.");
        }
        
        //System.out.println("uses = " + uses);
        //System.out.println("defs = " + defs);

        //    System.out.println("Constructing instruction " + code);
    }

    private static List head(List list) {
        if (list == null) {
            return null;
        }

        List result = null;

        // get first element of list
        Object elem = list.get(0);
        if (elem instanceof Register) {
            result = new ArrayList();
            result.add((Register) elem);
        }

        return result;
    }


    /**
     * Constructs a new List that contains the tail of a list, i.e.
     * a list containing all but the first element of the original list.
     * Only those elements that are instances of Register are included in 
     * the returned set.
     *
     * @param list a list
     * @return tail of the list as a List
     */
    public static List tail(List list) {
        if (list == null) {
            return null;
        }
        List result = null;
        Iterator iter = list.iterator();

        // skip first element of list
        if (iter.hasNext()) {
            iter.next();
        }
        // add rest of the list to result
        while (iter.hasNext()) {
            Object elem = iter.next();
            if (elem instanceof Register) {
                if (result == null)
                    result = new ArrayList();
                result.add((Register) elem);
            }
        }

        return result;
    }




    
    /**
     * @return the operation mnemonic of this instruction
     */
    public String getMnemonic() {
        return super.getOperation(0).getMnemonic();
    }


    /**
     * @return arguments of the instruction
     */
    public List getArgs() {
        return super.getOperation(0).getArgs();
    }


    /**
     * Function for emitting the instruction.
     *
     * @return Instruction as formatted string to emit
     */
    public String emit() {
        String result = "";
        Operation oper = getOperation(0);
        List args = oper.getArgs();

        result += oper.getMnemonic();
        result += "\t";
        result += args;

        return result;
        /*
        int numargs = 0;
        String argstring = "";
        ListIterator liter = args.listIterator();

        while (liter.hasNext()) {
            if (numargs > 0)
                argstring += ",";
            Object next = liter.next();
            Operand op = null;
            if (next instanceof Operand) {
                op = (Operand)next;
            }
            else {
                
            }
            argstring += next.toString();
            numargs++;
        }
        //if (!argstring.equals(""))
        //    argstring = " " + argstring;

        result += argstring;

        return result;
        */
    }





    /**
     * @param args new arguments of the instruction
     */
    //public void setArgs(List args) {
    //this.args = args;
    //}

    /**
     * @return regs used by the instruction
     */
    public List getUses() {
        return uses;
    }

    /**
     * @return regs defined by the instruction
     */
    public List getDefs() {
        return defs;
    }

    /**
     * @param defs List of the defined registers 
     */
    public void setDefs(List defs) {
        this.defs = defs;
    }

    /**
     * @param uses List of the used registers
     */
    public void setUses(List uses) {
        this.uses = uses;
    }

    // conditional arm instructions
    private boolean conditional = false;
    public void setConditional(boolean cond) {
        conditional = cond;
    }
    public boolean isConditional() {
        return conditional;
    }



    // Generic instruction interface for CFG e.g.
    // setBranch, setCall and setReturn here
    // will set the type of the first operation.
    // These work well if there is only one operation
    // in the instruction, e.g. for ARM
    // For architectures with multi-operation instructions,
    // these should be overridden.
    public void setBranch(boolean conditional) {
        Operation op = getOperation(0);
        op.setBranch(conditional);
    }
    public void setBranchTarget(String label) {
        Operation op = getOperation(0);
        op.setBranchTarget(label);
    }
    public void setCall(boolean conditional) {
        Operation op = getOperation(0);
        op.setCall(conditional);
    }
    public void setReturn(boolean conditional) {
        Operation op = getOperation(0);
        op.setReturn(conditional);
    }

}
