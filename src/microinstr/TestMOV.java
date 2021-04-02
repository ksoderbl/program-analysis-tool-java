
package microinstr;

import machine.*;
import c55x.*;
import microinstr.*;
import java.util.ArrayList;
import java.util.List;

public class TestMOV{



    public static final boolean MOV1 = false;
    public static final boolean unsigned = false;
    public static final boolean overflow = false;
    public static final boolean DECODELONG = true;

    // remember to use L for constants to longs or weird things will happen 

    public static void main (String[] args){


        
        long src = 0xfeL;
        long dest = 0L;
        
        Machine c55xMachine = new C55xMachine("c55x");
        List micros = new ArrayList();

        Registers regs  = c55xMachine.getRegisters();
        C55xMicroRegisterOperand  reg0  = new C55xMicroRegisterOperand(regs.getRegister("ac0"), 4);
        C55xMicroRegisterOperand  reg1  = new C55xMicroRegisterOperand(regs.getRegister("ac1"), 4);
        C55xMicroRegisterOperand  ar3  = new C55xMicroRegisterOperand(regs.getRegister("ar3"), 4);
        C55xMicroRegisterOperand  t0  = new C55xMicroRegisterOperand(regs.getRegister("t0"), 4);
        C55xMicroRegisterOperand  ac0  = new C55xMicroRegisterOperand(regs.getRegister("ac0"), 4);
        C55xMicroRegisterOperand  st1  = new C55xMicroRegisterOperand(regs.getRegister("st1"), 4);

        //MicroOperand busResult = new BusResult(c55xMachine);

        
        // src = BitUtils.changeBit(src,0);
        
        if (MOV1){


//            Microinstruction readMem     = new ReadMem(ar3);
        //    Microinstruction condSignExt = new SignExt(busResult, 40, st1, C55xMachine.SXMD);
            //Microinstruction shift       = new Shift(busResult, t0, "AL");
            
            
            
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


        //                     bit  0 1 2
        //        *positive OVERFLOW* 0+0=1 (adding two positives should be positive)
        //      *positive OVERFLOW* 0-1=1 (subtracting a negative is the same as adding a positive)
        //      *negative OVERFLOW* 1+1=0 (adding two negatives should be negative)
        //      *negative OVERFLOW* 1-0=0 (subtracting a positive is the same as adding a negative)


        if (overflow){
            int bitSize = 8;
            long num1 = 0x7fL;
            long num2 = 0xf5L;
            
            long result1 = num1 - num2;
            

            long bit0 = BitUtils.testBit(num1, bitSize-1);
            long bit1 = BitUtils.testBit(num2, bitSize-1);
            long bit2 = BitUtils.testBit(result1, bitSize-1);
            


            System.out.println("Orig number:" + Long.toBinaryString(num1));
            System.out.println("Orig number:" + Long.toBinaryString(num2));
            
            System.out.println("result:"+Long.toBinaryString(result1) + " "+result1);
            System.out.println(bit0+" "+bit1+" "+bit2);
            
        }
        
        if (DECODELONG){
            long dataValue = Long.decode("0x76");
            System.out.println(dataValue+3);
        }

        
    }
    
}
