package microinstr;

import program.Program;
import machine.Machine;
import machine.Register;

public class Logical extends Microinstruction{

    MicroOperand source;
    MicroOperand destination;
    String type;
    int index = 0;
   

    public Logical(MicroOperand source, MicroOperand destination, String type, int index){
        super("Logical");
        this.source = source;
        this.destination = destination;
        this.type = type;
        this.index = index;
    }
    
    // for use with NOT, unary operand 
    public Logical(MicroOperand source, String type, int index){
        super("Logical");
        this.source = source;
        this.type = type;
        this.index = index;
    }


    public void execute(Program program, Machine machine){

        long op1 = source.getValue();
        long op2 = 0;
        if (destination != null){
            op2 = destination.getValue();
        //    op1 = BitUtils.readData(op2, 16, 0); // if you find problems with the destination data size, use this    
        }
        boolean debug = program.getOptions().getDebugMC();
        

        if (debug){ 
            if (destination != null)
                System.out.print(" Logical: "
                                 + type
                                 + " source:0x"+Long.toHexString(op1)
                                 + " destination:0x"+ Long.toHexString(op2));
        }
        
        if (type.equals("OR"))
            or(program, machine, op1, op2);

        else if (type.equals("AND")){
            and(program, machine, op1, op2);
        }

        else if (type.equals("NOT")){
            not (program, machine, op1);
        }

        else if (type.equals("XOR")){
            xor(program, machine, op1, op2);
        }

        else throw new IllegalArgumentException("Logical: unknown argument:"+type);
    }
    
        
    public void or(Program program, Machine machine, long op1, long op2){
     
             long result = op1 | op2;
        boolean debug = program.getOptions().getDebugMC();
        if (debug)
            System.out.println(" result:0x"
                               +Long.toHexString(result)
                               +" index:"+index);
        machine.setDataResult(result, 64, index);
    }

    public void and(Program program, Machine machine, long op1, long op2){
        
        long result = op1 & op2;
        boolean debug = program.getOptions().getDebugMC();
        if (debug)
            System.out.println(" result:0x"
                               +Long.toHexString(result)
                               +" index:"+index);
        machine.setDataResult(result, 64, index);
        
    }

    public void not(Program program, Machine machine, long op1){

        long result = ~op1;
        boolean debug = program.getOptions().getDebugMC();
        if (debug)
            System.out.println(" result:0x"
                               +Long.toHexString(result)
                               +" index:"+index);
        machine.setDataResult(result, 64, index);
    }

    
    public void xor(Program program, Machine machine, long op1, long op2){
        
        long result = op1 ^ op2;
        boolean debug = program.getOptions().getDebugMC();
        if (debug)
            System.out.println(" result:0x"
                               +Long.toHexString(result)
                               +" index:"+index);
        machine.setDataResult(result, 64, index);
    }

}
