package microinstr;

import program.Program;
import machine.Machine;
import machine.Register;

import c55x.instr.C55xRelOpOperand;
import c55x.instr.C55xRegisterOperand;
import c55x.instr.C55xImmediateOperand;
import c55x.instr.C55xBitOperand;
import c55x.instr.C55xConditionFieldOperand;
import c55x.C55xMachine;

public class C55xCondition extends Microinstruction{
    
    C55xConditionFieldOperand cond;
    
    public C55xCondition(C55xConditionFieldOperand cond){
        super("C55xCondition");
        this.cond = cond;
    }
    
    
    public void execute(Program program, Machine machine){
        boolean debug = program.getOptions().getDebugMC();
        Register st0 = machine.getRegister("st0");
        Register reg;
        int statusBitRegNum;
        int type = cond.getType();
        String opType;        
        
        // clear condition
        machine.setCondition(false);
        
        switch(type){
            
        case C55xConditionFieldOperand.ONEBIT:
            boolean not = cond.getNot(); // returns false if ! before cond otherwise true
            statusBitRegNum = cond.getFirstBitOp().getBit(machine);
            if (st0.testBit(statusBitRegNum) == 1) {
                machine.setCondition(not);
                if (debug) System.out.println(" ONEBIT: condition set");
            }
            if (st0.testBit(statusBitRegNum) == 0){ 
                machine.setCondition(!not);
                if (debug) System.out.println(" ONEBIT: condition not set ");
            }
            break;
        case C55xConditionFieldOperand.RELOP:
            reg = cond.getFirstReg(machine);
            long value = cond.getImmediateValue();
            opType = cond.getOpType();
            
            if (opType.equals("==")){
                if (value == reg.getValue()){
                    machine.setCondition(true);
                    if (debug) System.out.println(" RELOP: condition set");
                }
            }
            
            else if (opType.equals("!=")){
                if (value != reg.getValue()){
                    machine.setCondition(true);
                    if (debug) System.out.println(" RELOP: condition set");
                }
            }
            else if (opType.equals("<=")){
                if (reg.getValue() <= value){
                    machine.setCondition(true);
                    if (debug) System.out.println(" RELOP: condition set");
                }
            }
            
            else if (opType.equals("<")){
                if (reg.getValue() < value){
                    machine.setCondition(true);
                    if (debug) System.out.println(" RELOP: condition set");
                }
            }
            
            
            else if (opType.equals(">=")){
                if (reg.getValue() >= value){
                    machine.setCondition(true);
                    if (debug) System.out.println(" RELOP: condition set");
                }
            }
            
            else if (opType.equals(">")){
                if (reg.getValue() > value){
                    machine.setCondition(true);
                    if (debug) System.out.println(" RELOP: condition set");
                }
            }
            
                  else throw new IllegalArgumentException("C55xCondition: RELOP type "+opType+" not supported");
            break;        
            
        case C55xConditionFieldOperand.OVERFLOW:
            long overflow;
            reg = cond.getFirstReg(machine);
            overflow = ((C55xMachine)machine).getOverflow(reg);
            if (debug) System.out.println(" OVERFLOW:"+reg.getName()+" "+overflow);

            if (overflow == 1) { 
               machine.setCondition(true);
               if (debug) System.out.println(" OVERFLOW: condition set");
            }
            break;
        default:
            throw new IllegalArgumentException("C55xCondition: type: "+type+" not supported");
        }
        
        // st0.setBit(bit);
    }
    
    
    
    
}
