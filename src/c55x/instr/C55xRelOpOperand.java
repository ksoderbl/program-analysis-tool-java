/**
 * C55xRelOpOperand.java
 *
 * @author Kristian Söderblom
 */

package c55x.instr;

/*import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.PrintStream;
import input.Input;*/
import instr.Operand;
import machine.Register;
import machine.Machine;

/**
 * This class represents an operand with a relational operator. (op1 RELOP op2)
 */
public class C55xRelOpOperand extends Operand
{
    private Operand op1, op2;
    private String relop;

    public C55xRelOpOperand(Operand op1, String relop, Operand op2) {
        this.op1 = op1;
        this.relop = relop;
        this.op2 = op2;
    }

    ///////////////////////////////////////////
    
    public Register getFirstReg(Machine machine){
        C55xRegisterOperand regOp = (C55xRegisterOperand)op1;
        return regOp.getFirstReg(machine);
    }
    
    public Register getSecondReg(Machine machine){
        C55xRegisterOperand regOp = (C55xRegisterOperand)op2;
        return regOp.getFirstReg(machine);
    }


    public String getRelOp(){
        return relop;
    }

    // have to get memoryaccessoperands out to be evaluated first -pgm
    public C55xMemoryAccessOperand getMemAccessOp(){
        if (op1 instanceof C55xMemoryAccessOperand){
            return (C55xMemoryAccessOperand)op1;
        }
        else return null;
    }
    
    // immediateops -pgm
    public C55xImmediateOperand getImmediateAccessOp(){
        if (op2 instanceof C55xImmediateOperand){
            return (C55xImmediateOperand)op2;
        }
        else return null;
    }

    


    public String toString() {
        return op1.toString() + " " + relop + " " + op2.toString();
    }
}
