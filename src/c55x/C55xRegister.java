
package c55x;

import machine.AbstractRegister;
import machine.Machine; 
import microinstr.BitUtils;

public class C55xRegister extends AbstractRegister{
    
    public boolean isAUnit = false;
    public boolean isDUnit = false;
    public boolean isACx   = false;
    

    public C55xRegister(String regName, int bitSize, long value, int unit, C55xMachine machine) {
        super(regName, bitSize, 0, value, machine);
        setACx();
        setUnit(unit);
    }


    public C55xRegister(String regName, int bitSize, int unit, C55xMachine machine, boolean ac) {
        super(regName, bitSize, 0, 0, machine);
        setUnit(unit);

    }
    
    public C55xRegister(String regName, int bitSize, int unit, C55xMachine machine) {
        super(regName, bitSize, 0, 0, machine);
        setUnit(unit);

    }
    
    public C55xRegister(String regName, int bitSize, C55xMachine machine) {
        super(regName, bitSize, 0, 0, machine);
        
    }
    


    private void setUnit(int unit){
        if (unit == C55xMachine.AUNIT) isAUnit = true;
        else if (unit == C55xMachine.DUNIT) isDUnit = true;
        
    }
    
    private void setACx(){
        isACx = true;
    }
    
    public boolean isACx(){
        return this.isACx;
    }


    public long getSign(){


        // handle the 40 bit accumulator signs
        if ((((C55xMachine)machine).getM40() == 0) && (isACx)) // spru371f 2-49
            return BitUtils.testBit(this.getValue(), 31);
        if ((((C55xMachine)machine).getM40() == 1) && (isACx)) // spru371f 2-49
            return BitUtils.testBit(this.getValue(), 39);
        
        else return BitUtils.testBit(this.getValue(),getBitSize()-1);

    }
    

    public long getAliasAddr(){
        return 0;
    }

    public int getBitNum(){
        return 0;
    }
    
    public void writeBit(long value, int bit){
        ;
    }
    
    public boolean isAUnit(){
        return this.isAUnit;
    }
    
    public boolean isDUnit(){
        return this.isDUnit;
    }

    public C55xRegister getRealRegister(){
        return (C55xRegister)machine.getRegister(super.getName());
    }
    
    public C55xRegister getACxPair() {
        if (this.getName().equals("ac0"))
            return (C55xRegister)machine.getRegister("ac1");
        if (this.getName().equals("ac2"))
            return (C55xRegister)machine.getRegister("ac3");
        throw new NullPointerException("pair: illegal reg " + this.getName());
    }        

}
