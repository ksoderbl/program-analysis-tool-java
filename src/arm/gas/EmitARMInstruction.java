/**
 * EmitARMInstruction.java
 * 
 * Initial version
 *
 * @date 13.2.2005
 *
 * @author Ilari L.
 */
package arm.gas;

import instr.Instruction;
import arm.instr.*;
import emit.EmitInstruction;
import main.*;

import java.io.PrintStream;
import java.util.ListIterator;
import java.util.List;

/**
 * This class is responsible for emitting a single instruction.
 * It takes in an <code>Instruction</code> object of program's
 * intermediate representation and emits it formatted according to
 * ARM7 assembly instruction set syntax.
 */
public class EmitARMInstruction implements EmitInstruction
{
    private ARMInstruction instr;

    /** Constructor for EmitARMInstruction class.     */
    public EmitARMInstruction() {
  
    }

    /**
     * Main wrapper function for emitting the instruction.
     * Examines the type of the instruction and calls 
     * appropriate layout function to format the result string.
     *
     * @return Instruction as formatted string to emit
     */
    public String emit(Instruction instruction) {
        ARMInstruction instr = (ARMInstruction)instruction;
        String emitstring = "";
        String argstring = "";

        this.instr = instr;
       
        if (instr.getArgs() == null) {
            argstring = "";
        } else {
            argstring = getMemArgs();
        }

        emitstring = instr.getMnemonic() + "\t" + argstring;

        if (instr.getBranchTarget() != null)
            emitstring += instr.getBranchTarget();

        return emitstring;
    }

    /**
     * Formats arguments of memory instruction.
     *
     * @return Arguments as formatted string
     */
    private String getMemArgs() {

        String argstring = "";
        String oper = instr.getMnemonic().toLowerCase();
        int addrMode = 2;
        List tempList;

        if ((oper.startsWith("stm")) ||
            (oper.startsWith("ldm"))) {
            argstring = formatReglist(instr.getArgs());
        
        } else if ((oper.startsWith("str")) || (oper.startsWith("ldr")) ||
                   (oper.startsWith("stc")) || (oper.startsWith("ldc"))) {

            // Addressing mode:
            // H | SH | SB | D => mode 3
            // else => mode 2
            if ((oper.endsWith("h")) || (oper.endsWith("sh")) ||
                (oper.endsWith("sb")) || (oper.endsWith("d"))) {

                addrMode = 3;
                
            } else {
                
                addrMode = 2;
            }
            
            argstring = formatAmodeList(instr.getArgs(), addrMode);
           
        } else if (oper.startsWith("msr")) {
            
            // In msr instruction CPSR/SPSR register is repeated
            // at the end of the list, 
            // therefore we'll leave the last argument out
            tempList = instr.getArgs();
            argstring = formatDefaultList(tempList.subList(0, 
                                                           tempList.size()-1));

        } else if (oper.startsWith("mrs")) {
            
            // In mrs instruction CPSR/SPSR register is repeated
            // at the start of the list,
            // accordingly we won't emit the first argument
            tempList = instr.getArgs();
            argstring = formatDefaultList(tempList.subList(1, 
                                                           tempList.size()));
        } else {
            
            argstring = formatDefaultList(instr.getArgs());
        }

        return argstring;        
    }

    /**
     * Formats given arguments as a reglist
     * 
     * @param A <code>List</code> containing the arguments
     * @return Given list formatted as reglist style string
     */
    private String formatReglist(List arglist) {
        
        ListIterator liter = null;
        String argstring = "";
        String argrest = "";
        Object argObject = null;
        int magic = -1;

        liter = arglist.listIterator();

        // First register, Rd
        if (liter.hasNext()) {
            argObject = liter.next();
            if (argObject == null) {
                argstring = "";
            } else {
                argstring = argObject.toString();
            }

            // Other registers, <reglist>
            if (liter.hasNext()) {
        
                argObject = liter.next();
                while (argObject == null && liter.hasNext()) {
                    argObject = liter.next();
                }

                if (argObject != null) {
                    // First of list
                    argrest = argrest + ", {";
                    argrest = argrest + argObject.toString();

                    
                    // The rest
                    while (liter.hasNext()) {
                        argObject = liter.next();
                        while (argObject == null && liter.hasNext()) {
                            argObject = liter.next();
                        }
                        if (argObject != null) {
                            // If this is last argument, check for magic number
                            if (!liter.hasNext()) {
                                magic = checkMagic(argObject.toString());
                            } 
                            if (magic == -1) {
                                argrest = argrest + ", ";
                                argrest = argrest + argObject.toString();
                            }
                        }
                    }
                    /* Now check the magic number:
                     * 0 = no hat, no exclamation mark
                     * 1 = exclamation mark
                     * 10 = hat
                     * 11 = both
                     */
                    switch (magic) {
                    
                    case 0:
                        argstring = argstring + argrest + "}";
                        break;
                    case 1:
                        argstring = argstring + "!" + argrest + "}";
                        break;
                    case 10:
                        argstring = argstring + argrest + "^";
                        break;
                    case 11:
                        argstring = argstring + "!" + argrest + "^";
                        break;
                    default:
                        argstring = argstring + argrest + "}";
                    }
                    
                }
            }
        }
        
        return argstring;
    }
        
    /** 
     * Formats the given arguments in <a_modeN> style.
     *
     * @param The <code>List</code> containing the arguments
     * @return String of arguments formatted in <a_modeN> style
     */
    private String formatAmodeList(List arglist, int addr_mode) {
        ListIterator liter = arglist.listIterator();
        String argstring = "";
        String bracket = "]";
        String argrest = "";
        String sign = "";
        Object argObject = null;
        int magic = -1;


        // First register, Rd
        if (liter.hasNext()) {
            argObject = liter.next();
            if (argObject == null) {
                argstring = "";
            } else {
                argstring = argObject.toString();
            }

            // Other registers, <a_modeN>
            if (liter.hasNext()) {

                argObject = liter.next();
                while (argObject == null && liter.hasNext()) {
                    argObject = liter.next();
                }

                if (argObject != null) {

                    // kps - hack to emit Expressions
                    // check also hack in arm/ARMGCC3Parser.jj,
                    // production RegCommaAddr2
                    if (argObject instanceof misc.Expression) {
                        return argstring + ", " + argObject.toString();
                    }

                    // First of list
                    argstring = argstring + ", [";
                    argstring = argstring + formatArg(argObject.toString());
               
                    // The rest
                    while (liter.hasNext()) {
                        argObject = liter.next();
                        while (argObject == null && liter.hasNext()) {
                            argObject = liter.next();
                        }
                        if (argObject != null) {
                         
                            // Do we have sign before register?
                            if (argObject.toString().equals("+") ||
                                argObject.toString().equals("-")) {

                                // We'll save the sign and look 
                                // for the register
                                sign = (String)argObject;
                                argObject = liter.next();
                                while (argObject == null && liter.hasNext()) {
                                    
                                }

                                // This should not be the case
                                if (argObject == null) break;
                                
                            } else {
                                sign = "";
                            }
                            
                            // If this is last argument, check for magic number
                            if (!liter.hasNext()) {
                                magic = checkMagic(argObject.toString());
                            } 
                            if (magic == -1) {
                                argrest = argrest + ", " + sign;
                                argrest = argrest + formatArg(argObject.toString());
                            }
                        }
                        
                    }
                    if (addr_mode == 2) {
                        /* Addressing mode 2 magic numbers:
                         *
                         * 1 = a_mode5 Immediate offset
                         * 2 = Register offset
                         * 3 = Scaled register offset
                         * 4 = Immediate; mode 5 Pre-indexed
                         * 5 = Register
                         * 6 = Scaled register
                         * 7 = Immediate; Mode 5 Post-indexed
                         * 8 = Register 
                         * 9 = Scaled register
                         *
                         * 1-3, Every argument enclosed with brackets, no exclamation mark 
                         * 4-6, Every argument enclosed with brackets, exclamation mark follows
                         * 7-9, first register enclosed with brackets, no exclamation mark .
                         */

                        switch (magic) {
                            
                        case 1:
                        case 2: 
                        case 3:
                            argstring = argstring + argrest + bracket;
                            break;
                        case 4:
                        case 5:
                        case 6:
                            argstring = argstring + argrest + bracket + "!";
                            break;
                        case 7:
                        case 8:
                        case 9:
                            argstring = argstring + bracket + argrest;
                            break;
                        default:
                            argstring = argstring + argrest + bracket;
                        }
                    } else if (addr_mode == 3) {
                        
                        // addressing mode 3
                        /* Addressing mode 3 magic numbers:
                         *
                         * 1 = Immediate offset
                         * 2 = Register offset
                         * 3 = Immediate pre-indexed
                         * 4 = Register pre-indexed
                         * 5 = Immediate post-indexed
                         * 6 = Register post-indexed
                         *
                         * 1-2, brackets all around, no exclamation mark
                         * 3-4, brackets all around, with exclamation mark
                         * 5-6, first register enclosed with brackets, no exclamation mark
                         */
                        switch (magic) {

                        case 1:
                        case 2:
                            argstring = argstring + argrest + bracket;
                            break;
                        case 3:
                        case 4:
                            argstring = argstring + argrest + bracket + "!";
                            break;
                        case 5:
                        case 6:
                            argstring = argstring + bracket + argrest;
                            break;
                        default:
                            argstring = argstring + argrest + bracket;
                        }
                            
                    } else {

                        // Some default behaviour
                        argstring = argstring + argrest + bracket;
                    }
                    
                }
            }
        }

        return argstring;
    }



    /** 
     * Formats the given arguments in default style.
     *
     * @param The <code>List</code> containing the arguments
     * @return String of arguments formatted in default style
     */
    private String formatDefaultList(List arglist) {
        ListIterator liter = arglist.listIterator();
        String argstring = "";
        Object argObject = null;

        // First argument
        if (liter.hasNext()) {
            argObject = liter.next();
            while (argObject == null && liter.hasNext()) {
                argObject = liter.next();
            }
            
            if (argObject != null) {
                argstring = formatArg(argObject.toString());
            } else {
                argstring = "";
            }


            // Other arguments
            while (liter.hasNext()) {

                argObject = liter.next();
                while (argObject == null && liter.hasNext()) {
                    argObject = liter.next();
                }

                if (argObject != null) {
                    
                    // if last, we'll look for special number

                    argstring = argstring + ", ";
                    argstring = argstring + formatArg(argObject.toString());
                }

            }

        }
        
        return argstring;
    }




    /**
     * Formats a single argument, adds "#" before immediates.
     *
     * @param An argument to format
     * @return formatted argument
     */
    private String formatArg(String arg) {
        
        // If argument is a valid integer, we'll add "#" before it.
        try  {
            Integer.parseInt(arg);
            return "#" + arg;
        } catch (Exception e) {
            return arg;
        }
    }

    /**
     * Formats arguments of other instructions.
     *
     * @return the formatted arguments of instruction
     */
    private String getDefaultArgs() {
        
        return formatDefaultList(instr.getArgs());

    }

    /**
     * Takes in an argument and checks if it is a valid magic number.
     *
     * @param An argument to check
     * @return magic number (0-11) or -1.
     */
    private int checkMagic(String arg) {
        
        int magic = -1;
        // If argument is a valid integer, we'll give it back
        try  {
            magic = Integer.parseInt(arg);
            if (magic > -1 && magic < 12) {
                return magic;
            } else {
                return 0;
            }
        } catch (Exception e) {
            return magic;
        }
    }


}
