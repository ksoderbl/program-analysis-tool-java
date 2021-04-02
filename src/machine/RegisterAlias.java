/**
 * RegisterAlias.java
 */

package machine;

import java.util.List;
import microinstr.BitUtils;
import machine.Register;

/**
 * A generic register alias for the registers
 * used in CPU simulator
 * 
 * @author Peter Majorin
 */


public class RegisterAlias implements Register{

    /** alias name for register */
    private String aliasRegName;

    /** the register this registeralias accesses */
    private Register realReg;

    /** the register bit size */
    private int bitSize = 0;

    /** a possible address alias */
    private Long aliasAddr;

    /** possible bitnumber embedded in the name */
    private int bitNum;

    private int shift;
    

    public RegisterAlias(String regName, Register realReg, int bitSize, int shift) {
        this.aliasRegName = regName;
        this.realReg = realReg;
        this.bitSize = bitSize;
        this.shift = shift;
    }
  
    public RegisterAlias(String regName, int bitNum, Register realReg) {
        this.aliasRegName = regName;
        this.realReg = realReg;
        this.bitNum = bitNum;
    }
    
    public long getValue(){
        return BitUtils.readData(realReg.getValue(), bitSize, shift);
//        return aliasReg.getValue();
    }


    public void setValue(long value){
        long result = BitUtils.writeData(value, realReg.getValue(), bitSize, shift);
        //System.out.println("orig value: "+Long.toHexString(this.getValue())+" result:"+ Long.toHexString(result)+" value:"+  Long.toHexString(value)+ " bitSize: "+bitSize+" "+shift);
        realReg.setValue(result);
    }
    
    public void setBit(int bit){
        BitUtils.setBit(realReg.getValue(), bit);
        
    }
    
    public void writeBit(long bit, long bitNum){
        BitUtils.writeBit(realReg.getValue(), bit, bitNum);
    }

    public void clearBit(int bit){
        BitUtils.clearBit(realReg.getValue(), bit);
       
    }

    public long testBit(int bit){
        return BitUtils.testBit(realReg.getValue(), bit);
    }

    
    public int getBitSize(){
        if (bitSize != 0) return this.bitSize;
        else return realReg.getBitSize();
    }

    public void writeBit(long value, int bit){
        ;
    }

    /** returns the sign bit of a register value */

    public long getSign(){
        return 0;
    }

    /** clears this register */
    public void clear(){
        ;
    }
    
    public String getName(){
        return this.aliasRegName;
    } 


    public Register getRealRegister(){
        return realReg;
    }

    // position in register file
    public int getPos() {
        return this.realReg.getPos();
    }
    
    public int getBitNum(){
        return bitNum;
    }
    

    public Machine getMachine(){
        return realReg.getMachine();
    }
    

    public String toString(){
        return "";
    }

}
