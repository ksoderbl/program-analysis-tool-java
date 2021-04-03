/**
 * Machine.java
 */

package machine;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import instr.*;
import microinstr.*;
import program.Program;

/**
 * A machine.
 *
 * @author Mikko Reinikainen
 * @author Peter Majorin
 * @see Registers
 */

public abstract class Machine {


    public static final long EXIT_ADDR = -1L;

    /** name of the machine (arch) */ 
    private String arch;

    /** supply voltage of CPU core, volts */
    private double supplyVoltage;

    /** clock frequency of CPU core, Hz */
    private double clockFrequency;

    /** registers of the machine */
    private Registers registers;


    /** ArrayList of memoryBanks */
    protected List<MemoryBank> memoryBanks;



    /** scratchpad units */
    private List<Scratchpad> scratchpads;


    /** the value on address bus as read by a microinstruction, note you must initialize these! */ 
    private Constant [] addressValue1 = {new Constant(0L, 64), new Constant(0L, 64), new Constant(0L, 64),
                                         new Constant(0L, 64), new Constant(0L, 64), new Constant(0L, 64),
                                         new Constant(0L, 64), new Constant(0L, 64), new Constant(0L, 64),
                                         new Constant(0L, 64), new Constant(0L, 64), new Constant(0L, 64),
                                         new Constant(0L, 64), new Constant(0L, 64), new Constant(0L, 64),
                                         new Constant(0L, 64), new Constant(0L, 64), new Constant(0L, 64),
                                         new Constant(0L, 64), new Constant(0L, 64), new Constant(0L, 64),
                                         new Constant(0L, 64), new Constant(0L, 64), new Constant(0L, 64),
                                         new Constant(0L, 64), new Constant(0L, 64), new Constant(0L, 64),
                                         new Constant(0L, 64), new Constant(0L, 64), new Constant(0L, 64),
                                         new Constant(0L, 64)};
    
    
    /** the value as a result of a microinstruction computation, note you must initialize these! */
    private Constant [] dataValue1 = {new Constant(0L, 64), new Constant(0L, 64), new Constant(0L, 64),
                                      new Constant(0L, 64), new Constant(0L, 64), new Constant(0L, 64),
                                      new Constant(0L, 64), new Constant(0L, 64), new Constant(0L, 64),
                                      new Constant(0L, 64), new Constant(0L, 64), new Constant(0L, 64),
                                      new Constant(0L, 64), new Constant(0L, 64), new Constant(0L, 64),
                                      new Constant(0L, 64), new Constant(0L, 64), new Constant(0L, 64),
                                      new Constant(0L, 64), new Constant(0L, 64), new Constant(0L, 64),
                                      new Constant(0L, 64), new Constant(0L, 64), new Constant(0L, 64),
                                      new Constant(0L, 64), new Constant(0L, 64), new Constant(0L, 64),
                                      new Constant(0L, 64), new Constant(0L, 64), new Constant(0L, 64),
                                      new Constant(0L, 64)};
    


    private int resultOffset = 0;

    /** the result of a conditional microinstruction, can be used e.g. to implement conditional branches */
    private boolean condition;

    /** is the access to register or memory */
    private Register mmreg = null;

    /** the startAdd of program */
    private long startAdd;

    /** is the simulator running simulation status */
    private boolean simulationStatus = false;
    
    /** the partialExecution */
    protected boolean partialExecutionMode = false;

    /** loop counters */
    protected LinkedList<RepeatLoop> loopCounters;

    /** break points */
    protected HashMap<Long, Long> breakpoints;

    /** memory mapped register address -> registername */
    protected HashMap<Long, String> memoryMappedRegs;
    
    /** the next address of program counter */
    private long nextPCAddr;
    
    /**
     * Constructs a new machine
     *
     * @return the new machine
     */
    public Machine(String name) {
        memoryBanks = new ArrayList<MemoryBank>();
        initMemory();
        scratchpads = new ArrayList<Scratchpad>();
        loopCounters = new LinkedList<RepeatLoop>();
        breakpoints = new HashMap<Long, Long>();
        memoryMappedRegs = new HashMap<Long, String>();
        arch = name;
    }


    public void setProgramStartAdd(long value){
        startAdd = value;
    }
    
    public long getProgramStartAdd(){
        return startAdd;
    }
    



    protected abstract void initMemory();
    
    public abstract int getInstructionBankNum();
    public abstract int getDataBankNum();

    public MemoryBank getMemoryBank(int numBank){
        return memoryBanks.get(numBank);
    }

    public List<MemoryBank> getMemoryBanks(){
        return memoryBanks;
    }
    
    public String getArch(){
        return arch;
    }

    public void setSupplyVoltage(double voltage) {
        this.supplyVoltage = voltage;
    }
    public double getSupplyVoltage() {
        return supplyVoltage;
    }
    public void setClockFrequency(double f) {
        this.clockFrequency = f;
    }
    public double getClockFrequency() {
        return clockFrequency;
    }
    
    /**
     * Set the registers of the machine
     */
    public void setRegisters(ArrayList regNames) {
        // initialize the registers
        registers = new Registers(regNames);
    }

    /**
     * @return registers of the machine
     */
    public Registers getRegisters() {
        return registers;
    }

    /**
     * @return register of the machine
     */
    public Register getRegister(String name) {
    
        Register reg = registers.getRegister(name.toLowerCase());
        if (reg == null) throw new IllegalArgumentException("Machine.java: getRegister: "+name);
        return reg;
    }


    public void addInstruction(Instruction i, int mBIndex){
        MemoryBank mB = memoryBanks.get(mBIndex);
        mB.addInstruction(i);
    }

    public long getNextInstructionAddr(){
        MemoryBank mB = this.getMemoryBank(this.getInstructionBankNum());
        Instruction i = mB.readInstruction(this.getPCAddr());
        return this.getPCAddr()+i.getSize();        
    }

    
    /**
     * Prints the changes that has been made to all memory banks
     */
    public void printChangesMemory(){
        int i = 1;
        Iterator<MemoryBank> iter = memoryBanks.iterator();
        System.out.println("Banks:"+memoryBanks.size());
        while (iter.hasNext()){
            MemoryBank mb = iter.next();
            if (!mb.getInstructionMem())
            mb.printChangesMemory(i, this);
            i++;
        }
        
    }


    

    public long readMem(long address, int numBank){
        Long value;
        Long longAdd = new Long(address);
        MemoryBank mb = memoryBanks.get(numBank);

        /* String regName = (String)memoryMappedRegs.get(longAdd);
           if (regName != null) { 
           Register reg = getRegister(regName);
           System.out.println("bbuu"+reg.getValue());
           return reg.getValue();
           }
           else
        */ 
        return mb.readMem(address);
    }

    public void writeMem(long address, long value, int numBank){
        Long longAdd  = new Long(address);
        Long longValue = new Long(value);
        MemoryBank mb = memoryBanks.get(numBank);
        /* check for memory mapped registers
        String regName = (String)memoryMappedRegs.get(new Long(address));
        if (regName != null) { 
        Register reg = getRegister(regName);
        reg.setValue(value);
        System.out.println(reg.getName());
        }
        else
        */
        mb.writeMem(longAdd, longValue);
    }
    
    public void setMachineResultOffset(int off) {
        //System.out.println("setting offset to "+ off);
        this.resultOffset = off;
    }
    public int getMachineResultOffset() {
        return resultOffset;
    }
    

    public Constant getAddressResult(int index){
        //System.out.println("getting address result index " + index + "(really " + (index+resultOffset) + ")");
        return addressValue1[index+resultOffset];
    }
    public Constant getDataResult(int index){
        //System.out.println("getDataResult:"+index+" MemBank:"+microMemBank);
        //throw new NullPointerException("foo");
        //System.out.println("getting data result index " + index + "(really " + (index+resultOffset) + ")");
        return dataValue1[index+resultOffset];
    }
    

    public void setAddressResult(long value, int index){
        //System.out.println("setting address result index " + index + "(really " + (index+resultOffset) + ")");
        addressValue1[index+resultOffset].setValue(value);
        
   }
    
    public void setDataResult(long value, int bitSize, int index){
        //System.out.println("setting data result index " + index + "(really " + (index+resultOffset) + ")");
        dataValue1[index+resultOffset].setValue(value);
        dataValue1[index+resultOffset].setBitSize(bitSize);
    }

    public void printDataResult(){
        for (int i = 0; i < dataValue1.length; i++){
            System.out.print(dataValue1[i].getValue() + " ");
        }
        System.out.println("");
    }

    public boolean getCondition(){
        return this.condition;
    }
    
    public void setCondition(boolean value){
        this.condition = value;
    }

    /* 
     *  set the register for memory mapped register access through memory 
     *  null value is 
     *
     */
    public void setMMREG(Register reg){
        this.mmreg = reg;
    }
    
    public Register getMMREG(){
        return this.mmreg;
    }

    public void printBreakpoints(){
        Iterator<Long> iter = breakpoints.values().iterator();
        while (iter.hasNext()){
         System.out.println(Long.toHexString((iter.next()).longValue()));
        }
    }

    public void addBreakpoint(long address){
        Long key = new Long(address);
        breakpoints.put(key, key);
        System.out.println("machine: bp set at 0x" + Long.toHexString(address));
    }

    public boolean isBreakpoint(long address){
        Long key = new Long(address);
        if (breakpoints.get(key) != null) return true;
        else return false;
    }


    /**
     *@param register, the register where the carry occurred
     *
     */
    public abstract void setCarry(Register register);
    public abstract long getCarry();
    
    
    /** only for signed arithmetic operations */
    public abstract void setOverflow(Register register, boolean overflowPositive);
    public abstract long getOverflow(Register register);


    public abstract void reset(Program program);



    /* has the program terminated? */
    public boolean simulationRunning(){
        return simulationStatus;
    }


    public void setSimulationRunning(boolean status){
        this.simulationStatus = status;
    }


    /**
     * add a scratchpad into the machine
     */
    public void addScratchpad(Scratchpad sp){
        scratchpads.add(sp);
    }

    /**
     * get scratchpads of the machine
     * @return scratchpads
     */
    public List<Scratchpad> getScratchpads(){
        return scratchpads;
    }

    public int getNumLoopCounters(){
        return loopCounters.size();
    }

    public void deleteLoopCounters(){
        loopCounters.clear();
    }

    public void addLoopCounter(RepeatLoop repeat){
        System.out.println(" Machine.addLoopCounter: adding " + repeat);
        loopCounters.add(repeat);
    }
    
    public boolean handleRepeats(){
        if (loopCounters.size() == 0) return false;

        RepeatLoop repeat = loopCounters.getLast();
        
        if (this.getPCAddr() == repeat.getEndAddr()){
            if (repeat.decrementCounter()){
                loopCounters.removeLast();
                return true;
            }
            setNextPCAddr(repeat.getStartAddr());
        }
        
        return true;
    }
    


    public void setNextPCAddr(long value){
        nextPCAddr = value;
    }
    
    public long getNextPCAddr(){
        return nextPCAddr; 
    }


    public abstract long getPCAddr();
    public abstract void setPCAddr(long value);


    public abstract long getStackAddr();
    public abstract void setStackAddr(long value);
    


    public abstract Instruction makeLoad(ArrayList registers);
    public abstract Instruction makeMove(ArrayList registers);
    public abstract boolean isSpecialRegister(Register reg);
    public abstract int getRegisterCount();
    public abstract int getPCReg();
    public abstract boolean isPartialExecutionMode();
    public abstract void setPartialExecutionMode(boolean mode);
    public abstract double getMainMemoryEnergy();
}
