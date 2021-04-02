
package machine;

import java.util.TreeMap;
import misc.AddressComparator;
import machine.MemoryBank;
import instr.Instruction;
import java.util.Iterator;
import microinstr.BitUtils;

public class MemoryBank{
    

    public static final int DATA_MEMORY = 0;
    public static final int INSTRUCTION_MEMORY = 1;
    public static final int DATA_INSTRUCTION_MEMORY = 2;
    
    private TreeMap memory;
    private long lowAdd;
    private long hiAdd;
    private long numReads = 0;
    private long numWrites = 0;
    private long instrFetches = 0;
    private int accessEnergy;
    private boolean dataMem = false;
    private boolean instrMem = false;

    
    public MemoryBank(long lowAdd, long hiAdd, int memType){
        this.lowAdd = lowAdd;
        this.hiAdd = hiAdd;
        memory = new TreeMap(new AddressComparator());
        if (memType == DATA_MEMORY) dataMem = true;
        else if (memType == INSTRUCTION_MEMORY) instrMem = true;
        else if (memType == DATA_INSTRUCTION_MEMORY){ 
            dataMem = true; instrMem = true;
        }
        else throw new IllegalArgumentException("Memorybank:"+memType);
    }
    

    public boolean getDataMem(){
        return dataMem;
    }

    public boolean getInstructionMem(){
        return instrMem;
    }
    public int getAccessEnergy(){
        return accessEnergy;
    }

    public long getNumReads(){
        return numReads;
    }

    public long getNumWrites(){
        return numWrites;
    }

    public long getInstrFetches(){
        return instrFetches;
    }

    
    public void addInstruction(Instruction i){
        if (!instrMem) 
            throw new IllegalArgumentException("addInstruction: illegal MemoryBank type");
        memory.put(i.getAddr(), i);
    }


    public Instruction readInstruction(long address){
        Instruction i = (Instruction)memory.get(address);
        if (i == null)
            throw new NullPointerException("MemoryBank.java:readInstruction: no instruction found at:"+
                                           Long.toHexString(address));
        else {
        instrFetches++;
        return i;
        }
    }



    public long readMem(long address){
        Long value;
        Long longAdd = new Long(address);
        value = (Long)memory.get(longAdd);
        checkMemoryBoundaries(address);
        numReads++;
        if (value == null) return 0L;
        else return value.longValue();
    }

    // read memory without accounting reads, for internal use only
    public long readMemDebug(long address){
        Long value;
        Long longAdd = new Long(address);
        value = (Long)memory.get(longAdd);
        checkMemoryBoundaries(address);
        if (value == null) return 0L;
        else return value.longValue();
    }
    



    public void writeMem(long address, long value){
        Long longAdd  = new Long(address);
        Long longValue = new Long(value);
        checkMemoryBoundaries(longAdd);
        numWrites++;
        memory.put(longAdd, longValue);

    }

    /**
     * Prints the changes that has been made to memory
     */
    public void printChangesMemory(int bankNum, Machine machine){
        Iterator i = memory.keySet().iterator();
        System.out.println("--- Bank: "+bankNum+" ---");
        if (dataMem){ 
            while (i.hasNext()){
                String stack = "";
                Long add = (Long)i.next();
                Long value = (Long)memory.get(add);
                if (add.longValue() == machine.getRegister("sp").getValue())
                    stack = " <--stack here";
                String addstr = Long.toHexString(add.longValue());
                while (addstr.length() < 4) addstr = "0" + addstr;
                String hexstr = Long.toHexString(value.longValue());
                while (hexstr.length() < 4) hexstr = "0" + hexstr;
                long longValue = value.longValue();
                longValue = BitUtils.extendSigned(value.longValue(),16,64);
                // get q.15 string, should be moved elsewhere
                String q15str = BitUtils.q15ToString(longValue);
                q15str = " (Q.15: " +q15str+")";
                String decstr = " ("+longValue+")";
                while (decstr.length() < 10) decstr += " ";
                System.out.println(addstr+": "+hexstr+decstr
                                   +q15str // c55x hack
                                   +" "+stack);
                
            }
        }

        if (instrMem){
            while (i.hasNext()){
                Long add = (Long)i.next();
                Instruction instr = (Instruction)memory.get(add);
                System.out.println(Long.toHexString(add.longValue())+": "+
                                                    instr.getMachineCode());
                                   
            }
        }
        
        
        System.out.println("--- End of dump ---");
    }

    private void checkMemoryBoundaries(long address){
        if ((address > this.hiAdd) ||
            (address < this.lowAdd))
            throw new IllegalArgumentException("faulty memory access:" + address);
        
    }

}
