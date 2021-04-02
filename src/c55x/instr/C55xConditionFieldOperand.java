/**
 * C55xConditionFieldOperand.java
 *
 * @author Kristian Söderblom
 */

package c55x.instr;

import c55x.instr.C55xRegisterOperand;
import c55x.instr.C55xBitOperand;
import machine.Machine;
import machine.Register;
import instr.Operand;
import main.*;

/**
 * This class represents a condition field of C55x.
 */
public class C55xConditionFieldOperand extends Operand {

    // types of condition field
    public static final int RELOP = 0;
    public static final int OVERFLOW = 1;
    public static final int ONEBIT = 2;
    public static final int TWOBIT = 3;
    public static final int NE_0 = 4; // hack for L16_ARn_mod_NE_0 production in C55xDisParser.jj

    private int type = -1;
    private C55xRegisterOperand r;
    private C55xBitOperand b, b2;
    private C55xImmediateOperand imm;
    private C55xMemoryAccessOperand ma;
    private String op, not, not2;

    // e.g. T0 != #0
    public C55xConditionFieldOperand(String op,
                                     C55xRegisterOperand r, C55xImmediateOperand imm) {
        this.type = RELOP;
        this.op = op;
        this.imm = imm;
        this.r = r;
    }

    // e.g. !overflow(AC0)
    public C55xConditionFieldOperand(String not, C55xRegisterOperand r) {
        this.type = OVERFLOW;
        this.not = not;
        this.r = r;
    }

    // e.g. !TC1
    public C55xConditionFieldOperand(String not, C55xBitOperand b) {
        this.type = ONEBIT;
        this.not = not; // either "!" or "";
        this.b = b;
    }

    // e.g. !TC1 | TC2
    public C55xConditionFieldOperand(String not1, C55xBitOperand tc1,
                                     String op,
                                     String not2, C55xBitOperand tc2) {
        this.type = TWOBIT;
        this.not = not1;
        this.b = tc1;
        this.op = op;
        this.not2 = not2;
        this.b2 = tc2;
    }

    // e.g. *AR1(#0006h) != #0
    public C55xConditionFieldOperand(C55xMemoryAccessOperand ma) {
        this.type = NE_0;
        this.ma = ma;
    }


    public String toString() {
        String s = "";
        switch (type) {
        case RELOP:
            s = "" + r + " " + op + " " + imm;
            break;
        case OVERFLOW:
            s = "" + not + "overflow(" + r + ")";
            break;
        case ONEBIT:
            s = "" + not + b;
            break;
        case TWOBIT:
            s = "" + not + b + " " + op + " " + not2 + b2;
            break;
        case NE_0:
            s = "" + ma + " != #0";
            break;
        default:
            Main.fatal("C55xConditionFieldOperand: unknown type " + type);
            break;
        }
        return s;
    }

    //////////////////////////////////////
    public int getType(){
        return type;
    }

    public boolean getNot(){
            if (not.equals("!")) return false;
        else return true;
    }

    public String getOpType(){
        return op;
    }

    public C55xBitOperand getFirstBitOp(){
        return b;
    }

    public C55xBitOperand getSecondBitOp(){
        return b2;
    }

    public long getImmediateValue(){
        return imm.getValue();
    }        

    public Register getFirstReg(Machine machine){
        return machine.getRegister(r.toString());
    }
  
    public C55xMemoryAccessOperand getMemAccessOp(){
        return ma;
    }
}
