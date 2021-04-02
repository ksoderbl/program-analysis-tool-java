package microinstr;

import program.Program;
import machine.Machine;
import machine.Register;

import c55x.instr.C55xShiftOperand;
import c55x.instr.C55xMemoryAccessOperand;
import c55x.instr.C55xRegisterOperand;
import c55x.instr.C55xImmediateOperand;

import instr.Operand;

public class C55xShift extends Microinstruction{

    C55xShiftOperand shiftOp;
    MicroOperand source1; // for memoryaccessoperands
    int index = 0;
    long value1 = 0;
    long value2 = 0;

//    public C55xShift(C55xShiftOperand operand, long value2, int index){
//        super("C55xShift");
//        this.shiftOp = operand;
//        this.index = index;
//        this.value2 = value2;
//    }
 
    // Only one constructor, decompose the values during microinstruction creation to avoid instanceof
    // in this code; note also that decomposing the memoryAccessOperands would not be elegantly possible 
    // to do here either
    public C55xShift(C55xShiftOperand operand, long value1, long value2, int index){
        super("C55xShift");
        this.shiftOp = operand;
        this.index = index;
        this.value1 = value1;
        this.value2 = value2;
    }
        
       public C55xShift(C55xShiftOperand operand, MicroOperand value1, long value2, int index){
        super("C55xShift");
        this.shiftOp = operand;
        this.index = index;
        this.source1 = value1;
        this.value2 = value2;
    }



 
    public void execute(Program program, Machine machine){
        boolean debug = program.getOptions().getDebugMC();        
        Operand op1 = shiftOp.getOp1();
        Operand op2 = shiftOp.getOp2();
        Register reg1, reg2;
        long result = 0;
        boolean lShift = shiftOp.getLShift();

        
        if (op1 instanceof C55xRegisterOperand){
            reg1 = ((C55xRegisterOperand)op1).getFirstReg(machine);
            value1 = reg1.getValue();
        }
        
        if (op1 instanceof C55xImmediateOperand)
            value1 = ((C55xImmediateOperand)op1).getValue();

        if (op1 instanceof C55xMemoryAccessOperand)
            value1 = source1.getValue();

        if (op2 instanceof C55xRegisterOperand){
            reg2 = ((C55xRegisterOperand)op2).getFirstReg(machine);
            value2 = reg2.getValue();
        }
        
        if (op2 instanceof C55xImmediateOperand)
            value2 = ((C55xImmediateOperand)op2).getValue();

        //if (debug) System.out.println(" value1: 0x"+Long.toHexString(value1)+" value2: "+value2);
        
        if (value2 < 0){
            value2 = -value2;
            if (lShift)
                lShift = false; 
            else
                lShift = true;
        }
        if (lShift)
            result = value1 << value2;        
        else
            result = value1 >> value2;

        if (debug) System.out.println(" C55xShift: source:0x"+Long.toHexString(value1)
                                      + "(" + value1 + ")"
                                      + " shiftvalue:"+value2
                                      + " shiftdir:" + (lShift?"left":"right")
                                      + " result:0x" + Long.toHexString(result)
                                      + "(" + result + ")"
                                      );

        machine.setDataResult(result, 64, index);
    }
    
}
