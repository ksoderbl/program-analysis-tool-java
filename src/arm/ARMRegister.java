
package arm;

import machine.AbstractRegister;
import machine.Machine;
import machine.Register;

public class ARMRegister extends AbstractRegister {
    

    public ARMRegister(String regName, int bitSize, int pos, long value, Machine machine) {
        super(regName, bitSize, pos, value, machine);
     
    }
    
    //public ARMRegister(String regName, int bitSize, Machine machine) {
    //        super(regName, bitSize, machine);
    //}
    
    public void setValue(long value){
        // kps wtf? well you must implement the Register interface with this class -pgm
   ; 
   }
    
    public Register getRealRegister(){
        return machine.getRegister(super.getName());
        
    }

     public long getValue(){
        return 0;   
     }

    public void writeBit(long value, int bit){
        ;
    }
     
    public long getSign(){
        return 0;
    }

    public long getAliasAddr(){
        return 0;
    }

}
