/**
 * Register.java
 */

package machine;

import java.util.List;
import microinstr.BitUtils;

/**
 * A generic register for the registers
 * used in CPU simulator
 * 
 * @author Peter Majorin
 */


public abstract class AbstractRegister implements Register{

    private String registerName;
    protected long value;

    /** the register bit size */
    private int bitSize;

    /** the register position in the register file */
    private int pos;

    /** the machine this register belongs to (an optimization) */
    protected Machine machine;

    
    /**
     */
    public AbstractRegister(String regName, int bitSize, int pos, long value, Machine machine) {
        this.registerName = regName;
        this.bitSize = bitSize;
        this.pos = pos;
        this.value = value;
        this.machine = machine;
    }

    /*
    public AbstractRegister(String regName, int bitSize, Machine machine) {
        this.registerName = regName;
        this.value = 0;
        this.pos = -1;
        this.bitSize = bitSize;
        this.machine = machine;
        }*/
    
    public long getValue(){
        return value;
    }

    // position of register in register file, if used by arch
    public int getPos(){
        return pos;
    }

    // setValue will not accept values which violate the size of register
    public void setValue(long value){
//        long value2 = value & (~BitUtils.bitMask[bitSize]);
        // System.out.println(Long.toHexString(value2));
//        if (value2 !=0) throw new IllegalArgumentException("register: "+this.registerName+
//                                                           " attempted value:"+
//                                                           Long.toHexString(value)+" bitsize:"+bitSize);
        this.value = value;
    }
    
    public void setBit(int bit){
        this.value = BitUtils.setBit(this.value, bit);
        
    }
    

    public void writeBit(long bit, long bitNum){
        BitUtils.writeBit(this.value, bit, bitNum);
    }

    public void clearBit(int bit){
        this.value = BitUtils.clearBit(this.value, bit);
       
    }

    public long testBit(int bit){
        return BitUtils.testBit(this.value, bit);
    }

    
    public int getBitSize(){
        return this.bitSize;
    }

    /** returns the sign bit of a register value */
    public abstract long getSign();

    /** clears this register */
    public void clear(){
        value = 0L;
    }
    
    public String getName(){
        return registerName;
    } 
    
    public int getBitNum(){
        return 0;
    }

    public Machine getMachine(){
        return machine;
    }

    public String toString(){
        return(registerName);
    }

}
