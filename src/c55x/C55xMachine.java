/**
 * C55xMachine.java
 */

package c55x;

import input.Input;
import machine.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import instr.*;
import c55x.instr.*;
import misc.*;
import main.*;
import program.Program;


/**
 * A TI C55x machine.
 *
 * @author Kristian Söderblom
 */

public class C55xMachine extends Machine {

    // memory access energy for C55x
    public static final double MAINMEMENERGY = 4.0; // in nJ
    // initial stack address

    public static final long INIT_STACKADDR = 0x9000L;

    // ST0 - status register bits
    public static final int ACOV2 = 15; // spru371f, p. 2-38
    public static final int ACOV3 = 14;
    public static final int TC1   = 13;
    public static final int TC2   = 12;
    public static final int CARRY = 11;
    public static final int ACOV0 = 10;
    public static final int ACOV1 = 9;
    
    // ST1 - status register bits
    public static final int BSRAF  = 15;
    public static final int CPL   = 14;
    public static final int XF    = 13;
    public static final int HM    = 12;
    public static final int INTM  = 11;
    public static final int M40   = 10;
    public static final int SATD  = 9;
    public static final int SXMD  = 8;
    public static final int C16   = 7;
    public static final int FRCT  = 6;
    public static final int C54CM = 5;

    // ST2 - status register bits 
    public static final int ARMS   = 15;
    public static final int DBGM   = 12;
    public static final int EALLOW = 11;
    public static final int RDM    = 10;
    public static final int CDPLC  = 8;
    public static final int AR7LC  = 7;
    public static final int AR6LC  = 6;
    public static final int AR5LC  = 5;
    public static final int AR4LC  = 4;
    public static final int AR3LC  = 3;
    public static final int AR2LC  = 2;
    public static final int AR1LC  = 1;
    public static final int AR0LC  = 0;

    // ST3 - status register bits
    public static final int CAFRZ  = 15;
    public static final int CAEN   = 14;
    public static final int CACLR  = 13;
    public static final int HINT   = 12;
    public static final int CBERR  = 7;
    public static final int MPNMC  = 6;
    public static final int SATA   = 5;
    public static final int CLKOFF = 2;
    public static final int SMUL   = 1;
    public static final int SST    = 0;
    

    public static final int AUNIT = 1;
    public static final int DUNIT = 2;

    // pipeline Pipe, Pipeline Pipeline phase in which the instruction executes:
    //           AD Address
    //           D  Decode
    //           R  Read
    //           X  Execute
    // /cvs/bib/ti/spru374g.pdf
    public static final int PipelineAD = 1;
    public static final int PipelineD  = 2;
    public static final int PipelineR  = 3;
    public static final int PipelineX  = 4;
    public static final int PipelineX2 = 5; // typo?
   

    /**
     * Constructs a new C55x machine
     *
     * @return the new C55x machine
     */
    public C55xMachine(String arch) {
        super(arch);

        setSupplyVoltage(1.6); // 1.6 volts
        setClockFrequency(24 * 1e6); // 24 MHz

        ArrayList a = new ArrayList();

        // accumulators
        a.add(new C55xRegister("ac0", 40, DUNIT, this, true));        // spru371f, p. 2-9
        a.add(new C55xRegister("ac1", 40, DUNIT, this, true));
        a.add(new C55xRegister("ac2", 40, DUNIT, this, true));
        a.add(new C55xRegister("ac3", 40, DUNIT, this, true));
        
        // auxiliary registers are register aliases

        // circular buffer size registers
        a.add(new C55xRegister("bk03", 16, AUNIT, this));
        a.add(new C55xRegister("bk47", 16, AUNIT, this));
        a.add(new C55xRegister("bkc",  16, AUNIT, this));

        // Data Page Register (XDP / DP)
        a.add(new C55xRegister("xdp",  23, AUNIT, this));

        // Block repeat counters 0 and 1
        a.add(new C55xRegister("brc0", 16, this));
        a.add(new C55xRegister("brc1", 16, this));

        // coefficient data pointer (low part of XCDP)
        //a.add(new C55xRegister("cdp", 16, this));
        
        // Computed single-repeat register
        a.add(new C55xRegister("csr", 16, this));

        // BRC1 save register
        a.add(new C55xRegister("brs1", 16, this));

        // program counter
        a.add(new C55xRegister("pc", 24, this));
        
        // single-repeat counter
        a.add(new C55xRegister("rptc", 16, this));
        
        // low part XSSP
        a.add(new C55xRegister("ssp", 16, AUNIT, this));

        // status registers ST0-ST3
        a.add(new C55xRegister("st0",16, this));
        a.add(new C55xRegister("st1",16, this));
        a.add(new C55xRegister("st2",16, this));
        a.add(new C55xRegister("st3",16, this));


        // temporary registers
        a.add(new C55xRegister("t0", 16, AUNIT, this));
        a.add(new C55xRegister("t1", 16, AUNIT, this));
        a.add(new C55xRegister("t2", 16, AUNIT, this));
        a.add(new C55xRegister("t3", 16, AUNIT, this));

        // transition registers 0 and 1
        a.add(new C55xRegister("trn0", 16, DUNIT, this));
        a.add(new C55xRegister("trn1", 16, DUNIT, this));
        
        
        // Extended auxiliary registers 0 through 7
        a.add(new C55xRegister("xar0", 23, AUNIT, this));
        a.add(new C55xRegister("xar1", 23, AUNIT, this));
        a.add(new C55xRegister("xar2", 23, AUNIT, this));
        a.add(new C55xRegister("xar3", 23, AUNIT, this));
        a.add(new C55xRegister("xar4", 23, AUNIT, this));
        a.add(new C55xRegister("xar5", 23, AUNIT, this));
        a.add(new C55xRegister("xar6", 23, AUNIT, this));
        a.add(new C55xRegister("xar7", 23, AUNIT, this));

        a.add(new C55xRegister("xcdp", 23, AUNIT, this));

        a.add(new C55xRegister("xdp", 23, AUNIT, this));

        a.add(new C55xRegister("xsp",  23, AUNIT, this));

        // Circular buffer size registers
        a.add(new C55xRegister("bk03", 16, AUNIT, this));
        a.add(new C55xRegister("bk47", 16, AUNIT, this));
        a.add(new C55xRegister("bkc",  16, AUNIT, this));

        // Circular buffer start address registers
        a.add(new C55xRegister("bsa01", 16, AUNIT, this));
        a.add(new C55xRegister("bsa23", 16, AUNIT, this));
        a.add(new C55xRegister("bsa45", 16, AUNIT, this));
        a.add(new C55xRegister("bsa67", 16, AUNIT, this));
        a.add(new C55xRegister("bsac",  16, AUNIT, this));

        // register aliases

        super.setRegisters(a);

        // data stack pointer (low part of XSP)
        // registeraliasname, register, op size, shiftrightsize

        a.add(new RegisterAlias("ar0",super.getRegister("xar0"),16,0));
        a.add(new RegisterAlias("ar1",super.getRegister("xar1"),16,0));
        a.add(new RegisterAlias("ar2",super.getRegister("xar2"),16,0));
        a.add(new RegisterAlias("ar3",super.getRegister("xar3"),16,0));
        a.add(new RegisterAlias("ar4",super.getRegister("xar4"),16,0));
        a.add(new RegisterAlias("ar5",super.getRegister("xar5"),16,0));
        a.add(new RegisterAlias("ar6",super.getRegister("xar6"),16,0));
        a.add(new RegisterAlias("ar7",super.getRegister("xar7"),16,0));

        a.add(new RegisterAlias("cdp",super.getRegister("xcdp"),16,0));

        a.add(new RegisterAlias("sp",super.getRegister("xsp"),64,0));
        a.add(new RegisterAlias("st0_55",this.getRegister("st0"),16,0));
        a.add(new RegisterAlias("st1_55",this.getRegister("st1"),16,0));
        a.add(new RegisterAlias("st2_55",this.getRegister("st2"),16,0));
        a.add(new RegisterAlias("st3_55",this.getRegister("st3"),16,0));

        a.add(new RegisterAlias("t0b",this.getRegister("t0"),16,0));

        //a.add(new RegisterAlias("tc1", TC1, this.getRegister("st0")));
        //a.add(new RegisterAlias("tc2", TC2, this.getRegister("st0")));

        /*a.add(new RegisterAlias("st0_acov0", ACOV0, this.getRegister("st0")));

        a.add(new RegisterAlias("st1_c54cm", C54CM, this.getRegister("st1")));
        a.add(new RegisterAlias("st1_cpl",  CPL,  this.getRegister("st1")));
        a.add(new RegisterAlias("st1_frct", FRCT, this.getRegister("st1")));
        a.add(new RegisterAlias("st1_m40",  M40,  this.getRegister("st1")));
        a.add(new RegisterAlias("st1_satd", SATD, this.getRegister("st1")));
        a.add(new RegisterAlias("st1_sxmd", SXMD, this.getRegister("st1")));

        a.add(new RegisterAlias("st2_ar0lc", AR0LC, this.getRegister("st2")));
        a.add(new RegisterAlias("st2_ar1lc", AR1LC, this.getRegister("st2")));
        a.add(new RegisterAlias("st2_ar3lc", AR3LC, this.getRegister("st2")));
        a.add(new RegisterAlias("st2_ar4lc", AR4LC, this.getRegister("st2")));
        a.add(new RegisterAlias("st2_arms",  ARMS,  this.getRegister("st2")));
        a.add(new RegisterAlias("st2_cdplc", CDPLC, this.getRegister("st2")));
        a.add(new RegisterAlias("st2_ar2lc", AR2LC, this.getRegister("st2")));

        a.add(new RegisterAlias("st3_sata", SATA, this.getRegister("st3")));
        a.add(new RegisterAlias("st3_smul", SMUL, this.getRegister("st3")));
        a.add(new RegisterAlias("st3_sst",  SST,  this.getRegister("st3")));*/

        a.add(new RegisterAlias("ac0l", super.getRegister("ac0"),16, 0));
        a.add(new RegisterAlias("ac0h", super.getRegister("ac0"),16, 16));
        a.add(new RegisterAlias("ac0g", super.getRegister("ac0"), 8, 32));
        a.add(new RegisterAlias("ac1l", super.getRegister("ac1"),16, 0));
        a.add(new RegisterAlias("ac1h", super.getRegister("ac1"),16, 16));
        a.add(new RegisterAlias("ac1g", super.getRegister("ac1"), 8, 32));
        a.add(new RegisterAlias("ac2l", super.getRegister("ac2"),16, 0));
        a.add(new RegisterAlias("ac2h", super.getRegister("ac2"),16, 16));
        a.add(new RegisterAlias("ac2g", super.getRegister("ac2"), 8, 32));
        a.add(new RegisterAlias("ac3l", super.getRegister("ac3"),16, 0));
        a.add(new RegisterAlias("ac3h", super.getRegister("ac3"),16, 16));
        a.add(new RegisterAlias("ac3g", super.getRegister("ac3"), 8, 32));

        a.add(new RegisterAlias("dp",  super.getRegister("xdp"), 16, 0));
        a.add(new RegisterAlias("dph", super.getRegister("xdp"),  7, 16));

        super.setRegisters(a);

        
        // TODO ...
        
       
        // add memory mapped regs to separate hashtable
    }

    public void setSXMD(){
        Register  st1  = super.getRegister("st1");
        st1.setBit(SXMD);
    }
    
    public long getSXMD(){
        Register  st1  = super.getRegister("st1");
        return st1.testBit(SXMD);
    }

    public long getFRCT(){
        Register  st1  = super.getRegister("st1");
        return st1.testBit(FRCT);
    }
    
    public void setSATD(){
        Register  st1  = super.getRegister("st1");
        st1.setBit(SATD);
    }
    
    public long getSATD(){
        Register  st1  = super.getRegister("st1");
        return st1.testBit(SATD);
    }
    
    public void setM40(){
        Register  st1  = super.getRegister("st1");
        st1.setBit(M40);
    }

    public long getM40(){
        Register  st1  = super.getRegister("st1");
        return st1.testBit(M40);
    }




    public void setCarry(Register register){
        Register  st0  = super.getRegister("st0");
        st0.setBit(CARRY);
    }
    public void setCarry(){
        Register  st0  = super.getRegister("st0");
        st0.setBit(CARRY);
    }
    public void clearCarry(){
        Register  st0  = super.getRegister("st0");
        st0.clearBit(CARRY);
    }

    public long getCarry(){
        Register  st0  = super.getRegister("st0");
        return st0.testBit(CARRY);

    }

    public void setOverflow(Register register, boolean overflowPositive){
        Register st0  = super.getRegister("st0");

        String regName = register.getName();
        if (regName.equals("ac0")){
            st0.setBit(ACOV0);
            saturate(register, overflowPositive);
        }
        else if (regName.equals("ac1")){
            st0.setBit(ACOV1);
            saturate(register, overflowPositive);
        }
        else if (regName.equals("ac2")){
            st0.setBit(ACOV2);
            saturate(register, overflowPositive);
        }
        else if (regName.equals("ac3")){
            st0.setBit(ACOV3);
            saturate(register, overflowPositive);
        }
        else return; // No overflow for this register supported
    }

    //  spru371f, p. 2-50
    private void saturate(Register register, boolean overflowPositive){
        if (getSATD() == 0){
            return;
        }
        else {
            if (getM40() == 0){
                if (overflowPositive)
                    register.setValue(0x007fffffffL);
                else
                    register.setValue(0xff80000000L);
                return;
            }
            else {
                if (overflowPositive)
                    register.setValue(0x7fffffffffL);
                else
                    register.setValue(0x8000000000L);
                return;
            }
        }
        
    }
    
    public long getOverflow(Register register){
        
        // get ACOVx status register
        Register  st0  = super.getRegister("st0");

        String regName = register.getName();

        if (regName.equals("ac0")){
            return st0.testBit(ACOV0);
        }
        else if (regName.equals("ac1")){
            return st0.testBit(ACOV1);
        }
        else if (regName.equals("ac2")){
            return st0.testBit(ACOV2);
        }
        else if (regName.equals("ac3")){
            return st0.testBit(ACOV3);
        }

        else return -1; // no overflow supported for this register
    }


        public void printStatusRegs(){
         String string1 ="";
         Registers regs  = super.getRegisters();

         Register  st0  = regs.getRegister("st0");
         Register  st1  = regs.getRegister("st1");
         Register  st2  = regs.getRegister("st2");
         Register  st3  = regs.getRegister("st3");
         string1 ="";

         
          if (st0.testBit(ACOV3) == 1) string1 +="ACOV3 ";
          if (st0.testBit(TC1) == 1) string1 +="TC1 ";        
          if (st0.testBit(TC2) == 1) string1 +="TC2 ";        
          if (st0.testBit(CARRY) == 1) string1 +="CARRY ";        
          if (st0.testBit(ACOV0) == 1) string1 +="ACOV0 ";
          if (st0.testBit(ACOV1) == 1) string1 +="ACOV1 ";
  
          System.out.println("st0: "+string1);
          string1 ="";

          if (st1.testBit(BSRAF) == 1) string1 +="BSRAF ";
          if (st1.testBit(CPL) == 1) string1 +="CPL ";
          if (st1.testBit(XF) == 1) string1 +="XF ";
          if (st1.testBit(HM) == 1) string1 +="HM ";
          if (st1.testBit(INTM) == 1) string1 +="INTM ";
          if (st1.testBit(M40) == 1) string1 +="M40 ";
          
          if (st1.testBit(SATD) == 1) string1 +="SATD ";
          if (st1.testBit(SXMD) == 1) string1 +="SXMD ";
          if (st1.testBit(C16) == 1) string1 +="C16 ";
          if (st1.testBit(FRCT) == 1) string1 +="FRCT ";
          if (st1.testBit(C54CM) == 1) string1 +="C54CM ";

          System.out.println("st1: "+string1);
          string1 ="";

          if (st2.testBit(ARMS) == 1) string1 +="ARMS ";
          if (st2.testBit(DBGM) == 1) string1 +="DBGM ";
          if (st2.testBit(EALLOW) == 1) string1 +="EALLOW ";
          if (st2.testBit(RDM) == 1) string1 +="RMD ";
          if (st2.testBit(CDPLC) == 1) string1 +="CDPLC ";
          if (st2.testBit(AR7LC) == 1) string1 +="AR7LC ";
          if (st2.testBit(AR6LC) == 1) string1 +="AR6LC ";
          if (st2.testBit(AR5LC) == 1) string1 +="AR5LC ";
          if (st2.testBit(AR4LC) == 1) string1 +="AR4LC ";
          if (st2.testBit(AR3LC) == 1) string1 +="AR3LC ";
          if (st2.testBit(AR2LC) == 1) string1 +="AR2LC ";
          if (st2.testBit(AR1LC) == 1) string1 +="AR1LC ";
          if (st2.testBit(AR0LC) == 1) string1 +="AR0LC ";        

          System.out.println("st2: "+string1);
          string1 ="";

        // ST3 - status register bits

          if (st3.testBit(CAFRZ) == 1) string1 +="CAFRZ ";
          if (st3.testBit(CAEN) == 1) string1 +="CAEN ";
          if (st3.testBit(CACLR) == 1) string1 +="CACLR ";
          if (st3.testBit(HINT) == 1) string1 +="HINT ";
          if (st3.testBit(CBERR) == 1) string1 +="CBERR ";
          if (st3.testBit(MPNMC) == 1) string1 +="MPNMC ";
          if (st3.testBit(SATA) == 1) string1 +="SATA ";
          if (st3.testBit(CLKOFF) == 1) string1 +="CLKOFF ";
          if (st3.testBit(SMUL) == 1) string1 +="SMUL ";
          if (st3.testBit(SST) == 1) string1 +="SST ";

           System.out.println("st3: "+string1);
        }

        /** resets the machine; inits status registers, sets PC and stack
         *  to their desired default values
         */    
    public void reset(Program program){

        Registers regs  = super.getRegisters();

        Register  st0  = regs.getRegister("st0");
        Register  st1  = regs.getRegister("st1");
        Register  st2  = regs.getRegister("st2");
        Register  st3  = regs.getRegister("st3");
        
        st0.setValue(0);
        st1.setValue(0);
        st2.setValue(0);
        st3.setValue(0);
        
        st0.setBit(TC1); st0.setBit(TC2);
        st0.setBit(CARRY);

        st1.setBit(XF);        st1.setBit(INTM); st1.setBit(SXMD); st1.setBit(C54CM);

        st2.setBit(DBGM);

        st3.setBit(HINT); st3.setBit(11); st3.setBit(10);

        // reset PC
      
        setPCAddr(program.getEntryAddress().longValue());
        // init stack
        Register sp = super.getRegister("sp");

        setStackAddr(INIT_STACKADDR);

        // write exit address on stack;

        this.writeMem(sp.getValue(), EXIT_ADDR, this.getDataBankNum());

        // reset loopcounters
        super.deleteLoopCounters();
        // add breakpoints for c55x programs
        //        Long bp1 = new Long(0x5037ee);
        //        Long bp1 = new Long(0x502fa6);
        //        Long bp1 = new Long(0x1b1);
        //breakpoints.put(bp1, bp1);



    }

   public double getMainMemoryEnergy(){
        return MAINMEMENERGY;
   }


    // not implemented
    public int getPCReg(){
        return 0;
    }
    

    public long getPCAddr(){
        Register  pc  = super.getRegister("pc");
        return pc.getValue();
    }
    
    public void setPCAddr(long value){
        Register  pc  = super.getRegister("pc");
        pc.setValue(value);
    }



    public long getStackAddr(){
        Register sp = super.getRegister("sp");
        return sp.getValue();
    }

    public void setStackAddr(long value){
        Register sp = super.getRegister("sp");
        sp.setValue(value);
    }



    public void initMemory(){
        memoryBanks.add(new MemoryBank(0x0L, 0xffffL, MemoryBank.INSTRUCTION_MEMORY));
        memoryBanks.add(new MemoryBank(0x0L, 0xffffL, MemoryBank.DATA_MEMORY));
        return;
    }
    
    
    public int getInstructionBankNum(){
        return 0;
        
    }
    public int getDataBankNum(){
        return 1;
    }



    public Instruction makeLoad(ArrayList registers) {
        return null;
    }

    public Instruction makeMove(ArrayList registers) {
        return null;
    }


    /**
     * Tells whether the given register has a special function.
     *
     * @param reg The register
     * @return True if the register <code>reg</code> has a special function.
     */
    public boolean isSpecialRegister(Register reg) {
        
        if (reg == null) return false;
        int number = reg.getPos();
        
        return true;
    }

    /**
     * @return The number of registers in this machine
     */
    public int getRegisterCount() {
        return 0 /*C55xMachine.NUMREGS*/;
    }



    public void setPartialExecutionMode(boolean mode){
        partialExecutionMode = mode;
    }
    public boolean isPartialExecutionMode(){
         return partialExecutionMode;
    }

    // circular addressing stuff
    public boolean isRegisterWithCircularAddressing(Register reg) {
        if (reg == null)
            return false;
        String name = reg.getName();
        Register st2 = this.getRegister("st2");

        if (st2.testBit(AR0LC) == 1) {
            if (name.equals("ar0"))
                return true;
            if (name.equals("xar0") || name.equals("ar0h"))
                throw new NullPointerException("can't handle circular addressing for " + name);
        }
        if (st2.testBit(AR1LC) == 1) {
            if (name.equals("ar1"))
                return true;
            if (name.equals("xar1") || name.equals("ar1h"))
                throw new NullPointerException("can't handle circular addressing for " + name);
        }
        if (st2.testBit(AR2LC) == 1) {
            if (name.equals("ar2"))
                return true;
            if (name.equals("xar2") || name.equals("ar2h"))
                throw new NullPointerException("can't handle circular addressing for " + name);
        }
        if (st2.testBit(AR3LC) == 1) {
            if (name.equals("ar3"))
                return true;
            if (name.equals("xar3") || name.equals("ar3h"))
                throw new NullPointerException("can't handle circular addressing for " + name);
        }
        if (st2.testBit(AR4LC) == 1) {
            if (name.equals("ar4"))
                return true;
            if (name.equals("xar4") || name.equals("ar4h"))
                throw new NullPointerException("can't handle circular addressing for " + name);
        }
        if (st2.testBit(AR5LC) == 1) {
            if (name.equals("ar5"))
                return true;
            if (name.equals("xar5") || name.equals("ar5h"))
                throw new NullPointerException("can't handle circular addressing for " + name);
        }
        if (st2.testBit(AR6LC) == 1) {
            if (name.equals("ar6"))
                return true;
            if (name.equals("xar6") || name.equals("ar6h"))
                throw new NullPointerException("can't handle circular addressing for " + name);
        }
        if (st2.testBit(AR7LC) == 1) {
            if (name.equals("ar7"))
                return true;
            if (name.equals("xar7") || name.equals("ar7h"))
                throw new NullPointerException("can't handle circular addressing for " + name);
        }
        if (st2.testBit(CDPLC) == 1) {
            if (name.equals("cdp"))
                return true;
            if (name.equals("xcdp") || name.equals("cdph"))
                throw new NullPointerException("can't handle circular addressing for " + name);
        }
        return false;
    }

    public long getBufferStartAddress(Register reg) {
        String name = reg.getName();
        if (name.equals("ar0")) return getRegister("bsa01").getValue();
        if (name.equals("ar1")) return getRegister("bsa01").getValue();
        if (name.equals("ar2")) return getRegister("bsa23").getValue();
        if (name.equals("ar3")) return getRegister("bsa23").getValue();
        if (name.equals("ar4")) return getRegister("bsa45").getValue();
        if (name.equals("ar5")) return getRegister("bsa45").getValue();
        if (name.equals("ar6")) return getRegister("bsa67").getValue();
        if (name.equals("ar7")) return getRegister("bsa67").getValue();
        if (name.equals("cdp")) return getRegister("bsac").getValue();
        throw new NullPointerException("getBufferStartAddress: failed for reg " + name);
    }

    public long getBufferSize(Register reg) {
        String name = reg.getName();

        if (name.equals("ar0")) return getRegister("bk03").getValue();
        if (name.equals("ar1")) return getRegister("bk03").getValue();
        if (name.equals("ar2")) return getRegister("bk03").getValue();
        if (name.equals("ar3")) return getRegister("bk03").getValue();
        if (name.equals("ar4")) return getRegister("bk47").getValue();
        if (name.equals("ar5")) return getRegister("bk47").getValue();
        if (name.equals("ar6")) return getRegister("bk47").getValue();
        if (name.equals("ar7")) return getRegister("bk47").getValue();
        if (name.equals("cdp")) return getRegister("bkc").getValue();

        throw new NullPointerException("getBufferStartAddress: failed for reg " + name);
    }


    public int getBitByStatusBitName(String name) {
        // ST0
        if (name.equals("TC1")) return TC1;
        if (name.equals("TC2")) return TC2;
        if (name.equals("Carry")) return CARRY;
        if (name.equals("ST0_ACOV0")) return ACOV0;
        // ST1
        if (name.equals("ST1_CPL")) return CPL;
        if (name.equals("ST1_M40")) return M40;
        if (name.equals("ST1_SATD")) return SATD;
        if (name.equals("ST1_SXMD")) return SXMD;
        if (name.equals("ST1_FRCT")) return FRCT;
        if (name.equals("ST1_C54CM")) return C54CM;
        // ST2
        if (name.equals("ST2_ARMS")) return ARMS;
        if (name.equals("ST2_CDPLC")) return CDPLC;
         if (name.equals("ST2_AR7LC")) return AR7LC;
        if (name.equals("ST2_AR6LC")) return AR6LC;
        if (name.equals("ST2_AR5LC")) return AR5LC;
        if (name.equals("ST2_AR4LC")) return AR4LC;
        if (name.equals("ST2_AR3LC")) return AR3LC;
        if (name.equals("ST2_AR2LC")) return AR2LC;
        if (name.equals("ST2_AR1LC")) return AR1LC;
        if (name.equals("ST2_AR0LC")) return AR0LC;
        // ST3
        if (name.equals("ST3_SATA")) return SATA;
        if (name.equals("ST3_SMUL")) return SMUL;

        throw new NullPointerException("unknown status bit: " + name);
    }
    public Register getRegisterByStatusBitName(String name) {
        // ST0
        if (name.equals("TC1")) return getRegister("st0");
        if (name.equals("TC2")) return getRegister("st0");
        if (name.equals("Carry")) return getRegister("st0");
        if (name.equals("ST0_ACOV0")) return getRegister("st0");
        // ST1
        if (name.equals("ST1_CPL")) return getRegister("st1");
        if (name.equals("ST1_M40")) return getRegister("st1");
        if (name.equals("ST1_SATD")) return getRegister("st1");
        if (name.equals("ST1_SXMD")) return getRegister("st1");
        if (name.equals("ST1_FRCT")) return getRegister("st1");
        if (name.equals("ST1_C54CM")) return getRegister("st1");
        // ST2
        if (name.equals("ST2_ARMS")) return getRegister("st2");
        if (name.equals("ST2_CDPLC")) return getRegister("st2");
         if (name.equals("ST2_AR7LC")) return getRegister("st2");
        if (name.equals("ST2_AR6LC")) return getRegister("st2");
        if (name.equals("ST2_AR5LC")) return getRegister("st2");
        if (name.equals("ST2_AR4LC")) return getRegister("st2");
        if (name.equals("ST2_AR3LC")) return getRegister("st2");
        if (name.equals("ST2_AR2LC")) return getRegister("st2");
        if (name.equals("ST2_AR1LC")) return getRegister("st2");
        if (name.equals("ST2_AR0LC")) return getRegister("st2");
        // ST3
        if (name.equals("ST3_SATA")) return getRegister("st3");
        if (name.equals("ST3_SMUL")) return getRegister("st3");
        throw new NullPointerException("unknown status bit: " + name);
    }


    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////
    
    private void MYPRINT(String foo) {
        System.out.println(foo);
    }


    //
    //
    // Methods to make specific instructions.
    // The pXX page numbers are from
    //
    //    TMS320C55x DSP Mnemonic Instruction Set
    //              Reference Guide
    //          Literature Number: SPRU374G
    //              October 2002
    //
    // /cvs/bib/ti/spru374g.pdf
    //

    public C55xOperation make_AADD_p95_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineAD,
                                               95);
        //MYPRINT("make_AADD_p95_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_AADD_p97_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineAD,
                                               97);
        //MYPRINT("make_AADD_p97_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_AADD_p98_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineAD,
                                               98);
        //MYPRINT("make_AADD_p98_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    //
    // ABDST, p.99
    //
    public C55xOperation make_ABDST_p99_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               99);
        //MYPRINT("make_ABDST_p99_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    //
    // ABS, p. 101
    //
    public C55xOperation make_ABS_p101_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               101);
        //MYPRINT("make_ABS_p101_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    //
    // ADD
    //
    public C55xOperation make_ADD_p106_Instr(String opcode, List<Operand> operandList, String syntax) {
        // ADD [src],dst
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               106);
        //MYPRINT("make_ADD_p106_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ADD_p107_Instr(String opcode, List<Operand> operandList, String syntax) {
        // ADD k4, dst
        // ADD K16,[src],dst p.108
        // TODO
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               107);
        //MYPRINT("make_ADD_p107_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ADD_p110_Instr(String opcode, List<Operand> operandList, String syntax) {
        // ADD Smem, [src], dst
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               110);
        //MYPRINT("make_ADD_p110_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ADD_p112_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               112);
        //MYPRINT("make_ADD_p112_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ADD_p113_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX,
                                               113);
        //MYPRINT("make_ADD_p113_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ADD_p114_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               114);
        //MYPRINT("make_ADD_p114_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ADD_p115_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               115);
        //MYPRINT("make_ADD_p115_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ADD_p116_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               116);
        //MYPRINT("make_ADD_p116_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ADD_p118_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               118);
        //MYPRINT("make_ADD_p118_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ADD_p119_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               119);
        //MYPRINT("make_ADD_p119_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ADD_p120_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               120);
        //MYPRINT("make_ADD_p120_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ADD_p121_Instr(String opcode, List<Operand> operandList, String syntax) {
        
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               121);
        //MYPRINT("make_ADD_p121_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }
    
    public C55xOperation make_ADD_p122_Instr(String opcode, List<Operand> operandList, String syntax) {
        // add dbl(Lmem), [ACx,] ACy
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               122);
        //MYPRINT("make_ADD_p122_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ADD_p123_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               123);
        //MYPRINT("make_ADD_p123_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ADD_p124_Instr(String opcode, List<Operand> operandList, String syntax) {
        //ADD K16, Smem
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               124);
        //MYPRINT("make_ADD_p124_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ADD_p126_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               126);
        //MYPRINT("make_ADD_p126_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ADD_p128_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               128);
        //MYPRINT("make_ADD_p128_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    @SuppressWarnings("unchecked")
    private List<Operand> getOperandList(List<Object> objectList, int index) {
        Object obj = objectList.get(index);
        return (List<Operand>)obj;
    }

    @SuppressWarnings("unchecked")
    private String getOpcode(List<Object> objectList, int index) {
        Object obj = objectList.get(index);
        return (String)obj;
    }

    public C55xOperation make_ADD_p130_Instr(
                                           String opcode, List<Object> objectList,
                                           String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        // ADD Xmem << #16, ACx, ACy :: MOV HI(ACy << T2), Ymem
        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               130);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                130);

        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_ADD_p130_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ADDSUB_p133_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               133);
        //MYPRINT("make_ADDSUB_p133_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ADDSUB_p135_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               135);
        //MYPRINT("make_ADDSUB_p135_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }


    public C55xOperation make_ADDSUBCC_p137_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               137);
        //MYPRINT("make_ADDSUBCC_p137_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ADDSUBCC_p139_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               139);
        //MYPRINT("make_ADDSUBCC_p139_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }


    public C55xOperation make_ADDSUB2CC_p141_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               141);
        //MYPRINT("make_ADDSUB2CC_p141_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ADDV_p144_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               144);
        //MYPRINT("make_ADDV_p144_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_AMAR_p146_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 2, 1, 0, 0, PipelineAD,
                                               146);
        //MYPRINT("make_AMAR_p146_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_AMAR_p148_Instr(String opcode, List<Operand> operandList, String syntax) {
        // AMAR Smem, XAdst
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineAD,
                                               148);
        //MYPRINT("make_AMAR_p148_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_AMAR_p149_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               149);
        //MYPRINT("make_AMAR_p149_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_AMAR_p151_Instr(
                                            String opcode, List<Object> objectList,
                                            String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               151);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                151);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_AMAR_p151_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_AMAR_p153_Instr(
                                            String opcode, List<Object> objectList,
                                            String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               153);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                153);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_AMAR_p153_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_AMAR_p155_Instr(
                                            String opcode, List<Object> objectList,
                                            String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               155);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                155);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_AMAR_p155_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_AMAR_p157_Instr(
                                            String opcode, List<Object> objectList,
                                            String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               157);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                157);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_AMAR_p157_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_AMOV_p159_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 6, 1, 0, 0, PipelineAD, // TODO, is 6 right????
                                               159);
        //MYPRINT("make_AMOV_p159_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_AMOV_p161_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineAD,
                                               161);
        //MYPRINT("make_AMOV_p161_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_AMOV_p162_Instr(String opcode, List<Operand> operandList, String syntax) {
        // TODO: P8 or D16 here
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineAD, // size 4 for D16
                                               162);
        //MYPRINT("make_AMOV_p162_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_AND_p165_Instr(String opcode, List<Operand> operandList, String syntax) {
        // AND src, dst
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               165);
        //MYPRINT("make_AND_p165_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_AND_p166_Instr(String opcode, List<Operand> operandList, String syntax) {
        // AND k8/k16, src, dst
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX, // TODO
                                               166);
        //MYPRINT("make_AND_p166_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_AND_p168_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               168);
        //MYPRINT("make_AND_p168_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_AND_p169_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX,
                                               169);
        //MYPRINT("make_AND_p169_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_AND_p170_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               170);
        //MYPRINT("make_AND_p170_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_AND_p171_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               171);
        //MYPRINT("make_AND_p171_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_AND_p172_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               172);
        //MYPRINT("make_AND_p172_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ASUB_p174_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineAD,
                                               174);
        //MYPRINT("make_ASUB_p174_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ASUB_p176_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineAD,
                                               176);
        //MYPRINT("make_ASUB_p176_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BAND_p181_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               181);
        //MYPRINT("make_B_p179_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BCLR_p192_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               192);
        //MYPRINT("make_BCLR_p192_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BCLR_p193_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               193);
        //MYPRINT("make_BCLR_p193_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BCLR_p194_No1234_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX, // TODO: ST3_55 in 5 cycles
                                               194);
        //MYPRINT("make_BCLR_p194_No1234_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BCLR_p194_No5_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX, // TODO: ST3_55 in 5 cycles
                                               1945);
        C55xBitOperand o = (C55xBitOperand)operandList.get(0);
        //MYPRINT("make_BCLR_p194_No5_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BCNT_p197_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX,
                                               197);
        //MYPRINT("make_BCNT_p197_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BFXPA_p198_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               198);
        //MYPRINT("make_BFXPA_p198_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BFXTR_p199_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               199);
        //MYPRINT("make_BFXTR_p199_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BNOT_p200_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               200);
        //MYPRINT("make_BNOT_p200_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BNOT_p201_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               201);
        //MYPRINT("make_BNOT_p201_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BSET_p202_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               202);
        //MYPRINT("make_BSET_p202_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BSET_p203_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               203);
        //MYPRINT("make_BSET_p203_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BSET_p204_No1234_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX, // TODO 5 cycles
                                               204);
        //MYPRINT("make_BSET_p204_No1234_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BSET_p204_No5_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX, // TODO 5 cycles
                                               2045);
        //MYPRINT("make_BSET_p204_No5_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BTST_p207_Instr(String opcode, List<Operand> operandList, String syntax) {
        // BTST Baddr, src, TC1
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               207);
        //MYPRINT("make_BTST_p207_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BTST_p210_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               210);
        //MYPRINT("make_BTST_p210_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BTST_p211_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               211);
        //MYPRINT("make_BTST_p211_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BTSTCLR_p212_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               212);
        //MYPRINT("make_BTSTCLR_p212_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BTSTNOT_p213_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               213);
        //MYPRINT("make_BTSTNOT_p213_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BTSTP_p214_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               214);
        //MYPRINT("make_BTSTP_p214_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BTSTSET_p216_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               216);
        //MYPRINT("make_BTSTSET_p216_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_CMP_p227_Instr(String opcode, List<Operand> operandList, String syntax) {
        // CMP Smem == K16, TC1
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               227);
        //MYPRINT("make_CMP_p227_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_CMP_p229_Instr(String opcode, List<Operand> operandList, String syntax) {
        // CMP src RELOP dst, TC1
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX,
                                               229);
        //MYPRINT("make_CMP_p229_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_CMPU_p229_Instr(String opcode, List<Operand> operandList, String syntax) {
        // CMPU src RELOP dst, TC1 , for 64 bit signed longs, this is same as CMP.
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX, // TODO: wtf????
                                               2292);
        //MYPRINT("make_CMPU_p229_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_CMPAND_p231_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX,
                                               231);
        //MYPRINT("make_CMPAND_p231_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_CMPOR_p236_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX,
                                               236);
        //MYPRINT("make_CMPOR_p236_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_DELAY_p242_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 2, 1, 0, 0, PipelineX,
                                               242);
        //MYPRINT("make_DELAY_p242_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_EXP_p243_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX,
                                               243);
        //MYPRINT("make_EXP_p243_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_FIRSADD_p244_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               244);
        //MYPRINT("make_FIRSADD_p244_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_FIRSSUB_p246_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               246);
        //MYPRINT("make_FIRSSUB_p246_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_IDLE_p248_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineD, // TODO: cycles = ?
                                               248);
        //MYPRINT("make_IDLE_p248_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_LMS_p251_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               251);
        //MYPRINT("make_LMS_p251_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MAC_p256_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               256);
        //MYPRINT("make_MAC_p256_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MAC_p257_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               257);
        //MYPRINT("make_MAC_p257_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MACK_p258_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX, // TODO: K8/K16 p.259
                                               258);
        //MYPRINT("make_MACK_p258_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MACM_p260_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               260);
        //MYPRINT("make_MACM_p260_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MACM_p262_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               262);
        //MYPRINT("make_MACM_p262_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MACM_p263_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               263);
        //MYPRINT("make_MACM_p263_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MACMK_p264_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               264);
        //MYPRINT("make_MACMK_p264_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MACM_p265_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               265);
        //MYPRINT("make_MACM_p265_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MACM_p267_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               267);
        //MYPRINT("make_MACM_p267_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MACMZ_p269_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               269);
        //MYPRINT("make_MACMZ_p269_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MAC_p272_Instr(
                                           String opcode, List<Object> objectList,
                                           String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               272);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                272);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_MAC_p272_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MAC_p274_Instr(
                                           String opcode, List<Object> objectList,
                                           String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               274);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                274);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_MAC_p274_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MAC_p276_Instr(
                                           String opcode, List<Object> objectList,
                                           String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               276);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                276);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_MAC_p276_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MAC_p278_Instr(
                                           String opcode, List<Object> objectList,
                                           String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               278);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                278);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_MAC_p278_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MACM_p281_Instr(
                                            String opcode, List<Object> objectList,
                                            String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               281);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                281);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_MACM_p281_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MACM_p283_Instr(
                                            String opcode, List<Object> objectList,
                                            String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               283);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                283);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_MACM_p283_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MANT_p285_Instr(
                                            String opcode, List<Object> objectList,
                                            String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               true, 3, 1, 0, 0, PipelineX2, // TODO: X2 is typo?
                                               285);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                true, 3, 1, 0, 0, PipelineX2, // TODO: X2 is typo?
                                                285);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_MANT_p285_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_NEXP_p285_Instr(
                                            String opcode, List<Object> objectList,
                                            String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               true, 3, 1, 0, 0, PipelineX2, // TODO: X2 is typo?
                                               2852);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                true, 3, 1, 0, 0, PipelineX2, // TODO: X2 is typo?
                                                2852);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_NEXP_p285_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MAS_p288_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               288);
        //MYPRINT("make_MAS_p288_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MASM_p290_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               290);
        //MYPRINT("make_MASM_p290_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MASM_p292_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               292);
        //MYPRINT("make_MASM_p292_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MASM_p293_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               293);
        //MYPRINT("make_MASM_p293_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MASM_p294_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               294);
        //MYPRINT("make_MASM_p294_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MAS_p297_Instr(
                                           String opcode, List<Object> objectList,
                                           String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               297);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                297);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_MAS_p297_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MAS_p299_Instr(
                                           String opcode, List<Object> objectList,
                                           String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               299);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                299);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_MAS_p299_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MAS_p301_Instr(
                                           String opcode, List<Object> objectList,
                                           String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               301);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                301);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_MAS_p301_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MAS_p304_Instr(
                                           String opcode, List<Object> objectList,
                                           String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               304);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                304);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_MAS_p304_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MASM_p307_Instr(
                                            String opcode, List<Object> objectList,
                                            String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               307);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                307);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_MASM_p307_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MASM_p309_Instr(
                                            String opcode, List<Object> objectList,
                                            String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               309);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                309);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_MASM_p309_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MAX_p311_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               311);
        //MYPRINT("make_MAX_p311_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }


    public C55xOperation make_MAXDIFF_p315_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX,
                                               315);
        //MYPRINT("make_MAXDIFF_p315_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_DMAXDIFF_p318_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX,
                                               318);
        //MYPRINT("make_DMAXDIFF_p318_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MIN_p320_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               320);
        //MYPRINT("make_MIN_p320_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MINDIFF_p324_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX,
                                               324);
        //MYPRINT("make_MINDIFF_p324_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_DMINDIFF_p327_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX,
                                               327);
        //MYPRINT("make_DMINDIFF_p327_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MMAP_p329_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 1, 1, 0, 0, PipelineD,
                                               329);
        //MYPRINT("make_MMAP_p329_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p332_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               332);
        //MYPRINT("make_MOV_p332_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p333_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               333);
        //MYPRINT("make_MOV_p333_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p334_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               334);
        //MYPRINT("make_MOV_p334_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p335_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 2, 1, 0, 0, PipelineX,
                                               335);
        //MYPRINT("make_MOV_p335_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p336_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               336);
        //MYPRINT("make_MOV_p336_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p337_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               337);
        //MYPRINT("make_MOV_p337_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p338_Instr(String opcode, List<Operand> operandList, String syntax) {
        // MOV[40] dbl(Lmem), ACx
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               338);
        //MYPRINT("make_MOV_p338_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV40_p338_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               33840);
        //MYPRINT("make_MOV40_p338_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p339_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               339);
        //MYPRINT("make_MOV_p339_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p341_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               341);
        //MYPRINT("make_MOV_p341_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p342_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               342);
        //MYPRINT("make_MOV_p342_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p344_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               344);
        //MYPRINT("make_MOV_p344_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p345_Instr(String opcode, List<Operand> operandList, String syntax) {
        // MOV K16 << #SHFT, ACx
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               345);
        //MYPRINT("make_MOV_p345_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p347_Instr(String opcode, List<Operand> operandList, String syntax) {
        // MOV Smem,dst
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 2, 1, 0, 0, PipelineX,
                                               347);
        //MYPRINT("make_MOV_p347_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p348_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               348);
        //MYPRINT("make_MOV_p348_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p350_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               350);
        //MYPRINT("make_MOV_p350_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p353_Instr(String opcode, List<Operand> operandList, String syntax) {
        // MOV k4,dst /-k4,dst / k16,dst
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX, // TODO: k4/K16
                                               353);
        //MYPRINT("make_MOV_p353_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p356_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               356);
        //MYPRINT("make_MOV_p356_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p359_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineAD, // TODO k12, K16 etc.
                                               359);
        //MYPRINT("make_MOV_p359_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p360_k7_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineAD,
                                               36007);
        //MYPRINT("make_MOV_p360_k7_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p360_k9_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineAD,
                                               36009);
        //MYPRINT("make_MOV_p360_k9_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p360_k12_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineAD,
                                               36012);
        //MYPRINT("make_MOV_p360_k12_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p360_k16_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineAD,
                                               36016);
        //MYPRINT("make_MOV_p360_k16_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p362_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               362);
        //MYPRINT("make_MOV_p362_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p363_Instr(String opcode, List<Operand> operandList, String syntax) {
        // MOV [k8/k16, Smem]
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX, // TODO: K8/K16
                                               363);
        //MYPRINT("make_MOV_p363_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p364_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               364);
        //MYPRINT("make_MOV_p364_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p365_Instr(String opcode, List<Operand> operandList, String syntax) {
        // MOV src,dst
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               365);
        //MYPRINT("make_MOV_p365_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p367_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               367);
        //MYPRINT("make_MOV_p367_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p368_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               368);
        //MYPRINT("make_MOV_p368_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p370_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               370);
        //MYPRINT("make_MOV_p370_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p372_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 2, 1, 0, 0, PipelineX,
                                               372);
        //MYPRINT("make_MOV_p372_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p374_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               374);
        //MYPRINT("make_MOV_p374_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p375_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               375);
        //MYPRINT("make_MOV_p375_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p376_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               376);
        //MYPRINT("make_MOV_p376_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p377_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               377);
        //MYPRINT("make_MOV_p377_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p378_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               378);
        //MYPRINT("make_MOV_p378_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p379_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               379);
        // MOV *AR5, *AR3
               //MYPRINT("make_MOV_p379_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p382_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 2, 1, 0, 0, PipelineX,
                                               382);
        //MYPRINT("make_MOV_p382_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p383_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               383);
        //MYPRINT("make_MOV_p383_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p384_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               384);
        //MYPRINT("make_MOV_p384_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p385_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               385);
        //MYPRINT("make_MOV_p385_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p386_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               386);
        //MYPRINT("make_MOV_p386_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p387_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               387);
        //MYPRINT("make_MOV_p387_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p388_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               388);
        //MYPRINT("make_MOV_p388_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p389_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               389);
        //MYPRINT("make_MOV_p389_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p391_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               391);
        //MYPRINT("make_MOV_p391_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p393_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               393);
        //MYPRINT("make_MOV_p393_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p395_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               395);
        //MYPRINT("make_MOV_p395_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p396_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               396);
        //MYPRINT("make_MOV_p396_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p398_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               398);
        //MYPRINT("make_MOV_p398_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p399_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               399);
        //MYPRINT("make_MOV_p399_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p401_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               401);
        //MYPRINT("make_MOV_p401_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p402_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               402);
        //MYPRINT("make_MOV_p402_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p404_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 2, 1, 0, 0, PipelineX,
                                               404);
        //MYPRINT("make_MOV_p404_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p405_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               405);
        //MYPRINT("make_MOV_p405_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p406_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               406);
               //MYPRINT("make_MOV_p406_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p407_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               407);
        //MYPRINT("make_MOV_p407_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p408_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               408);
        //MYPRINT("make_MOV_p408_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p412_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               412);
        //MYPRINT("make_MOV_p412_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p413_Instr(
                                           String opcode, List<Object> objectList,
                                           String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               413);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                413);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_MOV_p413_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MPY_p417_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               417);
        //MYPRINT("make_MPY_p417_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MPY_p418_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               418);
        //MYPRINT("make_MPY_p418_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MPYK_p420_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               420);
        //MYPRINT("make_MPYK_p420_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MPYM_p421_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               421);
        //MYPRINT("make_MPYM_p421_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MPYM_p423_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               423);
        // MPYM[R] [T3 = ]Smem, [ACx,] ACy
        //MYPRINT("make_MPYM_p423_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }


    public C55xOperation make_MPYMK_p424_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               424);
        //MYPRINT("make_MPYMK_p424_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }


    public C55xOperation make_MPYM_p425_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               425);
        //MYPRINT("make_MPYM_p425_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MPYM_p427_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               427);
        //MYPRINT("make_MPYM_p427_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MPY_p428_Instr(
                                           String opcode, List<Object> objectList,
                                           String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               428);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                428);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_MPY_p428_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MPY_p430_Instr(
                                           String opcode, List<Object> objectList,
                                           String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               430);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                430);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_MPY_p430_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MPYM_p432_Instr(
                                            String opcode, List<Object> objectList,
                                            String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               432);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                432);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_MPYM_p432_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    
    public C55xOperation make_NEG_p434_Instr(String opcode, List<Operand> operandList, String syntax) {
        // neg [src],dst
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               434);
        //MYPRINT("make_NEG_p434_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_NOP_p436_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 1, 1, 0, 0, PipelineD,
                                               436);
        //MYPRINT("make_NOP_p436_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_NOP16_p436_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineD,
                                               43616);
        //MYPRINT("make_NOP16_p436_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_NOT_p437_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               437);
        //MYPRINT("make_NOT_p437_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_OR_p439_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               439);
        //MYPRINT("make_OR_p439_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_OR_p440_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX,
                                               440);
        //MYPRINT("make_OR_p440_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_OR_p442_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               442);
        //MYPRINT("make_OR_p442_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_OR_p443_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX,
                                               443);
        //MYPRINT("make_OR_p443_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_OR_p444_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               444);
        //MYPRINT("make_OR_p444_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_OR_p445_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               445);
        //MYPRINT("make_OR_p445_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_OR_p446_Instr(String opcode, List<Operand> operandList, String syntax) {
        // or k16, Smem
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               446);
        //MYPRINT("make_OR_p446_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_POP_p448_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               448);
        
        //MYPRINT("make_POP_p448_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_POP_p449_Instr(String opcode, List<Operand> operandList, String syntax) {
        // POP dst
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               449);
        //MYPRINT("make_POP_p449_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_POP_p450_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               450);
        //MYPRINT("make_POP_p450_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }


    public C55xOperation make_POP_p451_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               451);
        //MYPRINT("make_POP_p451_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_POP_p452_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 2, 1, 0, 0, PipelineX,
                                               452);
        //MYPRINT("make_POP_p452_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_POP_p453_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 2, 1, 0, 0, PipelineX,
                                               453);
        //MYPRINT("make_POP_p453_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_POPBOTH_p454_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               454);
        //MYPRINT("make_POPBOTH_p454_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_PSH_p458_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               458);
        //MYPRINT("make_PSH_p458_Instr: " + instr + " (" + syntax + ")") ;
         return oper;
    }

    public C55xOperation make_PSH_p459_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               459);
        //MYPRINT("make_PSH_p459_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_PSH_p460_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               460);
        //MYPRINT("make_PSH_p460_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }


    public C55xOperation make_PSH_p461_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               461);
        //MYPRINT("make_PSH_p461_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_PSH_p462_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 2, 1, 0, 0, PipelineX,
                                               462);
        //MYPRINT("make_PSH_p462_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_PSH_p463_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 2, 1, 0, 0, PipelineX,
                                               463);
        //MYPRINT("make_PSH_p463_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_PSHBOTH_p464_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               464);
        //MYPRINT("make_PSHBOTH_p464_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ROL_p475_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX,
                                               475);
        //MYPRINT("make_ROL_p475_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }


    public C55xOperation make_ROR_p477_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX,
                                               477);
        //MYPRINT("make_ROR_p477_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_ROUND_p479_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               479);
        //MYPRINT("make_ROUND_p479_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_RPT_p482_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineAD, // TODO: k8|k16
                                               482);
        //MYPRINT("make_RPT_p482_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_RPT_p484_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineAD,
                                               484);
        //MYPRINT("make_RPT_p484_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_RPTADD_p487_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               487);
        //MYPRINT("make_RPTADD_p487_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_RPTADD_p488_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               488);
        //MYPRINT("make_RPTADD_p488_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_RPTBLOCAL_p490_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineAD,
                                               490);
        // This branch target is actually not a branch target, but
        // we will need this in C55x input files to find the block to repeat.
        C55xProgramAddressOperand o = (C55xProgramAddressOperand)operandList.get(0);
        String label = o.getLabel();
        Long offset = o.getOffset();
        oper.setBranchTarget(label, offset);
        //MYPRINT("make_RPTBLOCAL_p490_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_RPTB_p497_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineAD,
                                               497);
        // This branch target is actually not a branch target, but
        // we will need this in C55x input files to find the block to repeat.
        C55xProgramAddressOperand o = (C55xProgramAddressOperand)operandList.get(0);
        String label = o.getLabel();
        Long offset = o.getOffset();
        oper.setBranchTarget(label, offset);
        //MYPRINT("make_RPTB_p497_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }


    public C55xOperation make_RPTCC_p500_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineAD,
                                               500);
        //MYPRINT("make_RPTCC_p500_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_RPTSUB_p503_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               503);
        //MYPRINT("make_RPTSUB_p503_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SAT_p505_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               505);
        //MYPRINT("make_SAT_p505_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SFTCC_p507_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               507);
        //MYPRINT("make_SFTCC_p507_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SFTL_p510_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               510);
        //MYPRINT("make_SFTL_p510_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SFTL_p511_Instr(String opcode, List<Operand> operandList, String syntax) {
        // SFTL ACx, #SHIFTW[, ACy]
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX,
                                               511);
        //MYPRINT("make_SFTL_p511_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SFTL_p513_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               513);
        //MYPRINT("make_SFTL_p513_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SFTS_p516_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               516);
        //MYPRINT("make_SFTS_p516_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SFTSC_p518_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               518);
        //MYPRINT("make_SFTSC_p518_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SFTS_p520_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX,
                                               520);
        //MYPRINT("make_SFTS_p520_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SFTSC_p522_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX,
                                               522);
        //MYPRINT("make_SFTSC_p522_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SFTS_p525_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               525);
        //MYPRINT("make_SFTS_p525_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SQA_p530_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               530);
        //MYPRINT("make_SQA_p530_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SQAM_p531_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               531);
        //MYPRINT("make_SQAM_p531_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SQDST_p532_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               532);
        //MYPRINT("make_SQDST_p532_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SQR_p535_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               535);
        //MYPRINT("make_SQR_p535_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SQRM_p536_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               536);
        //MYPRINT("make_SQRM_p536_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }


    public C55xOperation make_SQS_p538_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               538);
        //MYPRINT("make_SQS_p538_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SQSM_p539_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               539);
        //MYPRINT("make_SQSM_p539_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p541_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               541);
        //MYPRINT("make_SUB_p541_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p543_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               543);
        //MYPRINT("make_SUB_p543_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p545_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               545);
        //MYPRINT("make_SUB_p545_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p547_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               547);
        //MYPRINT("make_SUB_p547_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p551_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               551);
        //MYPRINT("make_SUB_p551_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p552_Instr(String opcode, List<Operand> operandList, String syntax) {
        // SUB K16, [src,] dst
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX, // TODO: p.553 k4/K16
                                               552);
        //MYPRINT("make_SUB_p552_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p556_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               556);
        //MYPRINT("make_SUB_p556_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p558_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               558);
        //MYPRINT("make_SUB_p558_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p560_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               560);
        //MYPRINT("make_SUB_p560_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p561_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 1, 0, 0, PipelineX,
                                               561);
        //MYPRINT("make_SUB_p561_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p562_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               562);
        //MYPRINT("make_SUB_p562_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p563_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               563);
        //MYPRINT("make_SUB_p563_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p564_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               564);
        //MYPRINT("make_SUB_p564_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p565_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               565);
        //MYPRINT("make_SUB_p565_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p566_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               566);
        //MYPRINT("make_SUB_p566_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p567_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               567);
        //MYPRINT("make_SUB_p567_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p569_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               569);
        //MYPRINT("make_SUB_p569_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p570_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               570);
        //MYPRINT("make_SUB_p570_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p572_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               572);
        //MYPRINT("make_SUB_p572_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p573_Instr(String opcode, List<Operand> operandList, String syntax) {
        // SUB ACx, dbl(Lmem), ACy
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               573);
        //MYPRINT("make_SUB_p573_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p574_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               574);
        //MYPRINT("make_SUB_p574_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_p575_Instr(
                                           String opcode, List<Object> objectList,
                                           String syntax) {
        List<Operand> args = getOperandList(objectList, 0);
        String opcode2 = getOpcode(objectList, 1);
        List<Operand> args2 = getOperandList(objectList, 2);

        C55xOperation oper = new C55xOperation(opcode, args, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               575);
        C55xOperation oper2 = new C55xOperation(opcode2, args2, syntax,
                                                false, 4, 1, 0, 0, PipelineX,
                                                575);
        oper.setImplicitlyParallelOperation(oper2);
        //MYPRINT("make_SUB_p575_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUB_xxx_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               999999);
        //MYPRINT("make_SUB_xxx_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUBADD_p578_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               578);
        //MYPRINT("make_SUBADD_p578_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUBADD_p580_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               580);
        //MYPRINT("make_SUBADD_p580_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SUBC_p582_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               582);
        //MYPRINT("make_SUBC_p582_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SWAP_p585_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               585);
        //MYPRINT("make_SWAP_p585_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SWAPP_p590_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineAD, // TODO: p590 SWAPP AC0, AC2 X pipe???
                                               590);
        //MYPRINT("make_SWAPP_p590_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_SWAP4_p595_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineAD,
                                               595);
        //MYPRINT("make_SWAP4_p595_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    // CFG
    public C55xOperation make_XCC_p600_Instr(String opcode, List<Operand> operandList, String syntax) {

        // XCC [label], cond
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 2, 1, 0, 0, PipelineAD,
                                               600);
        // these seem always to only have a cond in disassembly
        //oper.setBranch(false); // false: not conditional
        //oper.setBranchTarget(); // branch target has to be found in a later pass!!!

        //MYPRINT("make_XCC_p600_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_XCCPART_p603_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 2, 1, 0, 0, PipelineX,
                                               603);
        //MYPRINT("make_XCCPART_p603_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_XOR_p607_Instr(String opcode, List<Operand> operandList, String syntax) {
        // XOR src,dst
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 1, 0, 0, PipelineX,
                                               607);
        //MYPRINT("make_XOR_p607_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_XOR_p608_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX, // TODO: k8/k16
                                               608);
        //MYPRINT("make_XOR_p608_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_XOR_p610_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 1, 0, 0, PipelineX,
                                               610);
        //MYPRINT("make_XOR_p610_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_XOR_p611_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX, // TODO: k8/k16
                                               611);
        //MYPRINT("make_XOR_p611_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_XOR_p612_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               612);
        //MYPRINT("make_XOR_p612_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_XOR_p613_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               613);
        //MYPRINT("make_XOR_p613_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_XOR_p614_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX,
                                               614);
        //MYPRINT("make_XOR_p614_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_UNKNOWN_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 1, 0, 0, PipelineX, // TODO: instr doesn't exist ...
                                               777777);
        //MYPRINT("make_UNKNOWN_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }


    ///////////////////////////////////////////////////////////////////////
    // Instructions taking more than 1 cycle to execute

    //
    // B
    //
    public C55xOperation make_B_p178_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 2, 10, 0, 0, PipelineX,
                                               178);
        // B ACx
        // 10, 0, 0 means: B is always counted as part of basic block
        // for energy consumption purposes
        // the branch edges must be ignored (that's why we have 0, 0)
        //oper.setBranch(false); // CFG TODO
        //MYPRINT("make_B_p178_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    // CFG
    public C55xOperation make_B_p179_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 3, 0, 0, PipelineAD, // TODO: L7/L16/P24
                                               179);
        // 3, 0, 0 means: B is always counted as part of basic block
        // TODO: it can also take 5 or 6 cycles
        // for energy consumption purposes
        // the branch edges must be ignored (that's why we have 0, 0)
        oper.setBranch(false); // false: not conditional
        C55xProgramAddressOperand o = (C55xProgramAddressOperand)operandList.get(0);
        String label = o.getLabel();
        Long offset = o.getOffset();
        oper.setBranchTarget(label, offset);
        return oper;
    }

    // CFG
    public C55xOperation make_BCC_p182_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 2, 0, 6, 5, PipelineR, // TODO: I4, L8, L16, P24
                                               182);
        // 0, 6, 5 means: BCC is always never counted as part of basic block
        // for energy consumption purposes
        // the branch edges must be added to energy consumption
        oper.setBranch(true);
        C55xProgramAddressOperand o = (C55xProgramAddressOperand)operandList.get(0);
        String label = o.getLabel();
        Long offset = o.getOffset();
        oper.setBranchTarget(label, offset);
        //MYPRINT("make_BCC_p182_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BCC_p186_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 0, 6, 5, PipelineAD, // TODO: 6/5 cycles
                                               186);
        // 0, 6, 5 means: BCC is always never counted as part of basic block
        // for energy consumption purposes
        // the branch edges must be added to energy consumption
        oper.setBranch(true); // CFG
        C55xProgramAddressOperand o = (C55xProgramAddressOperand)operandList.get(0);
        String label = o.getLabel();
        Long offset = o.getOffset();
        oper.setBranchTarget(label, offset);
        //MYPRINT("make_BCC_p182_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_BCC_p189_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 0, 7, 6, PipelineX, // TODO: 7/6 cycles
                                               189);
        // 0, 7, 6 means: BCC is always never counted as part of basic block
        // for energy consumption purposes
        // the branch edges must be added to energy consumption
        oper.setBranch(true); // CFG
        C55xProgramAddressOperand o = (C55xProgramAddressOperand)operandList.get(0);
        String label = o.getLabel();
        Long offset = o.getOffset();
        oper.setBranchTarget(label, offset);
        //MYPRINT("make_BCC_p189_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    // dynamic CFG : CALL ACx
    public C55xOperation make_CALL_p218_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 2, 10, 0, 0, PipelineX,
                                               218);
        // 10, 0, 0 means: CALL ACx is always counted as part of basic block
        // for energy consumption purposes
        // the branch edges must be ignored (that's why we have 0, 0)
        //oper.setCall(false); // CFG TODO
        //MYPRINT("make_CALL_p218_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    // CFG: CALL L16 / P24
    public C55xOperation make_CALL_p219_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 6, 0, 0, PipelineAD, // TODO: L16/P24
                                               219);
        // 10, 0, 0 means: CALL is always counted as part of basic block
        // for energy consumption purposes
        // the branch edges must be ignored (that's why we have 0, 0)
        oper.setCall(false);
        C55xProgramAddressOperand o = (C55xProgramAddressOperand)operandList.get(0);
        String label = o.getLabel();
        Long offset = o.getOffset();
        oper.setBranchTarget(label, offset);
        //MYPRINT("make_CALL_p219_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    // CFG
    public C55xOperation make_CALLCC_p223_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 4, 0, 6, 5, PipelineR, // TODO: L16/P24
                                               223);
        // 0, 6, 5 means: BCC is always never counted as part of basic block
        // for energy consumption purposes
        // the branch edges must be added to energy consumption
        oper.setCall(true);
        C55xProgramAddressOperand o = (C55xProgramAddressOperand)operandList.get(0);
        String label = o.getLabel();
        Long offset = o.getOffset();
        oper.setBranchTarget(label, offset);
        //MYPRINT("make_CALLCC_p223_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_INTR_p249_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 2, 3, 0, 0, PipelineD,
                                               249);
        //MYPRINT("make_INTR_p249_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p408_No20_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 5, 0, 0, PipelineX,
                                               40820);
        //MYPRINT("make_MOV_p408_No20_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_MOV_p359_No20_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 3, 5, 0, 0, PipelineX, // TODO ????
                                               35920);
        //MYPRINT("make_MOV_p359_No20_Instr: " + oper + " (" + syntax + ")") ;
        return oper;
    }

    public C55xOperation make_RESET_p465_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 2, 0, 0, 0, PipelineD, // TODO: cycles = ?
                                               465);
        //MYPRINT("make_RESET_p465_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }



    // CFG
    public C55xOperation make_RET_p469_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 5, 0, 0, PipelineD,
                                               469);
        // 5, 0, 0 means: RET is always counted as part of basic block
        // for energy consumption purposes
        // the RET edges must be ignored
        oper.setReturn(false);
        //MYPRINT("make_RET_p469_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }

    // CFG
    public C55xOperation make_RETCC_p471_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 3, 0, 5, 5, PipelineR,
                                               471);
        // 0, 5, 5 means: RETCC is always counted not as part of basic block
        // for energy consumption purposes
        // the RETCC edges must be counted as part of energy consumption
        oper.setReturn(true); // true: conditional

        //MYPRINT("make_RETCC_p471_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }


    public C55xOperation make_RETI_p473_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               true, 2, 5, 0, 0, PipelineD,
                                               473);
        //MYPRINT("make_RETI_p473_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }


    public C55xOperation make_TRAP_p597_Instr(String opcode, List<Operand> operandList, String syntax) {
        C55xOperation oper = new C55xOperation(opcode, operandList, syntax,
                                               false, 2, 0, 0, 0, PipelineAD, // TODO: cycles?
                                               597);
        //MYPRINT("make_TRAP_p597_Instr: " + instr + " (" + syntax + ")") ;
        return oper;
    }




}
