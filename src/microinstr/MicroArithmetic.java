package microinstr;

import program.Program;
import machine.Machine;
import machine.Register;
import c55x.C55xMachine;

public class MicroArithmetic extends Microinstruction{

    MicroOperand source;
    MicroOperand destination;
    String type;
    int index = 0;


    public MicroArithmetic(MicroOperand source, MicroOperand destination, String type, int index){
        super("MicroArithmetic");
        this.source = source;
        this.destination = destination;
        this.type = type;
        this.index = index; // index to restore result
    }


    
    public void execute(Program program, Machine machine){
        
        long op1 = source.getValue();
        long op2 = destination.getValue();
      
        if (type.equals("ADD"))
            add(program, machine, op1, op2);
        else if (type.equals("SUB")){
            op1 = ~op1; op1++;
            add(program, machine, op1, op2);
        }
        else if (type.equals("MUL")){
            mul(program, machine);
        }
        else throw new IllegalArgumentException("MicroArithmetic: unknown argument:"+type);
    }
    
        
    public void add(Program program, Machine machine, long op1, long op2){
        
        boolean debug = program.getOptions().getDebugMC();
      


        long result = op1 + op2;
        //if (debug) System.out.println(" result:"+Long.toHexString(result));
        if (source.isSigned() || destination.isSigned()){
            
            // we're now doing signed arithmetic
            long bit1 = BitUtils.testBit(op1, (int)source.getSign());
            long bit2 = BitUtils.testBit(op2, (int)destination.getSign());
            long bit3 = BitUtils.testBit(result, source.getBitSize());
            
            
            // positive overflow
            if (((bit1 == 0) && (bit2 == 0) && (bit3 == 1)) ||
                ((bit1 == 0) && (bit2 == 1) && (bit3 == 1))){
                if (destination instanceof MicroRegisterOperand)
                    machine.setOverflow(((MicroRegisterOperand)destination).getRegister(), true);
                
            }
            
            // negative overflow
            if (((bit1 == 1) && (bit2 == 1) && (bit3 == 0)) ||
                    ((bit1 == 1) && (bit2 == 0) && (bit3 == 0))){
                if (destination instanceof MicroRegisterOperand)
                    machine.setOverflow(((MicroRegisterOperand)destination).getRegister(), false);
                
            }
            
            
            
            }
        
        else {
            // we're doing unsigned arithmetic
            long bit1 = BitUtils.testBit(op1, source.getBitSize());
            long bit2 = BitUtils.testBit(op2, destination.getBitSize());
            long bit3 = BitUtils.testBit(result, source.getBitSize());
            
            if (((bit1 == 1) && (bit2 == 1) && (bit3 == 1))){
                if (destination instanceof MicroRegisterOperand)
                    machine.setCarry(((MicroRegisterOperand)destination).getRegister());
            } 
            
            
            
        }

        if (debug) System.out.println(" MicroArithmetic: "
                                      + type
                                      + " src:"
                                      + BitUtils.valueToString(source.getValue())
                                      + " dst:"
                                      + BitUtils.valueToString(destination.getValue())
                                      + " result:"
                                      + BitUtils.valueToString(result)
                                      + " index:"+(index+machine.getMachineResultOffset()));

        machine.setDataResult(result, 64, index);

        //System.out.println("index:"+index+ " result:"+Long.toHexString(machine.getDataResult(0).getValue()));
    }
    
    public void mul(Program program, Machine machine){
        boolean debug = program.getOptions().getDebugMC();
        long op1 = source.getValue();
        long op2 = destination.getValue();
        long result = op1*op2;
        long frct = ((C55xMachine)machine).getFRCT();

        if (frct > 0) {
            // kps - too much messages, let's disable this
            //if (debug) System.out.println(" MicroArithmetic: MUL FRCT=" + frct + ","
            //                                          + " shifting result left by one bit");
            result <<= 1;
        }

        // pointless to show q.15 here
        if (debug) System.out.println(" MicroArithmetic: "
                                      + type
                                      + " src:"
                                      + BitUtils.valueToString2(source.getValue())
                                      + " dst:"
                                      + BitUtils.valueToString2(destination.getValue())
                                      + " result:"
                                      + BitUtils.valueToString2(result)
                                      + " index:"+(index+machine.getMachineResultOffset()));
        
        machine.setDataResult(result,64,index);
        
    }
}
