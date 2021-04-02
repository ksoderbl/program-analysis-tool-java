package machine;

import machine.Machine;

public interface Register{

    /** get name of register object */
    public String getName();

    /** when asked from registerAlias, returns the realname of this register; when asked from a register, returns
     * same as getName
     */
    public Register getRealRegister();

    /** get register position, if arch supports that */
    public int getPos();
    /** get value of register object */
    public long getValue();        
    
    /** set value of register object */
    public void setValue(long value);
    
    /** clear register object */
    public void clear();
    
    /** get the machine this machine belongs to */
    public Machine getMachine();
 
    /** get the bit number for the register name, if it is also embedded in the alias, eg st3_sata */
    public int getBitNum();
    
    public void setBit(int bit);
    public long testBit(int bit);
    public void clearBit(int bit);
    public void writeBit(long value, int bit);
    public int getBitSize();
    public long getSign();
}
