package microinstr;

import program.Program;
import machine.Machine;
import machine.Register;

import c55x.instr.C55xRelOpOperand;
import c55x.instr.C55xRegisterOperand;
import c55x.instr.C55xImmediateOperand;
import c55x.instr.C55xBitOperand;

public class C55xCompare extends Microinstruction {
    C55xRelOpOperand relOp;
    C55xBitOperand bitOp;
    MicroOperand sMem;

    public C55xCompare(C55xRelOpOperand relOp, C55xBitOperand bitOp){
        super("C55xCompare");
        this.relOp = relOp;
        this.bitOp = bitOp;
    }

     public C55xCompare(MicroOperand sMem, C55xRelOpOperand relOp, C55xBitOperand bitOp){
        super("C55xCompare");
        this.relOp = relOp;
        this.sMem = sMem;
        this.bitOp = bitOp;
     }

    public void execute(Program program, Machine machine) {
        boolean debug = program.getOptions().getDebugMC();
        String relOpString = relOp.getRelOp();
        Register st0 = bitOp.getRegister(machine);
        int bit = bitOp.getBit(machine);
        long value1 = 0, value2 = 0;
        int bitSize1, bitSize2;

        if (!st0.getName().equals("st0"))
            throw new NullPointerException("not st0: " + st0);

        if (sMem == null) {
            value1 = relOp.getFirstReg(machine).getValue();
            value2 = relOp.getSecondReg(machine).getValue();
            bitSize1 = relOp.getFirstReg(machine).getBitSize();
            bitSize2 = relOp.getSecondReg(machine).getBitSize();
        }
        else {
            // cmp smem, k16, both operands 16 bits
            value1 = sMem.getValue();
            value2 = relOp.getImmediateAccessOp().getValue();
            bitSize1 = bitSize2 = 16;
        }


        //switch (relOp.relOpType()){
        //case C55xConditionFieldOperand.NE_0:
        //         break;
        //}
        //     BitUtils.signedToLong(0xffffL, 16)
     

        long result = BitUtils.signedToLong(value1, bitSize1)
            - BitUtils.signedToLong(value2, bitSize2);
        if (debug) {
            System.out.println(" C55xCompare:"
                               + " relOp:\"" + relOp + "\""
                               + " relOpString:\"" + relOpString + "\""
                               + " bitOp:" + bitOp
                               + " smem:"  + sMem
                               + " value1:0x"+Long.toHexString(value1)
                               + "("+value1+")"
                               + " value2:0x"+Long.toHexString(value2)
                               + "("+value2+")"
                               + " result:"+Long.toHexString(result)
                               + "("+result+")");
        }

        st0.clearBit(bit);
        //        System.out.println("clearing bit:"+bit+" in st0"+"result "+result);
        if (relOpString.equals("==")) {
            if (result == 0L) {
                st0.setBit(bit);
                if (debug) System.out.println(" setting bit (==):"+bit+" in st0");
            }
            else {
                if (debug) System.out.println(" clearing bit (==):"+bit+" in st0");
            }
        }
        if (relOpString.equals("<")) {
            if (result < 0L) {
                st0.setBit(bit);
                if (debug) System.out.println(" setting bit (<):"+bit+" in st0");
            }
            else {
                if (debug) System.out.println(" clearing bit (<):"+bit+" in st0");
            }
        }
        if (relOpString.equals(">=")) {
            if (result >= 0L) {
                st0.setBit(bit);
                if (debug) System.out.println(" setting bit (>=):"+bit+" in st0");
            }
            else {
                if (debug) System.out.println(" clearing bit (>=):"+bit+" in st0");
            }
        }
        if (relOpString.equals("!=")) {
            if (result != 0L) {
                st0.setBit(bit);
                if (debug) System.out.println(" setting bit (!=):"+bit+" in st0");
            }
            else {
                if (debug) System.out.println(" clearing bit (!=):"+bit+" in st0");
            }
        }
    }
}
