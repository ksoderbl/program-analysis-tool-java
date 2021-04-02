package microinstr;

import program.Program;
import machine.Machine;
import machine.Register;

public class Shift extends Microinstruction{

    MicroOperand value;
    MicroOperand shiftValue;
    String type;
    int sourceWidth;
    int destWidth;
    int index = 0;
    
    
    public Shift(MicroOperand value, MicroOperand shiftValue, String type, int index){
        super("Shift");
        this.value = value;
        this.shiftValue = shiftValue;
        this.type = type;
        this.index = index;
    }
    

    public void execute(Program program, Machine machine){
        boolean debug = program.getOptions().getDebugMC();
        long shiftVal = shiftValue.getValue();
        long val = value.getValue();
        long result = 0;

        // how many bits are actually used from operand
        
        //        shiftVal = BitUtils.maskBits(shiftVal, shiftValue.getBitSize());


        if (type.equals("AS")){
            
            if (shiftVal >= 0L){
                result = val << shiftVal; 
            }
            
            else {
                result = val >> (-shiftVal);
            }
        }
        
        else if (type.equals("LS")){
            if (shiftVal >= 0L){
                result = val << shiftVal; 
            }
            else {
                result = val >>> (-shiftVal);
            }
            
            
        }
        
        else throw new NullPointerException();
        

        if (debug) System.out.println(" Shift:"+shiftVal +" value: "+
                           Long.toHexString(value.getValue())+
                           " result:"+Long.toHexString(result));
        machine.setDataResult(result, 64, index);
        //        return new Constant(result);
    }

}
