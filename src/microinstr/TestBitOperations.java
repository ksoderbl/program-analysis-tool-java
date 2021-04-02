
package microinstr;


public class TestBitOperations{

    public static final boolean  signed = false;
        public static final boolean unsigned = false;
        public static final boolean ioverflow = false;
        public static final boolean BINARY_PRINT = false;
        public static final boolean writebit = false;
        public static final boolean compare = false;
        public static final boolean writeData = false;
        public static final boolean readData = true;
    // remember to use L for constants to longs or weird things will happen 

    public static void main (String[] args){


        
        long src = 0xfeL;
        long dest = 0L;
        
        //System.out.println(Long.toBinaryString(0xffffffffffL));
        //System.out.println(Long.toBinaryString(0xffffffffL));
        
        // src = BitUtils.changeBit(src,0);
        
        if (signed){
            System.out.println(Long.toBinaryString(BitUtils.bitMask[33]));
            System.out.println(Long.toBinaryString(src));
            System.out.println(src);
            System.out.println(Long.toBinaryString(BitUtils.extendSigned(src, 8, 40)));
            System.out.println(BitUtils.extendSigned(src, 8, 40));
        }
        
        if (unsigned){
            src = 0xffff00ffffL;
            System.out.println("Orig number:    "+Long.toBinaryString(src));
            System.out.println(src);
            System.out.println("unsign extended:"+Long.toBinaryString(BitUtils.extendUnsigned(src, 8, 24)));
            System.out.println("unsign extended:"+BitUtils.extendUnsigned(src, 8, 24));
        }
        
        if (BINARY_PRINT){
            System.out.println("Orig number:    "+Long.toBinaryString(0xaa));
        }
        if (writebit){
            src = 0xfeL;
            System.out.println(Long.toBinaryString(src));
            long result = BitUtils.writeBit(src, 0, 0);
            
            System.out.println(Long.toBinaryString(result));
        }
        if (compare){
            System.out.println(BitUtils.signedToLong(0xffffL, 16));
        }


        if (writeData){
           long source = 0x3412L;
           long destination = 0xffffffffffffffffL;
           int size = 64;
           int shift = 0;
           System.out.println("writeData:"+Long.toHexString(BitUtils.writeData(source, destination, size, shift)));
        }

        if (readData){
           long source = 0x3412L;
           int size = 16;
           int shift = 0;
           System.out.println("readData:"+Long.toHexString(BitUtils.readData(source, size, shift)));
        }


    }
    
}
