
package microinstr;

import machine.Register;
import microinstr.BitUtils;

public class Constant implements MicroOperand{
    long value;
    int bitSize;
    boolean signed = false;


    public Constant(long value, int bitSize){
        this.value = value; //& BitUtils.bitMask[bitSize];
        this.bitSize = bitSize;
    }
    
    public String getName() {
        return "Constant:"+BitUtils.valueToString(value);
    }

    public String toString(){
        return getName();
    }
    
    public long getValue(){
        return value;
    }

    public void setValue(long value){
        this.value = value;
    }
                                    
    
    public int getBitSize(){
        return bitSize;
    }

    public void setBitSize(int value){
        bitSize = value;
    }
    
    public long getSign(){
        return BitUtils.testBit(value, getBitSize()-1);
    
    }
    
    public Register getRegister(){
        return null;
    }


    
    public boolean isSigned(){
        return this.signed;
    }
    
    public void setSigned(){
        this.signed = true;
    }

    
    public void setUnsigned(){
        this.signed = false;
    }


    public void setBit(int bit){
        value = (BitUtils.setBit(value, bit));
    }
    

    public void writeBit(long bit, long bitNum){
        BitUtils.writeBit(value, bit, bitNum);
    }
    
    public void clearBit(int bit){
        value = BitUtils.clearBit(value, bit);
        
    }
    
    public long testBit(int bit){
        return BitUtils.testBit(value, bit);
    }

     public int getBitNum(){
        return -1;
    }

}
