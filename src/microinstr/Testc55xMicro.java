
package microinstr;


import machine.*;
import c55x.C55xMachine;
import c55x.C55xRegister;
import microinstr.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigInteger;
import program.Program;
import main.UserOptions;


/** 
 *  usage e.g. java microinstr.Testc55xMicro c55x/test/fir.dis, the filename is irrelevant, just
 *  a valid c55 dis file will do 
 */

public class Testc55xMicro{

    public static final boolean DUNIT = false;
    public static final boolean RESET = false;
    public static final boolean BASICTEST = false;
    public static final boolean ARITHMETIC1 = false;
    public static final boolean ARITHMETIC2 = false;
    public static final boolean REGISTER_BITSIZE = false;
    public static final boolean REGISTER_BIT_OPERATIONS = false;
    public static final boolean CARRY = false;
    public static final boolean OVERFLOW = false;
    public static final boolean SWAP = false;
    public static final boolean unsigned = false;
    public static final boolean overflow = false;
    public static final boolean SIGNED_EXT = false;
    public static final boolean SUBTRACT = false;
    public static final boolean SUBTRACT2 = false;    
    public static final boolean INTERMEDIATE = false;
    public static final boolean pair = true;

    public static void main (String[] args){

        C55xMachine c55xMachine = new C55xMachine("c55x");
        UserOptions options = new UserOptions(args);
        Program program = new Program(options, c55xMachine);
        List<Microinstruction> micros = new ArrayList<Microinstruction>();


        
        if (BASICTEST){
            C55xMicroRegisterOperand  reg0  = new 
                C55xMicroRegisterOperand(c55xMachine.getRegister("ac0"));
            
            C55xMicroRegisterOperand  reg1  = 
                new C55xMicroRegisterOperand(c55xMachine.getRegister("ac0"));
            C55xMicroRegisterOperand  ac3   = 
                new C55xMicroRegisterOperand(c55xMachine.getRegister("ac3"));
            C55xMicroRegisterOperand  t0    = 
                new C55xMicroRegisterOperand(c55xMachine.getRegister("t0"));
            C55xMicroRegisterOperand  ac0   = 
                new C55xMicroRegisterOperand(c55xMachine.getRegister("ac0"));
            C55xMicroRegisterOperand  ac1   = 
                new C55xMicroRegisterOperand(c55xMachine.getRegister("ac1"));

            

            MicroOperand constant  = new Constant(0xffff232L,32);
            MicroOperand constant2 = new Constant(4L, 32);
            
//            MicroOperand busResult = new BusResult(c55xMachine);

            
            Microinstruction writereg  = new WriteReg(constant, reg0); 
            Microinstruction writereg2 = new WriteReg(reg0, reg1);
            Microinstruction writereg3 = new WriteReg(constant2, t0);
            Microinstruction writeMem = new WriteMem(constant,ac3);
            writereg.execute(program, c55xMachine);
            writereg2.execute(program, c55xMachine);
            writereg3.execute(program, c55xMachine);
            writeMem.execute(program, c55xMachine);
            
            
//            c55xMachine.getRegisters().printRegisters();
            
//            Microinstruction readMem = new ReadMem(ac3);
//            Microinstruction shiftL = new Shift(t0, busResult , "AL");
//            Microinstruction writeMem2 = new WriteMem(dataResult,ac0);
        
//            micros.add(readMem);
//            micros.add(shiftL);
//            micros.add(writeMem2);
//            executeMicroinstructions(micros, program, c55xMachine);
        }
        /*
        if (READMEM_WRITEMEM){
           
            MicroOperand constant  = new Constant(0xffff232L,32);
            MicroOperand dest  = new Constant(0xffL,32);
            Microinstruction writeReg = new WriteReg( 
            Microinstruction readMem = new ReadMem(ac0);
            Microinstruction writeMem = new WriteMem(ac0,dest);
            micros.add(constant);
            micros.add(dest);
            micros.add(readMem);
            executeMicroinstructions(micros, c55xMachine);
           
            
            
            }
        */
        if (INTERMEDIATE){
            C55xMicroRegisterOperand  sp  = new C55xMicroRegisterOperand(c55xMachine.getRegister("sp"));
            MicroOperand constant         = new Constant(64,16);
            Microinstruction add          = new MicroArithmetic(constant, sp, "ADD",0);
            Microinstruction writeReg     = new WriteReg(c55xMachine.getDataResult(0), sp);        
            micros.add(add);
            micros.add(writeReg);
            executeMicroinstructions(micros, program, c55xMachine);
            c55xMachine.getRegisters().printRegisters();
        }

        if (ARITHMETIC1){
            long val1 = 5764607523034234880L;
            long val2 = 5764607523034234880L;
            BigInteger val3 = new BigInteger("5764607523034234880");
            BigInteger val4 = new BigInteger("5764607523034234880");
            BigInteger val5 = val3.add(val4);
            System.out.println(val5.toString());
            
            System.out.println(Long.toHexString(val1)+" "+val1);
            System.out.println(Long.toHexString(val2)+" "+val2);
            System.out.println(Long.toHexString(val1+val2)+ " "+ (val1+val2));
            System.out.println(Long.toHexString(val1-val2)+ " "+ (val1-val2));
        }
        
        if (ARITHMETIC2){
            
            Register ac2 = c55xMachine.getRegister("ac2");
            ac2.setValue(0xffaf);
            C55xMicroRegisterOperand  ac2Full  = new C55xMicroRegisterOperand(ac2);
            C55xMicroRegisterOperand  ac2Size  = new C55xMicroRegisterOperand(ac2,8);
            //            MicroOperand dataResult = new DataResult(c55xMachine, 32);
            //Microinstruction mi = new MicroArithmetic(ac2Size,ac2Full, "ADD");
            //Microinstruction mi2 = new WriteReg(c55xMachine.getDataResult(), ac2Full);
            //micros.add(mi);
            // micros.add(mi2);
            executeMicroinstructions(micros, program, c55xMachine);
            c55xMachine.getRegisters().printRegisters();
        }
        

        if (DUNIT){
            C55xRegister ac0  = (C55xRegister)c55xMachine.getRegister("ac0");
            C55xRegister t0   = (C55xRegister)c55xMachine.getRegister("t0");
            if (!ac0.isDUnit())
                System.out.println(ac0.getName()+": Not DUNIT");
            if (t0.isAUnit())
                    System.out.println(t0.getName()+": AUNIT");
        }

        if (pair){
            C55xRegister ac0  = (C55xRegister)c55xMachine.getRegister("ac0");        
            C55xRegister pair = ac0.getACxPair();
            System.out.println(pair.getName());

        }

        
        if (SWAP){
            C55xMicroRegisterOperand  ac0   = 
                new C55xMicroRegisterOperand(c55xMachine.getRegister("ac0"));
            C55xMicroRegisterOperand  ac1   = 
                new C55xMicroRegisterOperand(c55xMachine.getRegister("ac1"));
            
            ac0.setValue(0xffffffffL);
            ac1.setValue(0xff0000ffL);
            Microinstruction mi = new SwapRegs(ac0,ac1);
            micros.add(mi);
            c55xMachine.getRegisters().printRegisters();
            executeMicroinstructions(micros, program, c55xMachine);
            c55xMachine.getRegisters().printRegisters();

            //    if (!c55xMachine.isDUnit(t0))
            // System.out.println(t0.getName()+": Not DUNIT");
            //if (!c55xMachine.isDUnit(ac0))
            //        System.out.println(ac0.getName()+": DUNIT");
        }

        

        if (REGISTER_BITSIZE){
            Register ac2 = c55xMachine.getRegister("ac2");
            ac2.setValue(0xffaf);
            C55xMicroRegisterOperand  ac2Full  = new C55xMicroRegisterOperand(ac2);
            C55xMicroRegisterOperand  ac2Size  = new C55xMicroRegisterOperand(ac2, 8);
            System.out.println("Register value:"+Long.toHexString(ac2.getValue()));
            System.out.println(Long.toHexString(ac2Full.getValue())+" "+ 
                               Long.toHexString(ac2Size.getValue()));
            
        }
        

        if (REGISTER_BIT_OPERATIONS){
            Register ac2 = c55xMachine.getRegister("ac2");
            ac2.setValue(0xf);
            System.out.println("Register value:"+Long.toBinaryString(ac2.getValue()));
            ac2.clearBit(2);
            ac2.setBit(5);
            System.out.println("Register value:"+Long.toBinaryString(ac2.getValue()));
            System.out.println(ac2.testBit(5));

        }


        if (SIGNED_EXT){
            long value = 0x12121263L;
            System.out.println("Register value:"+Long.toHexString(value));
            long result =  BitUtils.extendSigned(value, 8, 16);
            System.out.println("Register value:"+Long.toHexString(result));

        }

        

        if (SUBTRACT){
            
            long value1 = 0x7L;
            long value2 = 0x6L;

            value2 = ~value2;
            value2++;
            System.out.println("value1:"+Long.toHexString(value1));
            System.out.println("value2:"+Long.toHexString(value2));
            System.out.println("result: value1-value2:"+Long.toHexString(value1+value2));
        }

        if (SUBTRACT2){
           long value1 = 0x9000L;
           long value2 = 0xfffdL;
           System.out.println("result:"+Long.toHexString(value1+value2));
        }



        if (RESET){
            c55xMachine.getRegisters().printRegisters();
            c55xMachine.reset(program);
            c55xMachine.getRegisters().printRegisters();
        }

        c55xMachine.printChangesMemory();



        



    }

    



    public static void executeMicroinstructions(List<Microinstruction> micros, Program program, Machine c55xMachine){
        for (int i = 0; i < micros.size(); i++){
            Microinstruction mi = micros.get(i);
            System.out.println(mi.getName());
            //            if (i > 0) System.out.println("joo:"+c55xMachine.getDataResult(0).getValue());
            mi.execute(program, c55xMachine);
        }
    }
    
    
}
