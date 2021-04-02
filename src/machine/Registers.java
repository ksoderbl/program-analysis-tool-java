/**
 * Registers.java
 */

package machine;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Registers of a machine.
 *
 * @author Mikko Reinikainen
 * @see Machine
 */

public class Registers {

    private Hashtable<String, Register> registers = new Hashtable<String, Register>(); 

    /** the registers */
    public Register[] r;

    /**
     * Constructs the registers
     *
     * @return the registers
     */
    public Registers(ArrayList<Register> regs) {
        int numRegs = regs.size();

        r = new Register[numRegs];
        for (int i = 0; i < numRegs; i++) {
            Register reg = regs.get(i);
            r[i] = reg;
            registers.put(reg.getName(), reg);
        }
    }
    
    /**
     * @return registers
     *
     */
    public Register[] getRegisters(){
        return r;

    }

    public Register getRegister(String name){
        if (registers.get(name) != null)
            return registers.get(name);
        else throw new NullPointerException("getRegister:no register found:"+name);
    }
    

    public void printRegisters(){
        int numRegs = registers.size();
        System.out.println("Registers:");
        for (int i = 0; i < numRegs; i++) {
            Register reg = getRegister(r[i].getName());
            if (reg instanceof AbstractRegister)
            System.out.print(reg.getName()+"="+
                               "0x"+Long.toHexString(reg.getValue())+" ");
        }
        System.out.println();
        System.out.println("Register Aliases");
        for (int i = 0; i < numRegs; i++) {
            Register reg = getRegister(r[i].getName());
            if (reg instanceof RegisterAlias)
            System.out.print(reg.getName()+"="+
                               "0x"+Long.toHexString(reg.getValue())+" ");
        }
        System.out.println();
    }
    
    public void printRegistersBinary(){
        int numRegs = registers.size();
        for (int i = 0; i < numRegs; i++) {
            Register reg = getRegister(r[i].getName());
            System.out.println(reg.getName()+"="+
                               "0%"+Long.toBinaryString(reg.getValue()));
        }
    }
    

    
    public void clearRegisters(){
        int numRegs = registers.size();
        for (int i = 0; i < numRegs; i++) {
            Register reg = getRegister(r[i].getName());
            reg.clear();
        }
    }
    

}
