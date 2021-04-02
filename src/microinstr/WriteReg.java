package microinstr;

import program.Program;
import machine.Machine;
import c55x.instr.C55xRegisterOperand;
import machine.Register;
import microinstr.WriteReg;

/**
 * moves a value or another reg to a reg
 */

public class WriteReg extends Microinstruction{
    
    MicroOperand source;
    int sourceSize = 0;
    int sourceShift;
    MicroRegisterOperand destination;
    int destinationSize = 0;
    int destinationShift;

    public WriteReg(MicroOperand source, MicroRegisterOperand destination){
        super("WriteReg");
        this.source = source;
        this.destination = destination;
    }

    // use if you want to read only specific bits in source operand
    public WriteReg(MicroOperand source,
                    int sourceSize, int sourceShift,
                    MicroRegisterOperand destination) {

        super("WriteReg");
        this.source = source;
        this.sourceSize = sourceSize;
        this.sourceShift = sourceShift;
        this.destination = destination;
    }

    // use if you want to write only specific bits in destination register
    public WriteReg(MicroOperand source,
                    MicroRegisterOperand destination,
                    int destinationSize, int destinationShift) {
        super("WriteReg");
        this.source = source;
        this.destination = destination;
        this.destinationSize = destinationSize;
        this.destinationShift = destinationShift;
    }

    // use if you want to read only specific bits in source operand
    // and if you want to write only specific bits in destination register
    public WriteReg(MicroOperand source,
                    int sourceSize, int sourceShift,
                    MicroRegisterOperand destination,
                    int destinationSize, int destinationShift) {
        super("WriteReg");
        this.source = source;
        this.sourceSize = sourceSize;
        this.sourceShift = sourceShift;
        this.destination = destination;
        this.destinationSize = destinationSize;
        this.destinationShift = destinationShift;
    }

    public String getRegisterName(){
        return destination.getName();
    }        


    public void execute(Program program, Machine machine){
        boolean debug = program.getOptions().getDebugMC();
        Register mmReg;
        long src = source.getValue();
        long dst = destination.getValue();

        if (sourceSize > 0)
            src = BitUtils.readData(src, sourceSize, sourceShift);
        if (destinationSize > 0)
            src = BitUtils.writeData(src, dst, destinationSize, destinationShift);

        destination.setValue(src);
        if (debug)
            System.out.println(" WriteReg: writing "
                               + BitUtils.valueToString(src)
                               + " op size "
                               + source.getBitSize()+  " to "+destination.getName());

        if (destination.getName().equals("brc1")) {
            Register brs1 = machine.getRegister("brs1");
            brs1.setValue(destination.getValue());
            if (debug) System.out.println(" WriteReg: wrote brc1 to brs1"); 
        } 
        


    }
    
}
