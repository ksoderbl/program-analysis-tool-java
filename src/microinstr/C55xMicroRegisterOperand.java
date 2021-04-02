
/**
 * C55xMicroRegisterOperand.java
 */

package microinstr;

import java.util.List;
import microinstr.MicroOperand;
import c55x.C55xRegister;
import c55x.C55xMachine;
import machine.Register;

/**
 * MicroRegisterOperands are Operands and they implement the MicroOperand interface
 * Registers in themselves cannot contain operand-specific data, such
 * as bitsize, or if the register value is to be treated as a signed value etc.
 * 
 * @author Peter Majorin
 */


public class C55xMicroRegisterOperand extends MicroRegisterOperand{

    
    /**
     * Constructs a new C55xMicroRegisterOperand.
     *
     * @param register the register 
     * @param bitSize the bit size of the register operation
     * @return a new MicroRegisterOperand
     */

    public C55xMicroRegisterOperand(Register c55xregister, int bitSize) {
        super(c55xregister, bitSize);
    }
    
    /**
     * Constructs a new MicroRegisterOperand.
     *
     * @param register the register, the operand size is assumed
     * to be that of the whole registersize
     * @return a new MicroRegisterOperand
     */

    public C55xMicroRegisterOperand(Register c55xregister) {
        super(c55xregister, c55xregister.getBitSize());
        
    }

    /** 
        performs sign extension of input operands, according to c55x status bits,
        the result is not stored in any register
    */
  
    public long getValue(){

        //        Machine c55xMachine = (Machine)register.getMachine();

        //        if (((C55xRegister)register).isDUnit()){
            //  if (c55xMachine.getSXMD() == 1){
            //;
           
                //BitUtils.extendSigned(c55xregister.getValue());
                
            //}
            return register.getValue(); //& BitUtils.bitMask[bitSize];
            //}
            //return 0;
    }
        
    public void setValue(long value){
        //System.out.println(Long.toHexString(value)+" "+bitSize);
        register.setValue(value);
    }
    


    /** the operand bitsize */
    public int getBitSize(){
        return this.bitSize;
    }
    

    public boolean isSigned(){
        return this.isSigned;
    }



}
