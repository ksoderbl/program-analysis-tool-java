
package microinstr;

import program.Program;
import machine.Machine;
import c55x.C55xMachine;
import machine.Register;

import c55x.instr.C55xMemoryAccessOperand;
import c55x.instr.C55xRegisterOperand;
import c55x.instr.C55xImmediateOperand;

public class C55xComputeSmem extends Microinstruction{
    
    C55xMemoryAccessOperand operand;
    int index = 0;

    public C55xComputeSmem(C55xMemoryAccessOperand op, int index){
        super("C55xComputeSmem");
        this.operand = op;
        this.index = index;
    }
    
    public void execute(Program program, Machine machine){
        boolean debug = program.getOptions().getDebugMC();
        C55xMachine c55x = (C55xMachine)machine;
        C55xRegisterOperand regOp1 = operand.getRegOp1();
        C55xRegisterOperand regOp2 = operand.getRegOp2();
        C55xImmediateOperand immediate = operand.getImmediateOp();
        int operation = operand.getAddressingMode();
        Register addReg1 = operand.getFirstReg(machine); 
        Register addReg2 = operand.getSecondReg(machine); 

        long immediateValue = 0;
        int memoryAccessMode = operand.getMemoryAccessMode();
        long memOffset = 1;

        if (memoryAccessMode == C55xMemoryAccessOperand.Lmem)
            memOffset = 2;
        machine.setMMREG(null); // clear previous Smem access if it was an MMREG

        // possibly use circular addressing
        if (addReg1 != null
            && c55x.isRegisterWithCircularAddressing(addReg1)
            && c55x.getBufferSize(addReg1) > 0) {
            long bufferStartAddress = c55x.getBufferStartAddress(addReg1);
            long bufferSize = c55x.getBufferSize(addReg1);
            long offs, addr;

            if (debug)
            System.out.print(" C55xComputeSmem: circular addressing:"
                             + " reg:" + addReg1.getName()
                             + " start:" + Long.toHexString(bufferStartAddress)
                             + " offs:" + addReg1.getValue()
                             + " size:" + Long.toHexString(bufferSize)
                             + " op:" + operation
                             );

            offs = addReg1.getValue();
            if (offs < 0)
                throw new NullPointerException("C55xComputeSmem:"
                                               +"circular addressing offs < 0:" + offs);
            if (offs >= bufferSize)
                throw new NullPointerException("C55xComputeSmem:"
                +"circular addressing offs >= bs:" + offs + ">=" + bufferSize);

            switch (operation) { // documented on p. 49 of SPRU374G

                // works: used in dsplib fir
            case C55xMemoryAccessOperand.NOTMOD: // e.g. *ar0
                addr = bufferStartAddress + offs;
                break;

                // works: used in dsplib fir
            case C55xMemoryAccessOperand.POSTINC: // e.g. *ar0+
                addr = bufferStartAddress + offs;
                offs += memOffset;
                while (offs >= bufferSize)
                    offs -= bufferSize;
                addReg1.setValue(offs);
                break;

                // works: used in dsplib fir2
            case C55xMemoryAccessOperand.POSTDEC: // e.g. *ar0-
                addr = bufferStartAddress + offs;
                offs -= memOffset;
                while (offs < 0)
                    offs += bufferSize;
                addReg1.setValue(offs);
                break;

                // used in dsplib mtrans
            case C55xMemoryAccessOperand.PLUSREGOFFSET: // e.g. *(ar0+t0)
                addr = bufferStartAddress + offs;
                offs += addReg2.getValue();
                while (offs < 0)
                    offs += bufferSize;
                while (offs >= bufferSize)
                    offs -= bufferSize;
                addReg1.setValue(offs);
                break;

                // works: used in dsplib fir
            case C55xMemoryAccessOperand.MINUSREGOFFSET: // mode 6, e.g. *(ar0-t0)
                addr = bufferStartAddress + offs; 
                offs -= addReg2.getValue();
                while (offs < 0)
                    offs += bufferSize;
                while (offs >= bufferSize)
                    offs -= bufferSize;
                addReg1.setValue(offs);
                break;

                // used in dsplib iir4
            case C55xMemoryAccessOperand.REGOFFSET: // mode 7, e.g. *ar0(t0)
                offs += addReg2.getValue();
                while (offs < 0)
                    offs += bufferSize;
                while (offs >= bufferSize)
                    offs -= bufferSize;
                addr = bufferStartAddress + offs;
                break;

                // used in dsplib iir4
            case C55xMemoryAccessOperand.PREINC_IMMOFFSET: // mode 9, e.g. *+ar2(#0013h)
                offs += immediate.getValue();
                while (offs < 0)
                    offs += bufferSize;
                while (offs >= bufferSize)
                    offs -= bufferSize;
                addr = bufferStartAddress + offs;
                addReg1.setValue(offs);
                break;
            default:
                throw new NullPointerException("C55xComputeSmem:"
                                               +"circular addressing mode not implemented:"
                                               +operation);
            }
            if (debug)
            System.out.println(" addr:"+Long.toHexString(addr));
            machine.setAddressResult(addr, index);
            return;
        }

        // linear addressing
               switch (operation){ 

        case C55xMemoryAccessOperand.NOTMOD: // e.g. *cdp
            machine.setAddressResult(addReg1.getValue(), index);
            break;
        case C55xMemoryAccessOperand.PREINC: // e.g. *+cdp
            addReg1.setValue(addReg1.getValue()+memOffset);
            machine.setAddressResult(addReg1.getValue(), index);
            break;
        case C55xMemoryAccessOperand.PREDEC: // e.g. *-cdp
            addReg1.setValue(addReg1.getValue()-memOffset);
            machine.setAddressResult(addReg1.getValue(), index);
            break;
        case C55xMemoryAccessOperand.POSTDEC: // e.g. *cdp-
            machine.setAddressResult(addReg1.getValue(), index);
            addReg1.setValue(addReg1.getValue() - memOffset);
            break;
        case C55xMemoryAccessOperand.POSTINC: // e.g. *cdp+
            machine.setAddressResult(addReg1.getValue(), index);
            addReg1.setValue(addReg1.getValue() + memOffset);
            break;
        case C55xMemoryAccessOperand.REGOFFSET: // e.g. *ar0(t0)
            machine.setAddressResult(addReg1.getValue()+addReg2.getValue(), index);
            break;

        case C55xMemoryAccessOperand.PLUSREGOFFSET: // e.g. *(cdp+t0)
            machine.setAddressResult(addReg1.getValue(), index);
            addReg1.setValue(addReg1.getValue() + addReg2.getValue());
            break;

        case C55xMemoryAccessOperand.MINUSREGOFFSET: // e.g. *(ar0-t0)
            // documented on p. 49 of SPRU374G
            machine.setAddressResult(addReg1.getValue(), index);
            addReg1.setValue(addReg1.getValue() - addReg2.getValue());
            break;

        case C55xMemoryAccessOperand.PREINC_IMMOFFSET: // e.g. *+ar2(#0013h)
            addReg1.setValue(addReg1.getValue()+immediate.getValue());
            machine.setAddressResult(addReg1.getValue(), index);

            break;
        case C55xMemoryAccessOperand.INDIRECT:            // e.g. *(#00000h)

            machine.setAddressResult(immediate.getValue(), index);

            break;
        case C55xMemoryAccessOperand.DMA: // e.g. @#01h
            immediateValue = immediate.getValue();
            Register stack = machine.getRegister("sp");
            
            machine.setAddressResult(immediateValue + stack.getValue(), index);
            break;

        case C55xMemoryAccessOperand.MMREG: // e.g. mmap(@ST3_55)
            machine.setMMREG(addReg1);
            // The MMAP is special kludge of c55x; just write MMAPRegister into a temp storage place, 
            // to be accessed later
            break; 
        case C55xMemoryAccessOperand.IMMOFFSET: // e.g. *ar0(#0010h)
            // e.g. *ar0(#0010h)
            machine.setAddressResult(immediate.getValue()+addReg1.getValue(), index);
            break;
        case C55xMemoryAccessOperand.ABS16: // e.g. *abs(#00000h)
            immediateValue = immediate.getValue();
            machine.setAddressResult(immediateValue, index);
            break;

        default:
            throw new NullPointerException("C55xComputeSmem:addressing mode not implemented:"+operation);
        }

        String regs = "";
        if (addReg1 != null) regs +=" reg1:"+addReg1.getName()
                                 +"("+Long.toHexString(addReg1.getValue())+")";
        if (addReg2 != null) regs +=" reg2:"+addReg2.getName()
                                 +"("+Long.toHexString(addReg2.getValue())+")";
        if (debug) {
            if (operation != C55xMemoryAccessOperand.MMREG) {
                long addr = machine.getAddressResult(index).getValue();
                System.out.println(" C55xComputeSmem: busresult:0x"+Long.toHexString(addr)
                                   + regs);
            }
            else {// mmreg
                System.out.println(" C55xComputeSmem: using " + addReg1.getName()
                                   + " as MMREG");
            }
        }
        //if (operation == C55xMemoryAccessOperand.NOTMOD )  address.setValue(address.getValue()+offset);
        //else machine.setBusResult(address.getValue());
        
    }
    
}
