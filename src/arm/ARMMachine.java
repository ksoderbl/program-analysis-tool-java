/**
 * ARMMachine.java
 */

package arm;

import machine.Machine;
import machine.Registers;
import java.util.ArrayList;
import instr.*;
import arm.instr.*;
import machine.Register;
import program.Program;

/**
 * An ARM7 machine.
 *
 * @author Mikko Reinikainen
 * @author Juha Tukkinen
 */

public class ARMMachine extends Machine {
    /** number of registers */
    public static final double MAINMEMENERGY = 16.0;
    public static final int NUMREGS = 32;

    /** all ARM regs are of same size, use this */
    public static final int REGSIZE = 32;

    /** size of memory in words */
    public static final int MEMSIZE = 1024;

    /** size of instruction in bytes */
    public static final int INSTRUCTIONSIZE = 4;

    /* user mode registers: */

    /** stack limit (R10) */
    public static final int SL = 10;
    /** frame pointer (R11) */
    public static final int FP = 11;
    /** scratch register (R12) */
    public static final int IP = 12;
    /** stack pointer (R13) */
    public static final int SP = 13;
    /** link register (R14) */
    public static final int LR = 14;
    /** program counter (R15) */
    public static final int PC = 15;
    /** current program status register  */
    public static final int CPSR = 16;

    /**
     * Constructs a new ARM machine
     *
     * @return the new ARM machine
     */
    public ARMMachine(String arch) {
        
        super(arch);
        ArrayList a = new ArrayList();

        a.add(new ARMRegister("r0", REGSIZE, 0, 0, this));
        a.add(new ARMRegister("r1", REGSIZE, 1, 0, this));
        a.add(new ARMRegister("r2", REGSIZE, 2, 0, this));
        a.add(new ARMRegister("r3", REGSIZE, 3, 0, this));
        a.add(new ARMRegister("r4", REGSIZE, 4, 0, this));
        a.add(new ARMRegister("r5", REGSIZE, 5, 0, this));
        a.add(new ARMRegister("r6", REGSIZE, 6, 0, this));
        a.add(new ARMRegister("r7", REGSIZE, 7, 0, this));
        a.add(new ARMRegister("r8", REGSIZE, 8, 0, this));
        a.add(new ARMRegister("r9", REGSIZE, 9, 0, this));
        
        a.add(new ARMRegister("sl", REGSIZE, 10, 0, this));
        a.add(new ARMRegister("fp", REGSIZE, 11, 0, this));
        
        a.add(new ARMRegister("ip", REGSIZE, 12, 0, this));
        a.add(new ARMRegister("sp", REGSIZE, 13, 0, this));
        a.add(new ARMRegister("lr", REGSIZE, 14, 0, this));
        a.add(new ARMRegister("pc", REGSIZE, 15, 0, this));
        a.add(new ARMRegister("cpsr", REGSIZE, 16, 0, this));
        a.add(new ARMRegister("r17", REGSIZE, 17, 0, this));
        a.add(new ARMRegister("r18", REGSIZE, 18, 0, this));
        a.add(new ARMRegister("r19", REGSIZE, 19, 0, this));
        
        a.add(new ARMRegister("r20", REGSIZE, 20, 0, this));
        a.add(new ARMRegister("r21", REGSIZE, 21, 0, this));
        a.add(new ARMRegister("r22", REGSIZE, 22, 0, this));
        a.add(new ARMRegister("r23", REGSIZE, 23, 0, this));

        a.add(new ARMRegister("r24", REGSIZE, 24, 0, this));
        a.add(new ARMRegister("r25", REGSIZE, 25, 0, this));
        a.add(new ARMRegister("r26", REGSIZE, 26, 0, this));
        a.add(new ARMRegister("r27", REGSIZE, 27, 0, this));

        a.add(new ARMRegister("r28", REGSIZE, 28, 0, this));
        a.add(new ARMRegister("r29", REGSIZE, 29, 0, this));
        a.add(new ARMRegister("r30", REGSIZE, 30, 0, this));
        a.add(new ARMRegister("r31", REGSIZE, 31, 0, this));


        /*
          a.add("r0");        a.add("r1");        a.add("r2");        a.add("r3");
          a.add("r4");        a.add("r5");        a.add("r6");        a.add("r7");
          a.add("r8");        a.add("r9");        a.add("sl");        a.add("fp");
          a.add("ip");        a.add("sp");        a.add("lr");        a.add("pc");
          a.add("cpsr");        a.add("r17");        a.add("r18");        a.add("r19");
          a.add("r20");        a.add("r21");        a.add("r22");        a.add("r23");
          a.add("r24");        a.add("r25");        a.add("r26");        a.add("r27");
          a.add("r28");        a.add("r29");        a.add("r30");        a.add("r31");
        */

        super.setRegisters(a);


    }
        
    
    public int getPCReg(){
        return PC;
    }
    

    public long getPCAddr(){
        Register  pc  = super.getRegister("pc");
        return pc.getValue();
    }
    
    public void setPCAddr(long value){
  
        Register  pc  = super.getRegister("pc");
        pc.setValue(value);
    }
  


    public long getStackAddr(){
        return 0;
    }

    public void setStackAddr(long value){
        
    }


    
    public void setCarry(Register register){
        return;
    }
 
    public long getCarry(){
        return 0;
    }



    public void setOverflow(Register register, boolean overflowPositive){
        return;
    }

    public long getOverflow(Register register){
        return 0;
    }


    public void initMemory(){
        return;
    }

     public int getInstructionBankNum(){
        return 0;

    }
    public int getDataBankNum(){
        return 1;
    }

    public double getMainMemoryEnergy(){
        return MAINMEMENERGY;
    }

    public void reset(Program program){
        ;
    }

    public boolean isPartialExecutionMode(){
        return partialExecutionMode;
    }

    public void setPartialExecutionMode(boolean mode){
        partialExecutionMode = mode;
    }

    public void setLoopCounter(Register counter, long endAddress){
        ;
    }

    


    public Instruction makeLoad(ArrayList registers){
        Instruction instr = null;
        // instr = new ARMInstruction( ...
        return instr;
    }

    public Instruction makeMove(ArrayList registers){
        Instruction instr = null;
        // instr = new ARMInstruction( ...
        return instr;
    }




    /**
     * Tells whether the given register has a special function.
     *
     * @param reg The register
     * @return True if the register <code>reg</code> has a special function.
     */
    public boolean isSpecialRegister(Register reg) {
        
        if (reg == null) return false;
        int number = reg.getPos();
        
        if (number < 10) {
            return false;
        } else if (number > 16) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @return The number of registers in this machine
     */
    public int getRegisterCount() {
        return ARMMachine.NUMREGS;
    }

}
