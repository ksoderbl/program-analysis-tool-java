package microinstr;

public class BitUtils{

    public static final long bitMask[] = {0L, 1L, 0x3L, 0x7L, 0xfL, 
                                          0x1fL, 0x3fL, 0x7fL, 0xffL, 
                                          0x1ffL, 0x3ffL, 0x7ffL, 0xfffL, 
                                          0x1fffL, 0x3fffL, 0x7fffL, 0xffffL,
                                          0x1ffffL, 0x3ffffL, 0x7ffffL, 0xfffffL,
                                          0x1fffffL, 0x3fffffL, 0x7fffffL, 0xffffffL,
                                          0x1ffffffL, 0x3ffffffL, 0x7ffffffL, 0xfffffffL,
                                          0x1fffffffL, 0x3fffffffL, 0x7fffffffL, 0xffffffffL,
                                          0x1ffffffffL, 0x3ffffffffL, 0x7ffffffffL, 0xfffffffffL,
                                          0x1fffffffffL, 0x3fffffffffL, 0x7fffffffffL, 0xffffffffffL,
                                          0x1ffffffffffL, 0x3ffffffffffL,0x7ffffffffffL, 0xfffffffffffL,
                                          0x1fffffffffffL, 0x3fffffffffffL,0x7fffffffffffL, 0xffffffffffffL,
                                          0x1ffffffffffffL, 0x3ffffffffffffL, 0x7ffffffffffffL, 0xfffffffffffffL,
                                          0x1fffffffffffffL, 0x3fffffffffffffL, 0x7fffffffffffffL, 0xffffffffffffffL,
                                          0x1ffffffffffffffL, 0x3ffffffffffffffL, 0x7ffffffffffffffL, 0xfffffffffffffffL,
                                          0x1fffffffffffffffL, 0x3fffffffffffffffL, 0x7fffffffffffffffL, 0xffffffffffffffffL};
    
    public static long maskBits(long value, int bits){
        return value & bitMask[bits];
    }

   /* writes data into existing data with size given as size, without destroying other source data
    * and shift is the value in bits where the data is written
    * eg. writeData(size=8, shift=32) writes into the 5th byte 8 bits of data
    */
   public static long writeData(long source, long dest, int size, int shift){
        // first cut off all unnecessary data from source, should be right from beginning
        source = source & bitMask[size];
        // shift into correct position
        source = source << shift;
        //System.out.println(Long.toHexString(Long.rotateLeft(~bitMask[size],shift)));
        dest = dest & (Long.rotateLeft(~bitMask[size], shift)); 
        return source | dest;
   }

   /* reads out a data portion from a long, specified by position shift, and size size */
   public static long readData(long source, int size, int shift){
        source = source >> shift;
        return source & (bitMask[size]);
   }


    public static long setBit(long value, int bit){
        long bitValue = 1;
        bitValue = bitValue << bit;
        return value | bitValue;
    }

    public static long writeBit(long value, long bit, long bitValue){
        if (bitValue == 0){
          bitValue = 1;
          bitValue = bitValue << bit;
          bitValue = ~bitValue;
          return value & bitValue;
        }
        else{
          bitValue = 1;
          bitValue = bitValue << bit;
          return value | bitValue;
        }
    }

    public static long clearBit(long value, int bit){
        long bitValue = 1;
        bitValue = bitValue << bit;
        bitValue = ~bitValue;
        return value & bitValue;
    }
    
    public static long changeBit(long value, int bit){
        long bitValue = 1;
        bitValue = bitValue << bit;
        return value ^ bitValue;
    }

    public static long testBit(long value, long bit){
        long tmpValue = value >> bit;
        tmpValue = tmpValue & 0x1;
        return tmpValue;
    }


    /** 
     *        This method assumes you want to extend a signed number, and if the value is not signed
     *  performs same an unsigned extension 
     *  nothing is done 
     *  @param value the value to be signextended
     *  @param valueBitSize the bitsize of the value
     *  @param extBitSize the bitsize of the extended size
     *  @return the new sign extended value of size extBitSize
     */
    public static long extendSigned(long value, int valueBitSize, int extBitSize){
       
        long extMask = 0L;
        long maskedValue = maskBits(value, valueBitSize);
        if (testBit(maskedValue, valueBitSize - 1) == 1){
            extMask = bitMask[extBitSize];
            extMask = extMask & ~bitMask[valueBitSize];
            //     System.out.println("mask:"+Long.toBinaryString(extMask)+ " "+Long.toHexString(extMask));
            return value | extMask;
        }

        else{
            long msbMask = 0L;
            long lsbMask = 0L;
            long resultMask = 0L;
            msbMask = ~bitMask[extBitSize];
            //System.out.println("mask:"+Long.toBinaryString(msbMask));
            lsbMask = bitMask[valueBitSize];
            resultMask = msbMask | lsbMask;
            
            //System.out.println("mask2:"+Long.toBinaryString(resultMask));
            return value & resultMask;
        }
        
   
    }


    /* takes a long value specified in value  and converts it to a signed long
     * based on the bit size of the value given in valueBitSize
     */
    public static long signedToLong(long value, int valueBitSize){
        
        long extMask = 0L;
        long maskedValue = maskBits(value, valueBitSize);
        if (testBit(maskedValue, valueBitSize - 1) == 1){
            extMask = bitMask[64];
            extMask = extMask & ~bitMask[valueBitSize];
            //     System.out.println("mask:"+Long.toBinaryString(extMask)+ " "+Long.toHexString(extMask));
            return value | extMask;
        }   
        
        else return value;
        
    }
    

    /** this method assumes you want to extend an unsigned number, signs are ignored
     */
    public static long extendUnsigned(long value, int valueBitSize, int extBitSize){
        
        long msbMask = 0L;
        long lsbMask = 0L;
        long resultMask = 0L;
        msbMask = ~bitMask[extBitSize];
        //System.out.println("mask:"+Long.toBinaryString(msbMask));
        lsbMask = bitMask[valueBitSize];
        resultMask = msbMask | lsbMask;
        
        //System.out.println("mask2:"+Long.toBinaryString(resultMask));
        return value & resultMask;
        
        
        
    }
    

    public static boolean overflow(long value, int bitSize){
        long val;
        val = value & (~bitMask[bitSize]);
        if (val!= 0) return true;
        else return false;
    }
    
    public static long logicalShift(){
        return 0;
    }

    public static long arithmeticShift(){
        return 0;


    }
    
   
    public static long getSignedValue(long value, int opSize){
        value = value & bitMask[opSize];
        System.out.println(Long.toBinaryString(value));
        long result = testBit(value, opSize-1);
        if (result == 1){
            //            value = clearBit(value, opSize-1);
            System.out.println(Long.toBinaryString(~value));
            return ~value;
        }
        else return value;
        
    }

    // not sure if this belongs here --kps
    public static String q15ToString(long value) {
        String q15str = "";
        long tmp = extendSigned(value, 16, 64);
        double dbl = 0.0;
        double x = 0.5;
        if (tmp < 0) {
            q15str = "-";
            tmp = -tmp;
        }
        for (int i = 14; i >= 0; i--) {
            if (BitUtils.testBit(tmp, i) == 1)
                dbl += x;
            x /= 2;
        }
        q15str += "" + String.format("%.6f", dbl);
        return q15str;
    }

    public static String q31ToString(long value) {
        String q31str = "";
        long tmp = extendSigned(value, 32, 64);
        double dbl = 0.0;
        double x = 0.5;
        if (tmp < 0) {
            q31str = "-";
            tmp = -tmp;
        }
        for (int i = 30; i >= 0; i--) {
            if (BitUtils.testBit(tmp, i) == 1)
                dbl += x;
            x /= 2;
        }
        q31str += "" + String.format("%.6f", dbl);
        return q31str;
    }

    public static String valueToString(long value) {
        String s;
        s = "0x"+Long.toHexString(value)
            + "(" + value
            + " q15:" + q15ToString(value /*extendSigned(value, 16, 64)*/)
            //+ " q31:" + q31ToString(extendSigned(value, 32, 64))
            +")";

        return s;
    }

    public static String valueToString2(long value) {
        String s;
        s = "0x"+Long.toHexString(value)
            + "(" + value +")";

        return s;
    }
}
