
/**
 * MicroRegisterOperand.java
 */

package microinstr;

import java.util.List;
import microinstr.MicroOperand;
import machine.Register;

/**
 * MicroRegisterOperands are Operands and they implement the MicroOperand interface
 * Registers in themselves cannot contain operand-specific data, such
 * as bitsize, or if the register value is to be treated as a signed value etc.
 * 
 * @author Peter Majorin
 */


public abstract class MicroRegisterOperand implements MicroOperand{

    protected Register register;

    /** the operand bitSize (not in general same as registerBitSize) */
    protected int bitSize;

    /** is this operand signed */
    protected boolean isSigned;
    
    /**
     * Constructs a new MicroRegisterOperand, use this constructor
     * when bitSize is statically determined in instructions, i.e.
     * doesn't change at run-time.
     *
     * @param register the register 
     * @param bitSize the bit size of register
     * @return a new MicroRegisterOperand
     */

    public MicroRegisterOperand(Register register, int bitSize) {
        this.register = register;
        this.bitSize = bitSize;
    }

    /**
     * Constructs a new MicroRegisterOperand, use this constructor
     * when bitsize changes dynamically, use setValue() to change
     * during runtime.
     *
     * @param register the register, the operand size is assumed
     * to be that of the whole registersize
     * @return a new MicroRegisterOperand
     */

    public MicroRegisterOperand(Register register) {
        this.register = register;
        this.bitSize = register.getBitSize();
        
    }

    public String getName(){
        return register.getName();
    }

    public String toString(){
        return "mRegOp:0x"+Long.toHexString(getValue())+"("+getValue()+")";
    }

    public abstract long getValue();
        
    public long getSign(){
        return register.getSign();
    }

    public Register getRegister(){
        return register;
    }


    /** the operand bitsize */
    public int getBitSize(){
        return this.bitSize;
    }
    

    public boolean isSigned(){
        return this.isSigned;
    }


    public void setBit(int bit){
        register.setValue(BitUtils.setBit(register.getValue(), bit));
        
    }
    

    public void writeBit(long bit, long bitNum){
        BitUtils.writeBit(register.getValue(), bit, bitNum);
    }

    public void clearBit(int bit){
        register.setValue(BitUtils.clearBit(register.getValue(), bit));
       
    }

    public long testBit(int bit){
        return BitUtils.testBit(register.getValue(), bit);
    }
    
    public int getBitNum(){
        return register.getBitNum();
    }





}
