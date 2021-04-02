
/**
 * Operand.java
 */

package microinstr;

import machine.Register;


/**
 * A microinstruction operand is a register or a constant.
 * The constant could be an immediate value, or a label + offset,
 * but implementors of the Operand interface needs to calculate this
 *
 * @author Peter Majorin
 */


public interface MicroOperand{

    /** the name of the operand */
    public String getName();

    /** the value of the operand (appropriate bit masking must be done by implementors of this
     *  interface if the actual value is bigger than its bitSize */
    public long getValue();


    /** set the value of this operand (for operands that write back to other operands or memory) */
    public void setValue(long value);

    /** the size of the operand, which is usually not the same as register size */
    public int getBitSize();

    /** is the operand to be treated as signed? (some CPUs perform sign-related preprocessing
        on operands before doing any operations */
    public boolean isSigned();

    /** return the sign of the operand */
    public long getSign();

    /** return the register or else null */
    public Register getRegister();

    /** get Bitnum */
    public int getBitNum();

    
    public void setBit(int bit);
    public void writeBit(long bit, long bitNum);
    public void clearBit(int bit);
    public long testBit(int bit);



}
