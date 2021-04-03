
package c55x.instr;

import java.util.List;
import java.util.ListIterator;
import java.util.ArrayList;
import java.util.Iterator;

import instr.Operation;
import machine.*;
import c55x.*;
import c55x.instr.*;
import microinstr.*;
import instr.Operand;
import main.*;

public class C55xOperation extends Operation
{
    C55xOperation next;
    int page; // page in SPRU374G
    List<Microinstruction> microinstrs;
    int cycles_in_basic_block;
    int cycles_cond_true;
    int cycles_cond_false;


    // C55x instruction classes according to paper
    // Power Consumption Characterisation of the Texas Instruments
    // TMS320VC5510 DSP, Table 2
    public static final int UnknownOp        = 0;  // not assigned to class
    public static final int ArithmeticalOp   = 1;
    public static final int BitOp            = 2;
    public static final int LogicalOp        = 3;
    public static final int MoveOp           = 4;

    int instruction_class = UnknownOp;

    public int getInstructionClass() {
        return instruction_class;
    }

    public int getPageNumber(){
        return page;
    }

    //public C55xOperation(String mnemonic, List<Operand> args, String syntax, int page) {
    //        super(mnemonic, args, syntax);
    //        this.page = page;
    //}

    // C55x instruction classes according to paper
    // Power Consumption Characterisation of the Texas Instruments
    // TMS320VC5510 DSP, Table 2
    private int findInstructionClass(String mnemonic, List<Operand> args, int page) {
        int cl = UnknownOp; // instruction class

        // java main/Main -E -v -Oc -march=c55x c55x/test/example.dis

        // arithmetic ops
        if (mnemonic.equals("AADD")) cl = ArithmeticalOp;
        if (mnemonic.equals("ABS")) cl = ArithmeticalOp;
        if (mnemonic.length() >= 3 && mnemonic.substring(0, 3).equals("ADD")) cl = ArithmeticalOp;
        if (mnemonic.equals("AMAR")) cl = ArithmeticalOp;
        if (mnemonic.equals("ASUB")) cl = ArithmeticalOp;
        if (mnemonic.equals("ABDST")) cl = ArithmeticalOp;
        if (mnemonic.equals("EXP")) cl = ArithmeticalOp;
        if (mnemonic.equals("FIRSADD")) cl = ArithmeticalOp;
        if (mnemonic.equals("FIRSSUB")) cl = ArithmeticalOp;
        if (mnemonic.equals("LMS")) cl = ArithmeticalOp;
        if (mnemonic.length() >= 3 && mnemonic.substring(0, 3).equals("MAC")) cl = ArithmeticalOp;
        if (mnemonic.length() >= 3 && mnemonic.substring(0, 3).equals("MAS")) cl = ArithmeticalOp;
        if (mnemonic.length() >= 3 && mnemonic.substring(0, 3).equals("MPY")) cl = ArithmeticalOp;
        if (mnemonic.equals("MANT")) cl = ArithmeticalOp;
        if (mnemonic.equals("NEXP")) cl = ArithmeticalOp;
        if (mnemonic.equals("NEG")) cl = ArithmeticalOp;
        if (mnemonic.equals("ROUND")) cl = ArithmeticalOp;
        if (mnemonic.length() >= 3 && mnemonic.substring(0, 3).equals("SAT")) cl = ArithmeticalOp;
        if (mnemonic.length() >= 2 && mnemonic.substring(0, 2).equals("SQ")) cl = ArithmeticalOp;
        if (mnemonic.length() >= 3 && mnemonic.substring(0, 3).equals("SUB")) cl = ArithmeticalOp;


        // bit ops
        if (mnemonic.equals("BAND")) cl = BitOp;
        if (mnemonic.equals("BCLR")) cl = BitOp;
        if (mnemonic.equals("BCNT")) cl = BitOp;
        if (mnemonic.equals("BNOT")) cl = BitOp;
        if (mnemonic.equals("BSET")) cl = BitOp;
        if (mnemonic.length() >= 4 && mnemonic.substring(0, 4).equals("BTST")) cl = BitOp;
        if (mnemonic.equals("BFXPA")) cl = BitOp;
        if (mnemonic.equals("BFXTR")) cl = BitOp;
        if (mnemonic.equals("AND")) cl = BitOp;
        if (mnemonic.equals("NOT")) cl = BitOp;
        if (mnemonic.equals("OR"))  cl = BitOp;
        if (mnemonic.equals("ROR")) cl = BitOp; // is bit op ???
        if (mnemonic.equals("ROL")) cl = BitOp; // is bit op ???
        if (mnemonic.equals("XOR")) cl = BitOp;
        if (mnemonic.length() >= 3 && mnemonic.substring(0, 3).equals("SFT")) cl = BitOp; //shift

        // logical ops - no idea what these are :P
        if (mnemonic.length() >= 3 && mnemonic.substring(0, 3).equals("CMP")) cl = LogicalOp;
        if (mnemonic.length() >= 3 && mnemonic.substring(0, 3).equals("MAX")) cl = LogicalOp;
        if (mnemonic.length() >= 3 && mnemonic.substring(0, 3).equals("MIN")) cl = LogicalOp;
        if (mnemonic.equals("DMAXDIFF")) cl = LogicalOp;
        if (mnemonic.equals("DMINDIFF")) cl = LogicalOp;
        
        // assign these to logical even though I don't know where they should be
        if (mnemonic.equals("B")) cl = LogicalOp;
        if (mnemonic.equals("BCC")) cl = LogicalOp;
        if (mnemonic.equals("BCCU")) cl = LogicalOp;
        if (mnemonic.equals("CALL")) cl = LogicalOp;
        if (mnemonic.equals("CALCC")) cl = LogicalOp; // type in dis55, should be CALLCC
        if (mnemonic.equals("DELAY")) cl = LogicalOp;
        if (mnemonic.equals("IDLE")) cl = LogicalOp;
        if (mnemonic.equals("INTR")) cl = LogicalOp;
        if (mnemonic.equals("NOP")) cl = LogicalOp;
        if (mnemonic.equals("NOP_16")) cl = LogicalOp;
        if (mnemonic.equals("RESET")) cl = LogicalOp;
        if (mnemonic.equals("RET")) cl = LogicalOp;
        if (mnemonic.equals("RETCC")) cl = LogicalOp;
        if (mnemonic.length() >= 3 && mnemonic.substring(0, 3).equals("RPT")) cl = LogicalOp;
        if (mnemonic.equals("SIM_TRIG")) cl = LogicalOp;
        if (mnemonic.equals("TRAP")) cl = LogicalOp;
        if (mnemonic.equals("XCC")) cl = LogicalOp;
        if (mnemonic.equals("XCCPART")) cl = LogicalOp;

        // move ops
        if (mnemonic.equals("AMOV")) cl = MoveOp;
        if (mnemonic.equals("MOV")) cl = MoveOp;
        if (mnemonic.equals("MOV4")) cl = MoveOp;
        if (mnemonic.equals("SWAP")) cl = MoveOp;
        if (mnemonic.equals("PSH")) cl = MoveOp;
        if (mnemonic.equals("POP")) cl = MoveOp;
        if (mnemonic.equals("PSHBOTH")) cl = MoveOp;
        if (mnemonic.equals("POPBOTH")) cl = MoveOp;

        if (cl == UnknownOp) {
            System.out.println("*** C55xOperation.java: findInstructionClass needs this:");
            System.out.println("*** mnemonic : " + mnemonic);
            System.out.println("*** args     : " + args);
            System.out.println("*** page     : " + page);
            System.out.println("*** memaccess: " + this.hasMemoryAccess());
            System.out.println("=========================");
        } else {
            /*System.out.println("mnemonic : " + mnemonic);
            System.out.println("args     : " + args);

            if (cl == ArithmeticalOp && !hasMemoryAccess())
                System.out.println("class    : ArithmeticalOpRR");
            if (cl == BitOp && !hasMemoryAccess())
                System.out.println("class    : BitOpRR");
            if (cl == LogicalOp && !hasMemoryAccess())
                System.out.println("class    : LogicalOpRR");
            if (cl == MoveOp && !hasMemoryAccess())
                System.out.println("class    : MoveOpRR");
            if (cl == ArithmeticalOp && hasMemoryAccess())
                System.out.println("class    : ArithmeticalOpMR");
            if (cl == BitOp && hasMemoryAccess())
                System.out.println("class    : BitOpMR");
            if (cl == LogicalOp && hasMemoryAccess())
                System.out.println("class    : LogicalOpMR");
            if (cl == MoveOp && hasMemoryAccess())
                System.out.println("class    : MoveOpMR");

            System.out.println("class    : " + cl);
            System.out.println("memaccess: " + hasMemoryAccess());
            System.out.println("=========================")*/;
        }
        
        return cl;
    }


    public String emitArgs() {
        List<Operand> args = this.getArgs();
        if (args == null)
            return "";
        
        int numargs = 0;
        String argstring = "";
        ListIterator<Operand> liter = args.listIterator();
        
        while (liter.hasNext()) {
            if (numargs > 0)
                argstring += ",";
            Object next = liter.next();
            Operand op = null;
            if (next instanceof Operand) {
                op = (Operand)next;
            }
            else {
                Main.warn("BUG: operand is not Operand: '"+next+"'");
            }
            argstring += op.toString();
            numargs++;
        }
        if (!argstring.equals(""))
            argstring = " " + argstring;
        
        return argstring;
    }

    /**
     * Function for emitting the operation.
     *
     * @return Operation as formatted string to emit
     */
    public String emit() {
        return this.getMnemonic() + this.emitArgs();
    }

    public String toString() {
        return this.emit();
    }

    public int getCycles() {
        return cycles_in_basic_block;
    }
    public int getCycles(boolean branchtaken) {
        if (branchtaken)
            return cycles_cond_true;
        else
            return cycles_cond_false;
    }


    public C55xOperation(String mnemonic, List<Operand> args, String syntax, 
                         boolean parallel_enable,
                         int size,
                         int cycles_in_basic_block,
                         int cycles_cond_true,
                         int cycles_cond_false,
                         int pipeline,
                         int page) {
        super(mnemonic, args, syntax);
        //System.out.println("Operation " + mnemonic + " " + args);
        //System.out.println("   cycles: " + cycles);
        this.cycles_in_basic_block = cycles_in_basic_block;
        this.cycles_cond_true = cycles_cond_true;  //conditional branch edge
        this.cycles_cond_false = cycles_cond_false; // branch not taken => falltrough

        this.page = page;
        this.instruction_class = findInstructionClass(mnemonic, args, page);
    }

    // for first operation in MAS::MAC
    public void setImplicitlyParallelOperation(C55xOperation op) {
        this.next = op;
        op.setIsImplicitlyParallel();
    }
    public C55xOperation getImplicitlyParallelOperation() {
        return next;
    }
    // for second operation in MAS::MAC
    private boolean isImplicitlyParallel = false;
    public void setIsImplicitlyParallel() {
        //System.out.println("is implicitly parallel: " + this);
        this.isImplicitlyParallel = true;
    }


    public int getPage() {
        return page;
    }

    
    public void addMicroinstr(Microinstruction mi) {
        // don't return any microinstrs for MAC in MAS::MAC
        if (this.isImplicitlyParallel) {
            //System.out.println("not returning microinstrs for " + this);
            return;
        }
        microinstrs.add(mi);
    }
    
    public List<Microinstruction> getMicroinstrList(){
        // don't return any microinstrs for MAC in MAS::MAC
        if (true)
            throw new NullPointerException("getMicroinstrList for " + this);
        if (this.isImplicitlyParallel) {
            //System.out.println("not returning microinstrs for " + this);
            return null;
        }
        return microinstrs;
    }    


    public List<Microinstruction> getMicroinstrs(Machine machine) {

        //System.out.println("  getMicroinstrs for " + this);
        //if (this.getImplicitlyParallelOperation() != null) {
        //    System.out.println("  has parallel op " + this.getImplicitlyParallelOperation());
        //}

        // don't return any microinstrs for MAC in MAS::MAC
        if (this.isImplicitlyParallel) {
            System.out.println("  not returning microinstrs for " + this);
            return null;
        }

        if (microinstrs == null) {
            //System.out.println("  microinstrs null for " + this);
            microinstrs = new ArrayList<Microinstruction>();
            makeMicroinstrs(machine);
        }
        return microinstrs;
    }

    public void checkImplicitParallelism() {
        C55xOperation p = getImplicitlyParallelOperation();
        //System.out.println("checking impl par for " + this);
        if (p != null) {
            System.out.println("" + this + " has parallel operation " + p);
            throw new NullPointerException("C55xOperation fix impl. parallelism: " + this);
        }
        if (this.isImplicitlyParallel) {
            System.out.println("" + this + " is parallel operation");
            throw new NullPointerException("C55xOperation fix impl. parallelism: " + this);
        }
    }

    private void makeMicroinstrs(Machine machine) {

        //System.out.println("  makeMicroinstrs for " + this);

        switch(page) {

        case 95: make_AADD_p95_Instr(machine); return;
        case 97: make_AADD_p97_Instr(machine); return;
        case 98: make_AADD_p98_Instr(machine); return;
        case 99: make_ABDST_p99_Instr(machine); return;
        case 101: make_ABS_p101_Instr(machine); return;
        case 106: make_ADD_p106_Instr(machine); return;
        case 107: make_ADD_p107_Instr(machine); return;
        case 110: make_ADD_p110_Instr(machine); return;
        case 112: make_ADD_p112_Instr(machine); return;
        case 113: make_ADD_p113_Instr(machine); return;
        case 114: make_ADD_p114_Instr(machine); return;
        case 115: make_ADD_p115_Instr(machine); return;
        case 116: make_ADD_p116_Instr(machine); return;
            //case 118: make_ADD_p118_Instr(machine); return;
        case 119: make_ADD_p119_Instr(machine); return;
        case 120: make_ADD_p120_Instr(machine); return;
        case 121: make_ADD_p121_Instr(machine); return;
        case 122: make_ADD_p122_Instr(machine); return;
        case 123: make_ADD_p123_Instr(machine); return;
        case 124: make_ADD_p124_Instr(machine); return;
        case 126: make_ADD_p126_Instr(machine); return;
        case 128: make_ADD_p128_Instr(machine); return;
        case 130: make_ADD_p130_Instr(machine); return;

        case 133: make_ADDSUB_p133_Instr(machine); return;
        case 135: make_ADDSUB_p135_Instr(machine); return;
        case 137: make_ADDSUBCC_p137_Instr(machine); return;
        case 139: make_ADDSUBCC_p139_Instr(machine); return;
        case 141: make_ADDSUB2CC_p141_Instr(machine); return;
        case 144: make_ADDV_p144_Instr(machine); return;
        case 146: make_AMAR_p146_Instr(machine); return;
        case 148: make_AMAR_p148_Instr(machine); return;
        case 149: make_AMAR_p149_Instr(machine); return;
        case 151: make_AMAR_p151_Instr(machine); return;
        case 153: make_AMAR_p153_Instr(machine); return;
        case 155: make_AMAR_p155_Instr(machine); return;
        case 157: make_AMAR_p157_Instr(machine); return;
        case 159: make_AMOV_p159_Instr(machine); return;
        case 161: make_AMOV_p161_Instr(machine); return;
        case 162: make_AMOV_p162_Instr(machine); return;
        case 165: make_AND_p165_Instr(machine); return;
        case 166: make_AND_p166_Instr(machine); return;
        case 168: make_AND_p168_Instr(machine); return;
        case 169: make_AND_p169_Instr(machine); return;
        case 170: make_AND_p170_Instr(machine); return;
        case 171: make_AND_p171_Instr(machine); return;
        case 172: make_AND_p172_Instr(machine); return;
        case 174: make_ASUB_p174_Instr(machine); return;
        case 176: make_ASUB_p176_Instr(machine); return;
        case 178: make_B_p178_Instr(machine); return;
        case 179: make_B_p179_Instr(machine); return;
        case 181: make_BAND_p181_Instr(machine); return;
        case 182: make_BCC_p182_Instr(machine); return;
        case 186: make_BCC_p186_Instr(machine); return;
        case 189: make_BCC_p189_Instr(machine); return;
        case 192: make_BCLR_p192_Instr(machine); return;
        case 193: make_BCLR_p193_Instr(machine); return;
        case 194: make_BCLR_p194_No1234_Instr(machine); return;
        case 1945: make_BCLR_p194_No5_Instr(machine); return;
        case 197: make_BCNT_p197_Instr(machine); return;
        case 198: make_BFXPA_p198_Instr(machine); return;
        case 199: make_BFXTR_p199_Instr(machine); return;
        case 200: make_BNOT_p200_Instr(machine); return;
        case 201: make_BNOT_p201_Instr(machine); return;
        case 202: make_BSET_p202_Instr(machine); return;
        case 203: make_BSET_p203_Instr(machine); return;
        case 204: make_BSET_p204_No1234_Instr(machine); return;
        case 2045: make_BSET_p204_No5_Instr(machine); return;
        case 207: make_BTST_p207_Instr(machine); return;
        case 210: make_BTST_p210_Instr(machine); return;
        case 211: make_BTST_p211_Instr(machine); return;
        case 212: make_BTSTCLR_p212_Instr(machine); return;
        case 213: make_BTSTNOT_p213_Instr(machine); return;
        case 214: make_BTSTP_p214_Instr(machine); return;
        case 216: make_BTSTSET_p216_Instr(machine); return;
        case 218: make_CALL_p218_Instr(machine); return;
        case 219: make_CALL_p219_Instr(machine); return;
        case 223: make_CALLCC_p223_Instr(machine); return;
        case 227: make_CMP_p227_Instr(machine); return;
        case 229: make_CMP_p229_Instr(machine); return;
        case 2292: make_CMPU_p229_Instr(machine); return;
        case 231: make_CMPAND_p231_Instr(machine); return;
        case 236: make_CMPOR_p236_Instr(machine); return;
        case 242: make_DELAY_p242_Instr(machine); return;
        case 243: make_EXP_p243_Instr(machine); return;
        case 244: make_FIRSADD_p244_Instr(machine); return;
        case 246: make_FIRSSUB_p246_Instr(machine); return;
        case 248: make_IDLE_p248_Instr(machine); return;
        case 249: make_INTR_p249_Instr(machine); return;
        case 251: make_LMS_p251_Instr(machine); return;
        case 256: make_MAC_p256_Instr(machine); return;
        case 257: make_MAC_p257_Instr(machine); return;
        case 258: make_MACK_p258_Instr(machine); return;
        case 260: make_MACM_p260_Instr(machine); return;
        case 262: make_MACM_p262_Instr(machine); return;
        case 263: make_MACM_p263_Instr(machine); return;
        case 264: make_MACMK_p264_Instr(machine); return;
        case 265: make_MACM_p265_Instr(machine); return;
        case 267: make_MACM_p267_Instr(machine); return;
        case 269: make_MACMZ_p269_Instr(machine); return;
        case 272: make_MAC_p272_Instr(machine); return;
        case 274: make_MAC_p274_Instr(machine); return;
        case 276: make_MAC_p276_Instr(machine); return;
        case 278: make_MAC_p278_Instr(machine); return;
        case 281: make_MACM_p281_Instr(machine); return;
        case 283: make_MACM_p283_Instr(machine); return;
        case 285: make_MANT_p285_Instr(machine); return;
        case 2852: make_NEXP_p285_Instr(machine); return;
        case 288: make_MAS_p288_Instr(machine); return;
        case 290: make_MASM_p290_Instr(machine); return;
        case 292: make_MASM_p292_Instr(machine); return;
        case 293: make_MASM_p293_Instr(machine); return;
        case 294: make_MASM_p294_Instr(machine); return;
        case 297: make_MAS_p297_Instr(machine); return;
        case 299: make_MAS_p299_Instr(machine); return;
        case 301: make_MAS_p301_Instr(machine); return;
        case 304: make_MAS_p304_Instr(machine); return;
        case 307: make_MASM_p307_Instr(machine); return;
        case 309: make_MASM_p309_Instr(machine); return;
        case 311: make_MAX_p311_Instr(machine); return;
        case 315: make_MAXDIFF_p315_Instr(machine); return;
        case 318: make_DMAXDIFF_p318_Instr(machine); return;
        case 320: make_MIN_p320_Instr(machine); return;
        case 324: make_MINDIFF_p324_Instr(machine); return;
        case 327: make_DMINDIFF_p327_Instr(machine); return;
        case 329: make_MMAP_p329_Instr(machine); return;
        case 332: make_MOV_p332_Instr(machine); return;
        case 333: make_MOV_p333_Instr(machine); return;
        case 334: make_MOV_p334_Instr(machine); return;
        case 335: make_MOV_p335_Instr(machine); return;
        case 336: make_MOV_p336_Instr(machine); return;
        case 337: make_MOV_p337_Instr(machine); return;
        case 338: make_MOV_p338_Instr(machine); return;
        case 33840: make_MOV40_p338_Instr(machine); return;
        case 339: make_MOV_p339_Instr(machine); return;
        case 341: make_MOV_p341_Instr(machine); return;
        case 342: make_MOV_p342_Instr(machine); return;
        case 344: make_MOV_p344_Instr(machine); return;
        case 345: make_MOV_p345_Instr(machine); return;
        case 347: make_MOV_p347_Instr(machine); return;
        case 348: make_MOV_p348_Instr(machine); return;
        case 350: make_MOV_p350_Instr(machine); return;
        case 353: make_MOV_p353_Instr(machine); return;
        case 356: make_MOV_p356_Instr(machine); return;
        case 359: make_MOV_p359_Instr(machine); return;
        case 35920: make_MOV_p359_No20_Instr(machine); return;
        case 36007: make_MOV_p360_k7_Instr(machine); return;
        case 36009: make_MOV_p360_k9_Instr(machine); return;
        case 36012: make_MOV_p360_k12_Instr(machine); return;
        case 36016: make_MOV_p360_k16_Instr(machine); return;
        case 362: make_MOV_p362_Instr(machine); return;
        case 363: make_MOV_p363_Instr(machine); return;
        case 364: make_MOV_p364_Instr(machine); return;
        case 365: make_MOV_p365_Instr(machine); return;
        case 367: make_MOV_p367_Instr(machine); return;
        case 368: make_MOV_p368_Instr(machine); return;
        case 370: make_MOV_p370_Instr(machine); return;
        case 372: make_MOV_p372_Instr(machine); return;
        case 374: make_MOV_p374_Instr(machine); return;
        case 375: make_MOV_p375_Instr(machine); return;
        case 376: make_MOV_p376_Instr(machine); return;
        case 377: make_MOV_p377_Instr(machine); return;
        case 378: make_MOV_p378_Instr(machine); return;
        case 379: make_MOV_p379_Instr(machine); return;
        case 382: make_MOV_p382_Instr(machine); return;
        case 383: make_MOV_p383_Instr(machine); return;
        case 384: make_MOV_p384_Instr(machine); return;
        case 385: make_MOV_p385_Instr(machine); return;
        case 386: make_MOV_p386_Instr(machine); return;
        case 387: make_MOV_p387_Instr(machine); return;
        case 388: make_MOV_p388_Instr(machine); return;
        case 389: make_MOV_p389_Instr(machine); return;
        case 391: make_MOV_p391_Instr(machine); return;
        case 393: make_MOV_p393_Instr(machine); return;
        case 395: make_MOV_p395_Instr(machine); return;
        case 396: make_MOV_p396_Instr(machine); return;
        case 398: make_MOV_p398_Instr(machine); return;
        case 399: make_MOV_p399_Instr(machine); return;
        case 401: make_MOV_p401_Instr(machine); return;
        case 402: make_MOV_p402_Instr(machine); return;
        case 404: make_MOV_p404_Instr(machine); return;
        case 405: make_MOV_p405_Instr(machine); return;
        case 406: make_MOV_p406_Instr(machine); return;
        case 407: make_MOV_p407_Instr(machine); return;
        case 408: make_MOV_p408_Instr(machine); return;
        case 40820: make_MOV_p408_No20_Instr(machine); return;
        case 412: make_MOV_p412_Instr(machine); return;
        case 413: make_MOV_p413_Instr(machine); return;
        case 417: make_MPY_p417_Instr(machine); return;
        case 418: make_MPY_p418_Instr(machine); return;
        case 420: make_MPYK_p420_Instr(machine); return;
        case 421: make_MPYM_p421_Instr(machine); return;
        case 423: make_MPYM_p423_Instr(machine); return;
        case 424: make_MPYMK_p424_Instr(machine); return;
        case 425: make_MPYM_p425_Instr(machine); return;
        case 427: make_MPYM_p427_Instr(machine); return;
        case 428: make_MPY_p428_Instr(machine); return;
        case 430: make_MPY_p430_Instr(machine); return;
        case 432: make_MPYM_p432_Instr(machine); return;
        case 434: make_NEG_p434_Instr(machine); return;
        case 436: make_NOP_p436_Instr(machine); return;
        case 43616: make_NOP16_p436_Instr(machine); return;
        case 437: make_NOT_p437_Instr(machine); return;
        case 439: make_OR_p439_Instr(machine); return;
        case 440: make_OR_p440_Instr(machine); return;
        case 442: make_OR_p442_Instr(machine); return;
        case 443: make_OR_p443_Instr(machine); return;
        case 444: make_OR_p444_Instr(machine); return;
        case 445: make_OR_p445_Instr(machine); return;
        case 446: make_OR_p446_Instr(machine); return;
        case 448: make_POP_p448_Instr(machine); return;
        case 449: make_POP_p449_Instr(machine); return;
        case 450: make_POP_p450_Instr(machine); return;
        case 451: make_POP_p451_Instr(machine); return;
        case 452: make_POP_p452_Instr(machine); return;
        case 453: make_POP_p453_Instr(machine); return;
        case 454: make_POPBOTH_p454_Instr(machine); return;
        case 458: make_PSH_p458_Instr(machine); return;
        case 459: make_PSH_p459_Instr(machine); return;
        case 460: make_PSH_p460_Instr(machine); return;
        case 461: make_PSH_p461_Instr(machine); return;
        case 462: make_PSH_p462_Instr(machine); return;
        case 463: make_PSH_p463_Instr(machine); return;
        case 464: make_PSHBOTH_p464_Instr(machine); return;
        case 465: make_RESET_p465_Instr(machine); return;
        case 469: make_RET_p469_Instr(machine); return;
        case 471: make_RETCC_p471_Instr(machine); return;
        case 473: make_RETI_p473_Instr(machine); return;
        case 475: make_ROL_p475_Instr(machine); return;
        case 477: make_ROR_p477_Instr(machine); return;
        case 479: make_ROUND_p479_Instr(machine); return;
        case 482: make_RPT_p482_Instr(machine); return;
        case 484: make_RPT_p484_Instr(machine); return;
        case 487: make_RPTADD_p487_Instr(machine); return;
        case 488: make_RPTADD_p488_Instr(machine); return;
        case 490: make_RPTBLOCAL_p490_Instr(machine); return;
        case 497: make_RPTB_p497_Instr(machine); return;
        case 500: make_RPTCC_p500_Instr(machine); return;
        case 503: make_RPTSUB_p503_Instr(machine); return;
        case 505: make_SAT_p505_Instr(machine); return;
        case 507: make_SFTCC_p507_Instr(machine); return;
        case 510: make_SFTL_p510_Instr(machine); return;
        case 511: make_SFTL_p511_Instr(machine); return;
        case 513: make_SFTL_p513_Instr(machine); return;
        case 516: make_SFTS_p516_Instr(machine); return;
        case 518: make_SFTSC_p518_Instr(machine); return;
        case 520: make_SFTS_p520_Instr(machine); return;
        case 522: make_SFTSC_p522_Instr(machine); return;
        case 525: make_SFTS_p525_Instr(machine); return;
        case 530: make_SQA_p530_Instr(machine); return;
        case 531: make_SQAM_p531_Instr(machine); return;
        case 532: make_SQDST_p532_Instr(machine); return;
        case 535: make_SQR_p535_Instr(machine); return;
        case 536: make_SQRM_p536_Instr(machine); return;
        case 538: make_SQS_p538_Instr(machine); return;
        case 539: make_SQSM_p539_Instr(machine); return;
        case 541: make_SUB_p541_Instr(machine); return;
        case 543: make_SUB_p543_Instr(machine); return;
        case 545: make_SUB_p545_Instr(machine); return;
        case 547: make_SUB_p547_Instr(machine); return;
        case 551: make_SUB_p551_Instr(machine); return;
        case 552: make_SUB_p552_Instr(machine); return;
        case 556: make_SUB_p556_Instr(machine); return;
        case 558: make_SUB_p558_Instr(machine); return;
        case 560: make_SUB_p560_Instr(machine); return;
        case 561: make_SUB_p561_Instr(machine); return;
        case 562: make_SUB_p562_Instr(machine); return;
        case 563: make_SUB_p563_Instr(machine); return;
        case 564: make_SUB_p564_Instr(machine); return;
        case 565: make_SUB_p565_Instr(machine); return;
        case 566: make_SUB_p566_Instr(machine); return;
        case 567: make_SUB_p567_Instr(machine); return;
        case 569: make_SUB_p569_Instr(machine); return;
        case 573: make_SUB_p573_Instr(machine); return;
        case 570: make_SUB_p570_Instr(machine); return;
        case 572: make_SUB_p572_Instr(machine); return;
        case 574: make_SUB_p574_Instr(machine); return;
        case 575: make_SUB_p575_Instr(machine); return;
        case 999999: make_SUB_xxx_Instr(machine); return;
        case 578: make_SUBADD_p578_Instr(machine); return;
        case 580: make_SUBADD_p580_Instr(machine); return;
        case 582: make_SUBC_p582_Instr(machine); return;
        case 585: make_SWAP_p585_Instr(machine); return;
        case 590: make_SWAPP_p590_Instr(machine); return;
        case 595: make_SWAP4_p595_Instr(machine); return;
        case 597: make_TRAP_p597_Instr(machine); return;
        case 600: make_XCC_p600_Instr(machine); return;
        case 603: make_XCCPART_p603_Instr(machine); return;
        case 607: make_XOR_p607_Instr(machine); return;
        case 608: make_XOR_p608_Instr(machine); return;
        case 610: make_XOR_p610_Instr(machine); return;
        case 611: make_XOR_p611_Instr(machine); return;
        case 612: make_XOR_p612_Instr(machine); return;
        case 613: make_XOR_p613_Instr(machine); return;
        case 614: make_XOR_p614_Instr(machine); return;
        case 777777: make_UNKNOWN_Instr(machine); return;
        


        default:
            throw new NullPointerException("C55xOperation: no microinstructions found for operation: " + this.getMnemonic()+ " page:"+page);   
        }
    }


    

    public void make_AADD_p95_Instr(Machine machine) { // kps quality code
        checkImplicitParallelism(); // to be on safe side
        // e.g. AADD T1,AR3
        // ADD TAx, TAy
        // TAy = TAy + TAx
        // used in mmul_t after label mmul_dual_mac
        List<Operand> args = this.getArgs();
        Register reg0 = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp0 = new C55xMicroRegisterOperand(reg0);
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);

        Microinstruction add = new MicroArithmetic(mRegOp0, mRegOp1, "ADD", 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp1);

        this.addMicroinstr(add);
        this.addMicroinstr(writeReg);
    }
    
    public void make_AADD_p97_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side        
        List<Operand> args = this.getArgs();
        MicroOperand constant            = new Constant(args.get(0).getValue(),8);
        Register reg0                    = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp0 = new C55xMicroRegisterOperand(reg0);
        
        Microinstruction add          = new MicroArithmetic(constant, mRegOp0, "ADD",0);
        Microinstruction writeReg     = new WriteReg(machine.getDataResult(0), mRegOp0);        
        
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg);
        
    }

    public void make_AADD_p98_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        C55xMicroRegisterOperand  sp  = new C55xMicroRegisterOperand(machine.getRegister("sp"));
        MicroOperand constant         = new Constant(args.get(0).getValue(),16);
        Microinstruction add          = new MicroArithmetic(constant, sp, "ADD",0);
        Microinstruction writeReg     = new WriteReg(machine.getDataResult(0), sp);        
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg);
        //System.out.println("make_AADD_p98_Instr: " + oper + " (" + syntax + ")") ;
       
    }

    
    public void make_ABDST_p99_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_ABS_p101_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // ABS [src,] dst
        List<Operand> args = this.getArgs();
        Register reg0 = args.get(0).getFirstReg(machine);
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp0 = new C55xMicroRegisterOperand(reg0);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        Microinstruction abs = new C55xAbs(mRegOp0, 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp1);

        this.addMicroinstr(abs);
        this.addMicroinstr(writeReg);
    } 


    public void make_ADD_p106_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // ADD [src],dst
        List<Operand> args = this.getArgs();
        Register reg0 = args.get(0).getFirstReg(machine);
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp0 = new C55xMicroRegisterOperand(reg0);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        Microinstruction add = new MicroArithmetic(mRegOp0, mRegOp1, "ADD",0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp1);
        
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg);
    } 

    public void make_ADD_p107_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side

        // make_ADD_p107 ; hopefully conditionally generating microcode can be 
        // avoided most of the time - pgm
        // ADD K16,[src],dst
        List<Operand> args = this.getArgs();

        MicroOperand constant = new Constant(args.get(0).getValue(),16);
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        Microinstruction writeReg;
        Microinstruction add = new MicroArithmetic(constant, mRegOp1, "ADD",0);

        if (args.size() == 3){
            Register reg2 = args.get(2).getFirstReg(machine);
            C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
            writeReg = new WriteReg(machine.getDataResult(0), mRegOp2);
        }
        
        else {
            writeReg = new WriteReg(machine.getDataResult(0), mRegOp1);
              }
        
        
        //System.out.println("make_ADD_p107_Instr: " + instr + " (" + syntax + ")") ;
        
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg);
        
    }



    public void make_ADD_p110_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // ADD Smem, [src], dst

        List<Operand> args = this.getArgs();

        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);
        Register reg1 = args.get(1).getFirstReg(machine);
        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        
        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem,0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0), machine.getDataResult(0));
        Microinstruction add = new MicroArithmetic(machine.getDataResult(0), mRegOp1, "ADD", 1);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(1), mRegOp2);

        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg);
    }

    public void make_ADD_p112_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    
    public void make_ADD_p113_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // ADD AC1 << #16, AC0
        List<Operand> args = this.getArgs();
        
        C55xShiftOperand c55xShift = (C55xShiftOperand)args.get(0);
        Register reg2 = ((C55xRegisterOperand)c55xShift.getOp1()).getFirstReg(machine);

        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);


        Microinstruction shift = new C55xShift(c55xShift, reg2.getValue(), c55xShift.getOp2().getValue(), 0);

        Microinstruction add = new MicroArithmetic(machine.getDataResult(0), mRegOp1, "ADD",1);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(1), mRegOp1);
        
        this.addMicroinstr(shift);
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg); 
    } 
    
    public void make_ADD_p114_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_ADD_p115_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_ADD_p116_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    //public void make_ADD_p118_Instr(Machine machine) {
    //checkImplicitParallelism(); // to be on safe side
    //}

    public void make_ADD_p119_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_ADD_p120_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_ADD_p121_Instr(Machine machine) { // kps quality code
        // ADD [uns(]Smem[)] << #SHIFTW, [ACx,] ACy
        // e.g. ADD mmap(@T1) << #1,AC0,AC0
        //      ADD @#00h << #15,AC0,AC0
        // ACy = ACx + (Smem << #SHIFTW)

        // WORKS?: used in dsplib sqrtv

        List<Operand> args = this.getArgs();
        C55xShiftOperand c55xShift = (C55xShiftOperand)args.get(0);
        C55xMemoryAccessOperand Smem = (C55xMemoryAccessOperand)c55xShift.getOp1();
        long c55xImmValue = c55xShift.getOp2().getValue();
        Constant shiftVal = new Constant(c55xImmValue, 6);

        Register ACx = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mACx = new C55xMicroRegisterOperand(ACx);
        Register ACy = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mACy = new C55xMicroRegisterOperand(ACy);

        Microinstruction computeSmem = new C55xComputeSmem(Smem, 0);
        ReadMem readSmem = new ReadMem(machine.getAddressResult(0),
                                       machine.getDataResult(0));
        if (!Smem.hasUnsMod()) readSmem.setSignExtend(ReadMem.sxUseSXMD);

        Microinstruction shift = new Shift(machine.getDataResult(0), shiftVal, "AS", 1);
        Microinstruction add = new MicroArithmetic(machine.getDataResult(1),
                                                   mACx, "ADD", 2);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(2), mACy);

        this.addMicroinstr(computeSmem);
        this.addMicroinstr(readSmem);
        this.addMicroinstr(shift);
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg); 
    }

    public void make_ADD_p122_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // add dbl(Lmem), [ACx,] ACy
        List<Operand> args = this.getArgs();
        
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);
        
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        
             Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem,0);
        Microinstruction readMemDouble = new ReadMemDouble(machine.getAddressResult(0), machine.getDataResult(0));
        Microinstruction add = new MicroArithmetic(machine.getDataResult(0), mRegOp1, "ADD",1);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(1), mRegOp2);
        
        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMemDouble);
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg);
        
    }

   public void make_ADD_p123_Instr(Machine machine) { 
       checkImplicitParallelism(); // to be on safe side
        //ADD Xmem, Ymem, ACx (shifts also operands by 16 bits left)
        List<Operand> args = this.getArgs();

        MicroOperand constant16  = new Constant(16,16); // shift value 16 bits left
        C55xMemoryAccessOperand c55xSmem1 = (C55xMemoryAccessOperand)args.get(0);
        C55xMemoryAccessOperand c55xSmem2 = (C55xMemoryAccessOperand)args.get(1);        
        Register reg1 = args.get(2).getFirstReg(machine);
        
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);

        Microinstruction c55xSmemAccess1 = new C55xComputeSmem(c55xSmem1,0);
        Microinstruction readMem1  = new ReadMem(machine.getAddressResult(0), machine.getDataResult(0));
        Microinstruction shift1 = new Shift(machine.getDataResult(0), constant16, "AS",1);
        Microinstruction c55xSmemAccess2 = new C55xComputeSmem(c55xSmem2,1);
        Microinstruction readMem2  = new ReadMem(machine.getAddressResult(1), machine.getDataResult(2));
        Microinstruction shift2 = new Shift(machine.getDataResult(2), constant16, "AS",3);
        Microinstruction add   = new MicroArithmetic(machine.getDataResult(1), machine.getDataResult(3), "ADD",4);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(4), mRegOp1);
        
        this.addMicroinstr(c55xSmemAccess1);
        this.addMicroinstr(readMem1);
        this.addMicroinstr(shift1);
        this.addMicroinstr(c55xSmemAccess2);
        this.addMicroinstr(readMem2);
        this.addMicroinstr(shift2);
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg);
   }

    public void make_ADD_p124_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        //ADD K16, Smem
        
        List<Operand> args = this.getArgs();
        MicroOperand constant = new Constant(args.get(0).getValue(),16);
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(1);
        //        MicroOperand dataResult = new DataResult(this,16);
        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem,0);
        Microinstruction readMem   = new ReadMem(machine.getAddressResult(0), machine.getDataResult(0));
        Microinstruction add       = new MicroArithmetic(constant, machine.getDataResult(0), "ADD",1);
        Microinstruction writeMem  = new WriteMem(machine.getDataResult(1), machine.getAddressResult(0));
        
        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(add);
        this.addMicroinstr(writeMem);
    } 
    
    
    public void make_ADD_p126_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        //ADD dual(Lmem), [ACx,] ACy - DOES NOT WORK pgm

        List<Operand> args = this.getArgs();
        
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);
        
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        Register reg2 = args.get(2).getFirstReg(machine);

        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        
        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem,0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0), machine.getDataResult(0));
        Microinstruction dual = new C55xDual(machine.getAddressResult(0), mRegOp1, mRegOp2);
        
        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(dual);

    }
    

    public void make_ADD_p128_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_ADD_p130_Instr(Machine machine) {
        // ADD::MOV
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    } 

    public void make_ADDSUB_p133_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // ADDSUB Tx, Smem, ACx DOES NOT WORK -pgm
        List<Operand> args = this.getArgs();
        Register reg1 = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(1);
        
        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2); 

        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        this.addMicroinstr(c55xSmemAccess);
    } 


    public void make_ADDSUB_p135_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_ADDSUBCC_p137_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_ADDSUBCC_p139_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_ADDSUB2CC_p141_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_ADDV_p144_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    
    public void make_AMAR_p146_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);
        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem,0);
        this.addMicroinstr(c55xSmemAccess);
        
    } 

    public void make_AMAR_p148_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // AMAR Smem, XAdst
        List<Operand> args = this.getArgs();
        
        
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction writeReg = new WriteReg(machine.getAddressResult(0), mRegOp1);
        
        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(writeReg);
    } 

    public void make_AMAR_p149_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_AMAR_p151_Instr(Machine machine) {
        // AMAR::MAC
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_AMAR_p153_Instr(Machine machine) {
        // AMAR::MAC
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    } 

    public void make_AMAR_p155_Instr(Machine machine) {
        // AMAR::MAS
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_AMAR_p157_Instr(Machine machine) {
        // AMAR::MPY
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_AMOV_p159_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_AMOV_p161_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        Register reg1 = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        Register reg2 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        
        Microinstruction writeReg = new WriteReg(mRegOp1, mRegOp2);
        this.addMicroinstr(writeReg);
        
    } 

    public void make_AMOV_p162_Instr(Machine machine) { // kps quality code
        checkImplicitParallelism(); // to be on safe side
        // AMOV P8, TAx
        List<Operand> args = this.getArgs();
        MicroOperand constant = new Constant(args.get(0).getValue(), 8);
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);

        Microinstruction writeReg = new WriteReg(constant, mRegOp1);
        this.addMicroinstr(writeReg);
    } 

    public void make_AND_p165_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // AND src, dst
        List<Operand> args = this.getArgs();
        
        Register reg1 = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        Register reg2 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        
        Microinstruction and = new Logical(mRegOp1, mRegOp2, "AND", 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp2);
        
        this.addMicroinstr(and);
        this.addMicroinstr(writeReg);
        
        //System.out.println("make_AND_p165_Instr: " + instr + " (" + syntax + ")") ;
    }
    


    public void make_AND_p166_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // AND k8/k16, src, dst
        List<Operand> args = this.getArgs();
        MicroOperand constant = new Constant(args.get(0).getValue(),16);
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        
        Microinstruction and = new Logical(constant, mRegOp1, "AND", 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp2);
        
        this.addMicroinstr(and);
        this.addMicroinstr(writeReg);
    }

    public void make_AND_p168_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_AND_p169_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_AND_p170_Instr(Machine machine) { // kps quality code
        // AND #32768 << 16,AC0,AC1
        // e.g. AND k16 << #16, [ACx,] ACy
        // ACy = ACx & (k16 <<< #16)
        List<Operand> args = this.getArgs();
        C55xShiftOperand shiftOp = (C55xShiftOperand)args.get(0);
        Register ACx = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOpACx = new C55xMicroRegisterOperand(ACx);
        Register ACy = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOpACy = new C55xMicroRegisterOperand(ACy);

        Microinstruction shift = new C55xShift(shiftOp, 0, 0, 0);
        Microinstruction and = new Logical(machine.getDataResult(0), mRegOpACx, "AND", 1);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(1), mRegOpACy);

        this.addMicroinstr(shift);
        this.addMicroinstr(and);
        this.addMicroinstr(writeReg);
    }

    public void make_AND_p171_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_AND_p172_Instr(Machine machine) { // kps bug fixed
        // AND k16, Smem
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();

        MicroOperand constant = new Constant(args.get(0).getValue(), 16);
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(1);

        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0),
                                               machine.getDataResult(0));
        Microinstruction and = new Logical(constant, machine.getDataResult(0), "AND", 1);
        Microinstruction writeMem = new WriteMem(machine.getDataResult(1),
                                                 machine.getAddressResult(0));

        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(and);
        this.addMicroinstr(writeMem);
    } 

    public void make_ASUB_p174_Instr(Machine machine) { // kps quality code
        // e.g. ASUB AR0,T1
        // ASUB TAx, TAy
        // TAy = TAy – TAx
        // XXX TODO: ARnLC stuff
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        Register TAx = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand TAxOp = new C55xMicroRegisterOperand(TAx);
        Register TAy = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand TAyOp = new C55xMicroRegisterOperand(TAy);
        Microinstruction sub = new MicroArithmetic(TAxOp, TAyOp, "SUB", 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), TAyOp);
        
        this.addMicroinstr(sub);
        this.addMicroinstr(writeReg);
    }

    public void make_ASUB_p176_Instr(Machine machine) { // kps quality code
        // e.g. ASUB #3,T1
        // ASUB P8, TAx
        // TAx = TAx – P8
        // XXX TODO: ARnLC stuff
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        // 16: this will make the constant positive, as it is not sign extended
        MicroOperand constant = new Constant(args.get(0).getValue(), 16);
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        Microinstruction sub = new MicroArithmetic(constant, mRegOp1, "SUB", 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp1);

        this.addMicroinstr(sub);
        this.addMicroinstr(writeReg);
    }

    public void make_B_p178_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_B_p179_Instr(Machine machine) { 
        Microinstruction unconditionalBranch = new C55xUnconditionalBranch(this);
        this.addMicroinstr(unconditionalBranch); 
    }
    
    
    public void make_BAND_p181_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_BCC_p182_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        C55xConditionFieldOperand condOp = (C55xConditionFieldOperand)args.get(1);
             Microinstruction cond = new C55xCondition(condOp);
        Microinstruction condBranch = new ConditionalBranch(this);
        
               this.addMicroinstr(cond); // evaluate condition first!
        this.addMicroinstr(condBranch);
    }



    public void make_BCC_p186_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_BCC_p189_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_BCLR_p192_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_BCLR_p193_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_BCLR_p194_No1234_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_BCLR_p194_No5_Instr(Machine machine) {
        // e.g. BCLR ST1_CPL

        List<Operand> args = this.getArgs();
        C55xBitOperand bitOp = (C55xBitOperand)args.get(0);
        Constant bit = new Constant(bitOp.getBit(machine), 16);
        Register reg = bitOp.getRegister(machine);

        C55xMicroRegisterOperand mRegOp = new C55xMicroRegisterOperand(reg);

        Microinstruction bclr = new C55xBitOperation(bit, mRegOp, "BCLR", 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp);

               this.addMicroinstr(bclr);
               this.addMicroinstr(writeReg);

        //checkImplicitParallelism(); // to be on safe side
        //throw new NullPointerException("not implemented: " + this);

        /*checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        C55xBitOperand bitOp = (C55xBitOperand)args.get(0);
        
        C55xMicroRegisterOperand mRegOp = new C55xMicroRegisterOperand(machine.getRegister(bitOp.getName()));
        Microinstruction bclr = new C55xBitOperation(mRegOp, "BCLR", 0);

        this.addMicroinstr(bclr);      // evaluate condition first*/
    }
    

    public void make_BCNT_p197_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_BFXPA_p198_Instr(Machine machine) { // kps quality code
        checkImplicitParallelism(); // to be on safe side
        // BFXPA k16, ACx, dst

        List<Operand> args = this.getArgs();
        MicroOperand constant = new Constant(args.get(0).getValue(), 16);
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

        if (true)
            throw new NullPointerException("not implemented: " + this);
        // kps - XXX TODO: implement XPA in C55xBitFieldOperation.java
        Microinstruction bfxpa = new C55xBitFieldOperation(constant, mRegOp1, "XPA", 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp2);

        this.addMicroinstr(bfxpa);
        this.addMicroinstr(writeReg);
    }

    public void make_BFXTR_p199_Instr(Machine machine) { // kps quality code
        checkImplicitParallelism(); // to be on safe side
        // BFXTR k16, ACx, dst
        // BFXTR #32768,AC0,T2
        List<Operand> args = this.getArgs();
        MicroOperand constant = new Constant(args.get(0).getValue(), 16);
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

        Microinstruction bfxtr = new C55xBitFieldOperation(constant, mRegOp1, "XTR", 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp2);

        this.addMicroinstr(bfxtr);
        this.addMicroinstr(writeReg);
    }

    public void make_BNOT_p200_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_BNOT_p201_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_BSET_p202_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_BSET_p203_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_BSET_p204_No1234_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_BSET_p204_No5_Instr(Machine machine) { // kps quality code
        // e.g. BSET ST1_CPL

        List<Operand> args = this.getArgs();
        C55xBitOperand bitOp = (C55xBitOperand)args.get(0);
        Constant bit = new Constant(bitOp.getBit(machine), 16);
        Register reg = bitOp.getRegister(machine);

        C55xMicroRegisterOperand mRegOp = new C55xMicroRegisterOperand(reg);

        Microinstruction bset = new C55xBitOperation(bit, mRegOp, "BSET", 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp);

               this.addMicroinstr(bset);
               this.addMicroinstr(writeReg);
    }
    
    
    public void make_BTST_p207_Instr(Machine machine) {
        //      BTST Baddr, src, TCx
        // e.g. BTST @#00h,AR7,TC1
        // used in craw

        List<Operand> args = this.getArgs();

        C55xBaddrOperand baddrOp = (C55xBaddrOperand)args.get(0);
        C55xRegisterOperand baddrReg = baddrOp.getRegister();        
        C55xImmediateOperand baddrImm = baddrOp.getImmediate();
        C55xMemoryAccessOperand baddrMa = baddrOp.getMemoryAccess();

        Register src = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mSrc = new C55xMicroRegisterOperand(src);

        C55xBitOperand bitOp = (C55xBitOperand)args.get(2);
        Constant bit = new Constant(bitOp.getBit(machine), 16);
        Register reg = bitOp.getRegister(machine);
        C55xMicroRegisterOperand mRegOp = new C55xMicroRegisterOperand(reg);

        // hack
        if (baddrMa != null) {
            Microinstruction compute = new C55xComputeSmem(baddrMa, 0);
            Microinstruction read = new ReadMem(machine.getAddressResult(0),
                                                machine.getDataResult(0));
            Microinstruction btst = new C55xBitOperation(machine.getDataResult(0), mSrc,
                                                         bit, mRegOp,
                                                         "BTST", 1);
            Microinstruction writeReg  = new WriteReg(machine.getDataResult(1), mRegOp);

            this.addMicroinstr(compute);
            this.addMicroinstr(read);
            this.addMicroinstr(btst);
            this.addMicroinstr(writeReg);
        }
        else
            throw new NullPointerException("not implemented: " + this);

    }




    public void make_BTST_p210_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_BTST_p211_Instr(Machine machine) {
        C55xMachine c55x = (C55xMachine)machine;
        // BTST k4, Smem, TCx
        // e.g. BTST #0,mmap(@T2),TC1
        // This instruction performs a bit manipulation in the A-unit ALU. This instruction
        // tests a single bit of a memory (Smem) location. The bit tested is defined by a
        // 4-bit immediate value, k4. The tested bit is copied into the selected TCx status
        // bit.
        List<Operand> args = this.getArgs();
        MicroOperand k4 = new Constant(args.get(0).getValue(), 4);
        C55xMemoryAccessOperand Smem = (C55xMemoryAccessOperand)args.get(1);
        C55xBitOperand bitOp = (C55xBitOperand)args.get(2);
        Constant bit = new Constant(bitOp.getBit(machine), 16);
        Register reg = bitOp.getRegister(machine);
        C55xMicroRegisterOperand mRegOp  = new C55xMicroRegisterOperand(reg);

        Microinstruction smemAccess = new C55xComputeSmem(Smem, 0);
        ReadMem readSmem = new ReadMem(machine.getAddressResult(0),
                                       machine.getDataResult(0));
        Microinstruction btst = new C55xBitOperation(k4, machine.getDataResult(0),
                                                     bit, mRegOp,
                                                     "BTST", 1);
        Microinstruction writeReg  = new WriteReg(machine.getDataResult(1), mRegOp);

        this.addMicroinstr(smemAccess);
        this.addMicroinstr(readSmem);
        this.addMicroinstr(btst);
        this.addMicroinstr(writeReg);
    }

    public void make_BTSTCLR_p212_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_BTSTNOT_p213_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_BTSTP_p214_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_BTSTSET_p216_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_CALL_p218_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side        
        // dynamic CFG : CALL ACx
        List<Operand> args = this.getArgs();
        Register reg0 = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg0);
        
        Microinstruction call = new C55xCall(mRegOp1);
        this.addMicroinstr(call);
        
    }
    
    
    public void make_CALL_p219_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // CFG: CALL L16 / P24
        Microinstruction call = new C55xCall(this);
        this.addMicroinstr(call);
    }
    


    public void make_CALLCC_p223_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }


    public void make_CMP_p227_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // CMP Smem == K16, TC1
        List<Operand> args = this.getArgs();
        
        C55xRelOpOperand relOp = (C55xRelOpOperand)args.get(0);
        C55xBitOperand   bitOp = (C55xBitOperand)args.get(1);
        C55xMemoryAccessOperand sMem = relOp.getMemAccessOp();
        
        Microinstruction c55xSmemAccess = new C55xComputeSmem(sMem, 0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0), machine.getDataResult(0));
        Microinstruction cmp = new C55xCompare(machine.getDataResult(0), relOp, bitOp);
        
        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(cmp);
    } 



    public void make_CMP_p229_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // CMP src RELOP dst, TC1
        //System.out.println("make_CMP_p229_Instr: " + instr + " (" + syntax + ")") ;

        List<Operand> args = this.getArgs();
        C55xRelOpOperand relOp = (C55xRelOpOperand)args.get(0);
        C55xBitOperand   bitOp = (C55xBitOperand)args.get(1);
        
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(relOp.getFirstReg(machine));
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(relOp.getSecondReg(machine));
        
        Microinstruction cmp = new C55xCompare(relOp, bitOp);
        this.addMicroinstr(cmp);
        
        //System.out.println("make_CMP_p229_Instr: " + instr + " (" + syntax + ")") ;
    }



    public void make_CMPU_p229_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // CMPU src RELOP dst, TC1 , for 64 bit signed longs, this is same as CMP.
       
        List<Operand> args = this.getArgs();
               C55xRelOpOperand relOp = (C55xRelOpOperand)args.get(0);
        C55xBitOperand   bitOp = (C55xBitOperand)args.get(1);

        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(relOp.getFirstReg(machine));
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(relOp.getSecondReg(machine));
        Microinstruction cmp = new C55xCompare(relOp, bitOp);
        this.addMicroinstr(cmp);

        //System.out.println("make_CMPU_p229_Instr: " + instr + " (" + syntax + ")") ;
    } 


    public void make_CMPAND_p231_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_CMPOR_p236_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_DELAY_p242_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_EXP_p243_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // e.g. EXP AC0,T3
        List<Operand> args = this.getArgs();
        Register ACx = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOpACx = new C55xMicroRegisterOperand(ACx);
        Register Tx =  args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOpTx  = new C55xMicroRegisterOperand(Tx);

        Microinstruction exponent = new C55xExponent(mRegOpACx, "EXP", 0);
        Microinstruction writeTx  = new WriteReg(machine.getDataResult(0), mRegOpTx);

        this.addMicroinstr(exponent);
        this.addMicroinstr(writeTx);
    }

    public void make_FIRSADD_p244_Instr(Machine machine) { // kps quality code
        //      FIRSADD Xmem, Ymem, Cmem, ACx, ACy
        // e.g. FIRSADD *AR1+,*AR3-,*CDP+,AC1,AC0

        // This instruction performs two parallel operations: multiply and accumulate
        // (MAC), and addition. The operation is executed:
        // ACy = ACy + (ACx * Cmem)
        // :: ACx = (Xmem << #16) + (Ymem << #16)
        // The first operation performs a multiplication and an accumulation in the D-unit
        // MAC. The input operands of the multiplier are the content of ACx(32–16) and
        // the content of a data memory operand Cmem, addressed using the coefficient
        // addressing mode, sign extended to 17 bits.

        List<Operand> args = this.getArgs();

        // Xmem
        C55xMemoryAccessOperand Xmem = (C55xMemoryAccessOperand)args.get(0);
        Microinstruction xmemAccess = new C55xComputeSmem(Xmem, 0);
        ReadMem readXmem = new ReadMem(machine.getAddressResult(0),
                                       machine.getDataResult(0));
        readXmem.setSignExtend(ReadMem.sxUseSXMD);
        this.addMicroinstr(xmemAccess);
        this.addMicroinstr(readXmem);

        // Ymem
        C55xMemoryAccessOperand Ymem = (C55xMemoryAccessOperand)args.get(1);
        Microinstruction ymemAccess = new C55xComputeSmem(Ymem, 1);
        ReadMem readYmem = new ReadMem(machine.getAddressResult(1),
                                       machine.getDataResult(1));
        readYmem.setSignExtend(ReadMem.sxUseSXMD);
        this.addMicroinstr(ymemAccess);
        this.addMicroinstr(readYmem);

        // Cmem
        C55xMemoryAccessOperand Cmem = (C55xMemoryAccessOperand)args.get(2);
        Microinstruction cmemAccess = new C55xComputeSmem(Cmem, 2);
        ReadMem readCmem = new ReadMem(machine.getAddressResult(2),
                                       machine.getDataResult(2));
        readCmem.setSignExtend(ReadMem.sxSigned);
        this.addMicroinstr(cmemAccess);
        this.addMicroinstr(readCmem);

        // ACx
        Register ACx = args.get(3).getFirstReg(machine);
        C55xMicroRegisterOperand mACx = new C55xMicroRegisterOperand(ACx);
        Microinstruction extractACx = new ExtractSignExtend(mACx, 17, 16, 3); // ACx(32–16)
        this.addMicroinstr(extractACx);

        // ACy
        Register ACy = args.get(4).getFirstReg(machine);
        C55xMicroRegisterOperand mACy = new C55xMicroRegisterOperand(ACy);

        // ACy = ACy + ACx * Cmem
        Microinstruction mul = new MicroArithmetic(machine.getDataResult(3),
                                                   machine.getDataResult(2), "MUL", 4);
        Microinstruction add = new MicroArithmetic(mACy,
                                                   machine.getDataResult(4), "ADD", 5);
        Microinstruction writeACy = new WriteReg(machine.getDataResult(5), mACy);
        this.addMicroinstr(mul);
        this.addMicroinstr(add);
        this.addMicroinstr(writeACy);

        // ACx = (Xmem << #16) + (Ymem << #16)
        Constant c16 = new Constant(16, 16);
        Microinstruction shiftX = new Shift(machine.getDataResult(0), c16, "AS", 6);
        Microinstruction shiftY = new Shift(machine.getDataResult(1), c16, "AS", 7);
        Microinstruction add2 = new MicroArithmetic(machine.getDataResult(6),
                                                    machine.getDataResult(7), "ADD", 8);
        Microinstruction writeACx = new WriteReg(machine.getDataResult(8), mACx);
        this.addMicroinstr(shiftX);
        this.addMicroinstr(shiftY);
        this.addMicroinstr(add2);
        this.addMicroinstr(writeACx);
    }


    public void make_FIRSSUB_p246_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_IDLE_p248_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_INTR_p249_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_LMS_p251_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MAC_p256_Instr(Machine machine) {

        // MAC::MAC
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MAC_p257_Instr(Machine machine) {
        // MAC[R] ACy, Tx, ACx, ACy
        // e.g. MACR AC0,T0,AC1,AC0
        // This instruction performs a multiplication and an accumulation in the D-unit
        // MAC. The input operands of the multiplier are ACy(32–16) and the content of
        // Tx, sign extended to 17 bits:
        // ACy = (ACy * Tx) + ACx
        // - If FRCT = 1, the output of the multiplier is shifted left by 1 bit.

        // RDM:
        // When the optional rnd or R keyword is applied to the instruction, then rounding
        // is performed in the D-unit shifter. This is done according to RDM:
        // - When RDM = 0, the biased rounding to the infinite is performed. 8000h
        // (2^15) is added to the 40-bit result of the shift result.
        // - When RDM = 1, the unbiased rounding to the nearest is performed.
        // According to the value of the 17 LSBs of the 40-bit result of the shift result,
        // 8000h (2^15) is added:
        // if( 8000h < bit(15–0) < 10000h)
        // add 8000h to the 40-bit result of the shift result.
        // else if( bit(15–0) == 8000h)
        // if( bit(16) == 1)
        // add 8000h to the 40-bit result of the shift result.
        // If a rounding has been performed, the 16 lowest bits of the result are cleared
        // to 0.

        checkImplicitParallelism();
        List<Operand> args = this.getArgs();
        Register reg0 = args.get(0).getFirstReg(machine);
        Register reg1 = args.get(1).getFirstReg(machine);
        Register reg2 = args.get(2).getFirstReg(machine);
        Register reg3 = args.get(3).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp0 = new C55xMicroRegisterOperand(reg0); // ACy
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1); // Tx
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2); // ACx
        C55xMicroRegisterOperand mRegOp3 = new C55xMicroRegisterOperand(reg3); // ACy

        Microinstruction extract = new ExtractSignExtend(mRegOp0, 17, 16, 0); // ACy(32–16)
        Microinstruction extend = new ExtractSignExtend(mRegOp1, 16, 0, 1);   // Tx
        Microinstruction multiply = new MicroArithmetic(machine.getDataResult(0),
                                                        machine.getDataResult(1), "MUL", 2);
        Microinstruction add = new MicroArithmetic(machine.getDataResult(2),
                                                   mRegOp2, "ADD", 3);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(3), mRegOp3);

        this.addMicroinstr(extract);
        this.addMicroinstr(extend);
        this.addMicroinstr(multiply);
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg);
    }
    
    public void make_MACK_p258_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MACM_p260_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side // rounding not implemented for R
        List<Operand> args = this.getArgs();

        C55xMemoryAccessOperand memAccessOp1 = (C55xMemoryAccessOperand)args.get(0);
        C55xMemoryAccessOperand memAccessOp2 = (C55xMemoryAccessOperand)args.get(1);
        Register reg1 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);

        Microinstruction memAccess1 = new C55xComputeSmem(memAccessOp1,0);
        Microinstruction readMem1 = new ReadMem(machine.getAddressResult(0), machine.getDataResult(0));
        Microinstruction memAccess2 = new C55xComputeSmem(memAccessOp2,1);
        Microinstruction  readMem2 = new ReadMem(machine.getAddressResult(1), machine.getDataResult(1));
        Microinstruction multiply = new MicroArithmetic(machine.getDataResult(0),machine.getDataResult(1), "MUL", 2);
        Microinstruction add      = new MicroArithmetic(machine.getDataResult(2), mRegOp1, "ADD",3);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(3), mRegOp1);
        
        
        this.addMicroinstr(memAccess1);
        this.addMicroinstr(readMem1);
        this.addMicroinstr(memAccess2);
        this.addMicroinstr(readMem2);
               this.addMicroinstr(multiply);
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg);
    } 

    public void make_MACM_p262_Instr(Machine machine) { // kps quality code
        // MACM[R] [T3 = ]Smem, [ACx,] ACy
        // e.g. MACM *AR1,AC0,AC1
        // ACy = ACy + (Smem * ACx)
        //if (true) throw new NullPointerException("foo");

        // used in dsplib firlat
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        C55xMemoryAccessOperand Smem = (C55xMemoryAccessOperand)args.get(0);
        Register reg1 = args.get(1).getFirstReg(machine);
        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1); // ACx
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2); // ACy
        
        Microinstruction computeSmem = new C55xComputeSmem(Smem, 0);
        ReadMem readSmem = new ReadMem(machine.getAddressResult(0),
                                       machine.getDataResult(0));
        readSmem.setSignExtend(ReadMem.sxSigned); // not SXMD, but signed

        Microinstruction extract = new ExtractSignExtend(mRegOp1, 17, 16, 1); // ACx(32–16)

        Microinstruction multiply = new MicroArithmetic(machine.getDataResult(0),
                                                        machine.getDataResult(1), "MUL", 2);
        Microinstruction add = new MicroArithmetic(machine.getDataResult(2),
                                                   mRegOp2, "ADD", 3);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(3), mRegOp2);

        this.addMicroinstr(computeSmem);
        this.addMicroinstr(readSmem);
        this.addMicroinstr(extract);
        this.addMicroinstr(multiply);
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg);
        
    }

    public void make_MACM_p263_Instr(Machine machine) { // kps quality code
        //      MACM[R] [T3 = ]Smem, Tx, [ACx,] ACy
        // e.g. MACM *abs16(#0804ah),T1,AC1,AC0
        // This instruction performs a multiplication and an accumulation in the D-unit
        // MAC. The input operands of the multiplier are the content of Tx, sign extended
        // to 17 bits, and the content of a memory (Smem) location, sign extended to
        // 17 bits:
        // ACy = ACx + (Tx * Smem)

        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        C55xMemoryAccessOperand Smem = (C55xMemoryAccessOperand)args.get(0);
        Register Tx  = args.get(1).getFirstReg(machine);
        Register ACx = args.get(2).getFirstReg(machine);
        Register ACy = args.get(3).getFirstReg(machine);
        C55xMicroRegisterOperand mTx  = new C55xMicroRegisterOperand(Tx);
        C55xMicroRegisterOperand mACx = new C55xMicroRegisterOperand(ACx);
        C55xMicroRegisterOperand mACy = new C55xMicroRegisterOperand(ACy);
        
        Microinstruction computeSmem = new C55xComputeSmem(Smem, 0);
        ReadMem readSmem = new ReadMem(machine.getAddressResult(0),
                                       machine.getDataResult(0));
        readSmem.setSignExtend(ReadMem.sxSigned); // not SXMD, but signed
        Register reg3 = machine.getRegister("t3");
        C55xMicroRegisterOperand mRegOp3 = new C55xMicroRegisterOperand(reg3);
        Microinstruction writeT3 = new WriteReg(machine.getDataResult(0), mRegOp3);

        Microinstruction extract = new ExtractSignExtend(mTx, 16, 0, 1); // Tx sign extended

        Microinstruction multiply = new MicroArithmetic(machine.getDataResult(0),
                                                        machine.getDataResult(1), "MUL", 2);
        Microinstruction add = new MicroArithmetic(machine.getDataResult(2),
                                                   mACx, "ADD", 3);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(3), mACy);

        this.addMicroinstr(computeSmem);
        this.addMicroinstr(readSmem);
        if (Smem.hasT3EQ())
            this.addMicroinstr(writeT3);
        this.addMicroinstr(extract);
        this.addMicroinstr(multiply);
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg);
    }

    public void make_MACMK_p264_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side        
        List<Operand> args = this.getArgs();
        
        C55xMemoryAccessOperand memAccessOp1 = (C55xMemoryAccessOperand)args.get(0);
        
        MicroOperand constant = new Constant(args.get(1).getValue(),8);
        
        
        Register reg1 = args.get(2).getFirstReg(machine);
        Register reg2 = args.get(3).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        
        
        Microinstruction memAccess1 = new C55xComputeSmem(memAccessOp1,0);
        Microinstruction readMem1 = new ReadMem(machine.getAddressResult(0), machine.getDataResult(0));
        Microinstruction multiply = new MicroArithmetic(machine.getDataResult(0), constant, 
                                                        "MUL", 0);
        Microinstruction add      = new MicroArithmetic(machine.getDataResult(0), mRegOp1, "ADD",0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp2);
        
        this.addMicroinstr(memAccess1);
        this.addMicroinstr(readMem1);
        this.addMicroinstr(multiply);
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg);

    } 

    public void make_MACM_p265_Instr(Machine machine) {

        // WORKS: used in dsplib fir
        // WORKS: used in dsplib convol

        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();

        C55xMemoryAccessOperand Xmem = (C55xMemoryAccessOperand)args.get(0);
        C55xMemoryAccessOperand Ymem = (C55xMemoryAccessOperand)args.get(1);

        Register reg1 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        Register reg2 = args.get(3).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

            Microinstruction computeXmem = new C55xComputeSmem(Xmem, 0);
        ReadMem readXmem = new ReadMem(machine.getAddressResult(0),
                                       machine.getDataResult(0));
        if (!Xmem.hasUnsMod()) readXmem.setSignExtend(ReadMem.sxUseSXMD);
    
        Microinstruction computeYmem = new C55xComputeSmem(Ymem, 1);
        ReadMem readYmem = new ReadMem(machine.getAddressResult(1),
                                       machine.getDataResult(1));
        if (!Ymem.hasUnsMod()) readYmem.setSignExtend(ReadMem.sxUseSXMD);

        Microinstruction multiply = new MicroArithmetic(machine.getDataResult(0),
                                                        machine.getDataResult(1), 
                                                        "MUL", 2);
        Microinstruction add = new MicroArithmetic(machine.getDataResult(2),
                                                   mRegOp1, "ADD", 3);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(3), mRegOp2);

        this.addMicroinstr(computeXmem);
        this.addMicroinstr(readXmem);
        this.addMicroinstr(computeYmem);
        this.addMicroinstr(readYmem);
        this.addMicroinstr(multiply);
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg);
    }


    public void make_MACM_p267_Instr(Machine machine) { // kps quality code
        // MACM[R][40] [T3 = ][uns(]Xmem[)], [uns(]Ymem[)], ACx >> #16[, ACy]
        // e.g. MACM *(AR0+T1),*(AR1+T0),AC0 >> #16,AC0
        // This instruction performs a multiplication and an accumulation in the D-unit
        // MAC. The input operands of the multiplier are the content of data memory
        // operand Xmem, extended to 17 bits, and the content of data memory operand
        // Ymem, extended to 17 bits:
        // ACy = (ACx >> #16) + (Xmem * Ymem)
        // used in dsplib mul32
        //if (true) throw new NullPointerException("foo");
        List<Operand> args = this.getArgs();
        C55xMemoryAccessOperand Xmem = (C55xMemoryAccessOperand)args.get(0);
        C55xMemoryAccessOperand Ymem = (C55xMemoryAccessOperand)args.get(1);
        C55xShiftOperand shiftOp = (C55xShiftOperand)args.get(2);
        Register reg = args.get(3).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp = new C55xMicroRegisterOperand(reg);

        Microinstruction XmemAccess = new C55xComputeSmem(Xmem, 0);
        Microinstruction readXmem = new ReadMem(machine.getAddressResult(0),
                                                machine.getDataResult(0));
        Microinstruction YmemAccess = new C55xComputeSmem(Ymem, 1);
        Microinstruction readYmem = new ReadMem(machine.getAddressResult(1),
                                                machine.getDataResult(1));
        Microinstruction extendX = new ExtractSignExtend(machine.getDataResult(0),
                                                         16, 0, 2);
        Microinstruction extendY = new ExtractSignExtend(machine.getDataResult(1),
                                                         16, 0, 3);
        Microinstruction multiply = new MicroArithmetic(machine.getDataResult(2),
                                                        machine.getDataResult(3),
                                                        "MUL", 4);
        Microinstruction shift = new C55xShift(shiftOp, 0, 0, 5);
        Microinstruction add = new MicroArithmetic(machine.getDataResult(4),
                                                   machine.getDataResult(5),
                                                   "ADD", 6);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(6),
                                                 mRegOp);
        this.addMicroinstr(XmemAccess);
        this.addMicroinstr(readXmem);
        this.addMicroinstr(YmemAccess);
        this.addMicroinstr(readYmem);
        this.addMicroinstr(extendX);
        this.addMicroinstr(extendY);
        this.addMicroinstr(multiply);
        this.addMicroinstr(shift);
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg);
    }

    public void make_MACMZ_p269_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MAC_p272_Instr(Machine machine) { // kps quality code
        // MAC::MAC
        // MAC[R][40] [uns(]Xmem[)], [uns(]Cmem[)], ACx
        // :: MAC[R][40] [uns(]Ymem[)], [uns(]Cmem[)], ACy
        // e.g.
        // MAC *AR1(T0),uns(*CDP-),AC0 :: MAC *(AR1+T0),uns(*CDP-),AC1
        // This instruction performs two parallel multiply and accumulate (MAC)
        // operations in one cycle:
        // ACx = ACx + (Xmem * Cmem)
        // :: ACy = ACy + (Ymem * Cmem)

        // WORKS: used in dsplib fir2

        C55xOperation mac1 = this;
        C55xOperation mac2 = getImplicitlyParallelOperation();
        boolean round = false;

        if (this.isImplicitlyParallel)
            throw new NullPointerException("bug: " + this);

        List<Operand> mac1Args = mac1.getArgs();
        List<Operand> mac2Args = mac2.getArgs();

        // check mac2 opcode
        if (mac2.getMnemonic().equals("MAC")) {
            ; // OK
        } else if (mac2.getMnemonic().equals("MACR")) {
            ; // XXX TODO: rounding not implemented
            //throw new NullPointerException("macr not implemented: " + this + " :: " + mac2);
        } else
            throw new NullPointerException("not mac " + this + " :: " + mac2);

        //MAC Xmem, Cmem, ACx
        C55xMemoryAccessOperand Xmem = (C55xMemoryAccessOperand)mac1Args.get(0);
        C55xMemoryAccessOperand Cmem = (C55xMemoryAccessOperand)mac1Args.get(1);        
        Register reg1 = mac1Args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        Microinstruction XmemAccess = new C55xComputeSmem(Xmem, 0);
        ReadMem readXmem = new ReadMem(machine.getAddressResult(0),
                                       machine.getDataResult(0));
        if (!Xmem.hasUnsMod()) readXmem.setSignExtend(ReadMem.sxUseSXMD);

        Microinstruction CmemAccess = new C55xComputeSmem(Cmem, 1);
        ReadMem readCmem = new ReadMem(machine.getAddressResult(1),
                                       machine.getDataResult(1));
        if (!Cmem.hasUnsMod()) readCmem.setSignExtend(ReadMem.sxUseSXMD);

        Microinstruction multiply1 = new MicroArithmetic(machine.getDataResult(0),
                                                         machine.getDataResult(1),
                                                         "MUL", 2);

        Microinstruction add1 = new MicroArithmetic(machine.getDataResult(2), mRegOp1,
                                                    "ADD", 3);

        Microinstruction writeReg1 = new WriteReg(machine.getDataResult(3),
                                                  mRegOp1);

        this.addMicroinstr(XmemAccess);
        this.addMicroinstr(readXmem);
        this.addMicroinstr(CmemAccess);
        this.addMicroinstr(readCmem);
        this.addMicroinstr(multiply1);
        this.addMicroinstr(add1);
        this.addMicroinstr(writeReg1);

        //MAC Ymem, Cmem, ACy
        C55xMemoryAccessOperand Ymem = (C55xMemoryAccessOperand)mac2Args.get(0);
        // Cmem from mac1
        Register reg2 = mac2Args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

        Microinstruction YmemAccess = new C55xComputeSmem(Ymem, 2);
        ReadMem readYmem = new ReadMem(machine.getAddressResult(2),
                                       machine.getDataResult(4));
        if (!Ymem.hasUnsMod()) readYmem.setSignExtend(ReadMem.sxUseSXMD);

        Microinstruction multiply2 = new MicroArithmetic(machine.getDataResult(1),
                                                         machine.getDataResult(4),
                                                         "MUL", 5);
        Microinstruction add2 = new MicroArithmetic(machine.getDataResult(5), mRegOp2,
                                                    "ADD", 6);
        Microinstruction writeReg2 = new WriteReg(machine.getDataResult(6), mRegOp2);

        this.addMicroinstr(YmemAccess);
        this.addMicroinstr(readYmem);
        this.addMicroinstr(multiply2);
        this.addMicroinstr(add2);
        this.addMicroinstr(writeReg2);
    } 
    

    public void make_MAC_p274_Instr(Machine machine) {
        // MAC::MAC
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }
    
    public void make_MAC_p276_Instr(Machine machine) { // kps quality code
        // MAC::MAC
        // MAC[R][40] [uns(]Xmem[)], [uns(]Cmem[)], ACx >> #16
        // :: MAC[R][40] [uns(]Ymem[)], [uns(]Cmem[)], ACy >> #16
        // e.g.
        // MAC *AR1,*(CDP+T0),AC0 >> #16 :: MAC *AR1(T0),*(CDP+T0),AC1 >> #16
        // This instruction performs two parallel multiply and accumulate (MAC)
        // operations in one cycle:
        // ACx = (ACx >> #16) + (Xmem * Cmem)
        // :: ACy = (ACy >> #16) + (Ymem * Cmem)
        C55xOperation mac1 = this;
        C55xOperation mac2 = getImplicitlyParallelOperation();

        //System.out.println("MAC::MAC for " + this + " :: " + mac2);

        if (this.isImplicitlyParallel)
            throw new NullPointerException("bug: " + this);

        List<Operand> mac1Args = mac1.getArgs();
        List<Operand> mac2Args = mac2.getArgs();

        // check mac2 opcode
        if (!mac2.getMnemonic().equals("MAC"))
            throw new NullPointerException("not mac " + mac2);

        // MAC[R][40] [uns(]Xmem[)], [uns(]Cmem[)], ACx >> #16
        C55xMemoryAccessOperand Xmem = (C55xMemoryAccessOperand)mac1Args.get(0);
        C55xMemoryAccessOperand Cmem = (C55xMemoryAccessOperand)mac1Args.get(1);        

        C55xShiftOperand shiftOp1 = (C55xShiftOperand)mac1Args.get(2);
        C55xRegisterOperand regOp1 = (C55xRegisterOperand)shiftOp1.getOp1();
        Register reg1 = regOp1.getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        Microinstruction XmemAccess = new C55xComputeSmem(Xmem, 0);
        Microinstruction readXmem = new ReadMem(machine.getAddressResult(0),
                                                machine.getDataResult(0));

        Microinstruction CmemAccess = new C55xComputeSmem(Cmem, 1);
        Microinstruction readCmem = new ReadMem(machine.getAddressResult(1),
                                                machine.getDataResult(1));

        Microinstruction multiply1 = new MicroArithmetic(machine.getDataResult(0),
                                                         machine.getDataResult(1),
                                                         "MUL", 2);
        Microinstruction shift1 = new C55xShift(shiftOp1, 0, 0, 3);

        Microinstruction add1 = new MicroArithmetic(machine.getDataResult(2),
                                                    machine.getDataResult(3),
                                                    "ADD", 4);

        Microinstruction writeReg1 = new WriteReg(machine.getDataResult(4),
                                                  mRegOp1);

        this.addMicroinstr(XmemAccess);
        this.addMicroinstr(readXmem);
        this.addMicroinstr(CmemAccess);
        this.addMicroinstr(readCmem);
        this.addMicroinstr(multiply1);
        this.addMicroinstr(shift1);
        this.addMicroinstr(add1);
        this.addMicroinstr(writeReg1);

        // MAC[R][40] [uns(]Ymem[)], [uns(]Cmem[)], ACy >> #16
        C55xMemoryAccessOperand Ymem = (C55xMemoryAccessOperand)mac2Args.get(0);
        // Cmem from mac1
        C55xShiftOperand shiftOp2 = (C55xShiftOperand)mac2Args.get(2);
        C55xRegisterOperand regOp2 = (C55xRegisterOperand)shiftOp2.getOp1();
        Register reg2 = regOp2.getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

        Microinstruction YmemAccess = new C55xComputeSmem(Ymem, 3);
        Microinstruction readYmem = new ReadMem(machine.getAddressResult(2),
                                                machine.getDataResult(2));

        Microinstruction multiply2 = new MicroArithmetic(machine.getDataResult(2),
                                                         machine.getDataResult(1),
                                                         "MUL", 4);
        Microinstruction shift2 = new C55xShift(shiftOp2, 0, 0, 5);
        Microinstruction add2 = new MicroArithmetic(machine.getDataResult(4),
                                                    machine.getDataResult(5),
                                                    "ADD", 6);
        Microinstruction writeReg2 = new WriteReg(machine.getDataResult(6), mRegOp2);

        this.addMicroinstr(YmemAccess);
        this.addMicroinstr(readYmem);
        this.addMicroinstr(multiply2);
        this.addMicroinstr(shift2);
        this.addMicroinstr(add2);
        this.addMicroinstr(writeReg2);
    }

    public void make_MAC_p278_Instr(Machine machine) {
        // MAC::MPY
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MACM_p281_Instr(Machine machine) {
        // MACM::MOV
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MACM_p283_Instr(Machine machine) {
        // MACM::MOV
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MANT_p285_Instr(Machine machine) { // kps quality code
        // MANT::NEXP
        // e.g. MANT AC0,AC1 :: NEXP AC0,T1, in arct2_t.dis
        C55xOperation mant = this;
        C55xOperation nexp = getImplicitlyParallelOperation();
        List<Operand> mantArgs = mant.getArgs();
        List<Operand> nexpArgs = nexp.getArgs();
        if (!nexp.getMnemonic().equals("NEXP"))
            throw new NullPointerException("not nexp: " + nexp);

        Register ACx = mantArgs.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOpACx = new C55xMicroRegisterOperand(ACx);
        Register ACy = mantArgs.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOpACy = new C55xMicroRegisterOperand(ACy);
        Register Tx =  nexpArgs.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOpTx  = new C55xMicroRegisterOperand(Tx);

        Microinstruction mantissa = new C55xMantissa(mRegOpACx, 0);
        Microinstruction writeACy = new WriteReg(machine.getDataResult(0), mRegOpACy);
        Microinstruction exponent = new C55xExponent(mRegOpACx, "NEXP", 1);
        Microinstruction writeTx  = new WriteReg(machine.getDataResult(1), mRegOpTx);

        this.addMicroinstr(mantissa);
        this.addMicroinstr(writeACy);
        this.addMicroinstr(exponent);
        this.addMicroinstr(writeTx);
    }

    public void make_NEXP_p285_Instr(Machine machine) {
        // MANT::NEXP
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MAS_p288_Instr(Machine machine) {
        // MAS: check in any case
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MASM_p290_Instr(Machine machine) {
        // MASM: check in any case
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MASM_p292_Instr(Machine machine) {
        // MASM: check in any case
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MASM_p293_Instr(Machine machine) {
        // MASM: check in any case
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MASM_p294_Instr(Machine machine) { // kps quality code
        // MASM: check in any case
        checkImplicitParallelism();
        // MASM[R][40] [T3 = ][uns(]Xmem[)], [uns(]Ymem[)], [ACx,] ACy
        // ACy = ACx – (Xmem * Ymem)

        List<Operand> args = this.getArgs();
        C55xMemoryAccessOperand c55xSmem1 = (C55xMemoryAccessOperand)args.get(0);
        C55xMemoryAccessOperand c55xSmem2 = (C55xMemoryAccessOperand)args.get(1);        
        Register reg1 = args.get(2).getFirstReg(machine);
        Register reg2 = args.get(3).getFirstReg(machine);
        
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        
        Microinstruction c55xSmemAccess1 = new C55xComputeSmem(c55xSmem1,0);
        Microinstruction readMem1  = new ReadMem(machine.getAddressResult(0), machine.getDataResult(0));
        Microinstruction c55xSmemAccess2 = new C55xComputeSmem(c55xSmem2,1);
        Microinstruction readMem2  = new ReadMem(machine.getAddressResult(1), machine.getDataResult(1));
        Microinstruction multiply = new MicroArithmetic(machine.getDataResult(0), machine.getDataResult(1), "MUL", 2);
        Microinstruction sub      = new MicroArithmetic(machine.getDataResult(2), mRegOp1, "SUB",3);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(3), mRegOp2);
        
        this.addMicroinstr(c55xSmemAccess1);
        this.addMicroinstr(readMem1);
        this.addMicroinstr(c55xSmemAccess2);
        this.addMicroinstr(readMem2);
        this.addMicroinstr(multiply);
        this.addMicroinstr(sub);
        this.addMicroinstr(writeReg);
    } 

    public void make_MAS_p297_Instr(Machine machine) { // kps quality code
        // MAS::MAC
        // MAS[R][40] [uns(]Xmem[)], [uns(]Cmem[)], ACx
        // :: MAC[R][40] [uns(]Ymem[)], [uns(]Cmem[)], ACy
        // e.g.
        // MAS Xmem, Cmem, ACx :: MAC Ymem, Cmem, ACy
        // This instruction performs two parallel operations in one cycle: multiply and
        // subtract (MAS), and multiply and accumulate (MAC):
        // ACx = ACx – (Xmem * Cmem)
        // :: ACy = ACy + (Ymem * Cmem)
        C55xOperation mas = this;
        C55xOperation mac = getImplicitlyParallelOperation();

        //System.out.println("MAS::MAC for " + this + " :: " + mac);

        if (this.isImplicitlyParallel)
            throw new NullPointerException("bug: " + this);

        List<Operand> masArgs = mas.getArgs();
        List<Operand> macArgs = mac.getArgs();

        // check mac opcode
        //if (!mac.getMnemonic().equals("MAC"))
        if (!mac.getMnemonic().equals("MACR")) {
            System.out.println("WARNING: MAS::MACR for " + this + " :: " + mac);
            // XXX TODO: implement rounding
        }
            
        //throw new NullPointerException("not mac " + mac);

        // mas
        C55xMemoryAccessOperand Xmem = (C55xMemoryAccessOperand)masArgs.get(0);
        C55xMemoryAccessOperand Cmem = (C55xMemoryAccessOperand)masArgs.get(1);        
        Register reg1 = masArgs.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        Microinstruction XmemAccess = new C55xComputeSmem(Xmem, 0);
        ReadMem readXmem = new ReadMem(machine.getAddressResult(0),
                                       machine.getDataResult(0));
        if (!Xmem.hasUnsMod()) readXmem.setSignExtend(ReadMem.sxUseSXMD);
        Microinstruction CmemAccess = new C55xComputeSmem(Cmem, 1);
        ReadMem readCmem = new ReadMem(machine.getAddressResult(1),
                                       machine.getDataResult(1));
        if (!Cmem.hasUnsMod()) readCmem.setSignExtend(ReadMem.sxUseSXMD);
        Microinstruction multiply = new MicroArithmetic(machine.getDataResult(0),
                                                        machine.getDataResult(1),
                                                        "MUL", 2);
        Microinstruction sub      = new MicroArithmetic(machine.getDataResult(2), mRegOp1,
                                                        "SUB", 3);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(3), mRegOp1);

        this.addMicroinstr(XmemAccess);
        this.addMicroinstr(readXmem);
        this.addMicroinstr(CmemAccess);
        this.addMicroinstr(readCmem);
        this.addMicroinstr(multiply);
        this.addMicroinstr(sub);
        this.addMicroinstr(writeReg);

        // mac
        C55xMemoryAccessOperand Ymem = (C55xMemoryAccessOperand)macArgs.get(0);
        // Cmem from mas
        Register reg2 = macArgs.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

        Microinstruction YmemAccess = new C55xComputeSmem(Ymem, 2);
        ReadMem readYmem = new ReadMem(machine.getAddressResult(2),
                                       machine.getDataResult(4));
        if (!Ymem.hasUnsMod()) readYmem.setSignExtend(ReadMem.sxUseSXMD);

        Microinstruction multiply2 = new MicroArithmetic(machine.getDataResult(1),
                                                         machine.getDataResult(4),
                                                         "MUL", 5);
        Microinstruction add = new MicroArithmetic(machine.getDataResult(5), mRegOp2,
                                                   "ADD", 6);
        Microinstruction writeReg2 = new WriteReg(machine.getDataResult(6), mRegOp2);

        this.addMicroinstr(YmemAccess);
        this.addMicroinstr(readYmem);
        this.addMicroinstr(multiply2);
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg2);
    }

    public void make_MAS_p299_Instr(Machine machine) {
        // MAS::MAC
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MAS_p301_Instr(Machine machine) {
        // MAS::MAS
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MAS_p304_Instr(Machine machine) {
        // MAS::MPY
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MASM_p307_Instr(Machine machine) {
        // MASM::MOV
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MASM_p309_Instr(Machine machine) {
        // MASM::MOV
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MAX_p311_Instr(Machine machine) {
        // e.g. MAX AC1,AC0
        // MAX [src,] dst
        // exists in arct2_t.dis
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();

        Register src = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand msrc = new C55xMicroRegisterOperand(src);
        Register dst = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mdst = new C55xMicroRegisterOperand(dst);

        Microinstruction max = new C55xMax(msrc, mdst, 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mdst);

        this.addMicroinstr(max);
        this.addMicroinstr(writeReg);
    }

    public void make_MAXDIFF_p315_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_DMAXDIFF_p318_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MIN_p320_Instr(Machine machine) { // kps quality code
        checkImplicitParallelism(); // to be on safe side
        // e.g. MIN T1,T0
        // exists in bexp_t.dis
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();

        Register src = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand msrc = new C55xMicroRegisterOperand(src);
        Register dst = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mdst = new C55xMicroRegisterOperand(dst);

        Microinstruction min = new C55xMin(msrc, mdst, 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mdst); 

        this.addMicroinstr(min);
        this.addMicroinstr(writeReg);
    }

    public void make_MINDIFF_p324_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_DMINDIFF_p327_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_MMAP_p329_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MOV_p332_Instr(Machine machine) { // kps quality code
        // MOV *(#0000ch) << T1,AC1
        // MOV [rnd(]Smem << Tx[)], ACx
        // This instruction loads the content of a
        // memory (Smem) location shifted by the
        // content of Tx to the accumulator (ACx):
        // ACx = Smem << Tx
        List<Operand> args = this.getArgs();
        C55xShiftOperand c55xShiftOp = (C55xShiftOperand)args.get(0);
        C55xMemoryAccessOperand c55xSmemOp = (C55xMemoryAccessOperand)c55xShiftOp.getOp1();
        Register Tx = c55xShiftOp.getOp2().getFirstReg(machine);
        C55xMicroRegisterOperand mRegOpTx = new C55xMicroRegisterOperand(Tx);
        Register ACx = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOpACx = new C55xMicroRegisterOperand(ACx);

        Microinstruction smemAccess = new C55xComputeSmem(c55xSmemOp, 0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0),
                                               machine.getDataResult(0));
        Microinstruction shift = new Shift(machine.getDataResult(0), mRegOpTx, "AS", 1);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(1), mRegOpACx);

        this.addMicroinstr(smemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(shift);
        this.addMicroinstr(writeReg);
    }

    public void make_MOV_p333_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_MOV_p334_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MOV_p335_Instr(Machine machine) { // kps quality code
        checkImplicitParallelism(); // to be on safe side
        // MOV Smem << #16, ACx
        List<Operand> args = this.getArgs();
        C55xShiftOperand c55xShift = (C55xShiftOperand)args.get(0);
        C55xMemoryAccessOperand Smem = (C55xMemoryAccessOperand)c55xShift.getOp1();

        Register ACx = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand ACxMRegOp = new C55xMicroRegisterOperand(ACx);

        Microinstruction computeSmem = new C55xComputeSmem(Smem, 0);
        ReadMem readSmem = new ReadMem(machine.getAddressResult(0),
                                      machine.getDataResult(0));
        if (!Smem.hasUnsMod()) readSmem.setSignExtend(ReadMem.sxUseSXMD);

        Microinstruction shift = new Shift(machine.getDataResult(0),
                                           new Constant(16,16), "AS", 1);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(1), ACxMRegOp);
        
        this.addMicroinstr(computeSmem);
        this.addMicroinstr(readSmem);
        this.addMicroinstr(shift);
        this.addMicroinstr(writeReg);
    }


    public void make_MOV_p336_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // MOV [uns(]Smem[)], ACx
        List<Operand> args = this.getArgs();
        C55xMemoryAccessOperand Smem = (C55xMemoryAccessOperand)args.get(0);
        Register ACx = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mACx = new C55xMicroRegisterOperand(ACx);
        
        Microinstruction computeSmem = new C55xComputeSmem(Smem, 0);
        ReadMem readSmem = new ReadMem(machine.getAddressResult(0), mACx);
        if (!Smem.hasUnsMod()) readSmem.setSignExtend(ReadMem.sxUseSXMD);

        this.addMicroinstr(computeSmem);
        this.addMicroinstr(readSmem);
    }



    public void make_MOV_p337_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // MOV [uns(]Smem[)] << #SHIFTW, ACx
        List<Operand> args = this.getArgs();

        C55xShiftOperand c55xShift = (C55xShiftOperand)args.get(0);
        C55xMemoryAccessOperand Smem = (C55xMemoryAccessOperand)c55xShift.getOp1();
        C55xImmediateOperand imm = (C55xImmediateOperand)c55xShift.getOp2();

        Register reg = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp = new C55xMicroRegisterOperand(reg);

        Microinstruction computeSmem = new C55xComputeSmem(Smem, 0);
        ReadMem readSmem = new ReadMem(machine.getAddressResult(0),
                                      machine.getDataResult(0));
        if (!Smem.hasUnsMod()) readSmem.setSignExtend(ReadMem.sxUseSXMD);
        
        Microinstruction shift = new Shift(machine.getDataResult(0),
                                           new Constant(imm.getValue(),6), "AS", 1);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(1),
                                                 mRegOp);
        
        this.addMicroinstr(computeSmem);
        this.addMicroinstr(readSmem);
        this.addMicroinstr(shift);
        this.addMicroinstr(writeReg);
    }



public void make_MOV_p338_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // MOV[40] dbl(Lmem), ACx
        List<Operand> args = this.getArgs();
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);

        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);

        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction readMemDouble = new ReadMemDouble(machine.getAddressResult(0), mRegOp1);

        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMemDouble);
 }

    // implemented by kps, same as above
    public void  make_MOV40_p338_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side 
        // MOV40 dbl(Lmem), ACx
        List<Operand> args = this.getArgs();
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);

        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);

        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction readMemDouble = new ReadMemDouble(machine.getAddressResult(0), mRegOp1);

        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMemDouble);
    }

    public void make_MOV_p339_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
            
 
    public void make_MOV_p341_Instr(Machine machine) { // kps quality code
        checkImplicitParallelism(); // to be on safe side
        // MOV dbl(Lmem), pair(HI(ACx))

        // This instruction loads the 16 highest bits of data memory
        // operand (Lmem) to the 16 highest bits of the accumulator
        // (ACx) and loads the 16 lowest bits of data memory operand
        // (Lmem) to the 16 highest bits of accumulator AC(x + 1):
        // pair(HI(ACx)) = Lmem

        List<Operand> args = this.getArgs();
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);
        Register reg = args.get(1).getFirstReg(machine);
        Register reg1 = null;
        Register reg2 = null;
        String regName = reg.getName();

        // - Valid accumulators are AC0 and AC2.
        if (regName.equalsIgnoreCase("ac0")) {
            reg1 = machine.getRegister("ac0h"); // 16 hi bits here
            reg2 = machine.getRegister("ac1h"); // 16 lo bits here
        }
        else if (regName.equalsIgnoreCase("ac2")) {
            reg1 = machine.getRegister("ac2h"); // 16 hi bits here
            reg2 = machine.getRegister("ac3h"); // 16 lo bits here
        }
        else
            throw new NullPointerException("illegal register " + regName + " in " + this);

        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction readMemDouble = new ReadMemDouble(machine.getAddressResult(0),
                                                           machine.getDataResult(0));
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

        Constant shiftVal = new Constant(-16, 16);
        Microinstruction getHi = new Shift(machine.getDataResult(0), shiftVal, "LS", 1);
        Constant mask = new Constant(0xffff, 16);
        Microinstruction getLo = new Logical(machine.getDataResult(0), mask, "AND", 2);

        // kps TODO, use improved WriteReg if possible
        Microinstruction writeReg1 = new WriteReg(machine.getDataResult(1), mRegOp1);
        Microinstruction writeReg2 = new WriteReg(machine.getDataResult(2), mRegOp2);

        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMemDouble);
        this.addMicroinstr(getHi);
        this.addMicroinstr(getLo);
        this.addMicroinstr(writeReg1);
        this.addMicroinstr(writeReg2);
    }


    public void make_MOV_p342_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_MOV_p344_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

public void make_MOV_p345_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // MOV K16 << #SHFT, ACx
        List<Operand> args = this.getArgs();
        C55xShiftOperand c55xShift = (C55xShiftOperand)args.get(0);
        Register reg1 = args.get(1).getFirstReg(machine);
        
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);

        Microinstruction shift = new C55xShift(c55xShift, c55xShift.getOp1().getValue(), c55xShift.getOp2().getValue(), 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp1);
        
        this.addMicroinstr(shift);
        this.addMicroinstr(writeReg);
}

public void make_MOV_p347_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // MOV Smem,dst

        List<Operand> args = this.getArgs();

        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);
    
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);   
        

        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem,0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0), machine.getDataResult(0));
        Microinstruction writeReg  = new WriteReg(machine.getDataResult(0), mRegOp1);     
        
        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(writeReg);
}


    public void make_MOV_p348_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_MOV_p350_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

public void make_MOV_p353_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // MOV k4,dst /-k4,dst / k16,dst
        List<Operand> args = this.getArgs();
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        MicroOperand constant = new Constant(args.get(0).getValue(),16);
        Microinstruction writeReg  = new WriteReg(constant, mRegOp1);
        
        this.addMicroinstr(writeReg);
} 


    public void make_MOV_p356_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MOV_p359_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // MOV Smem, reg
        List<Operand> args = this.getArgs();

        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);

        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);

        Microinstruction sMemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0), mRegOp1);

        this.addMicroinstr(sMemAccess);
        this.addMicroinstr(readMem);
    }

    public void  make_MOV_p359_No20_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void  make_MOV_p360_k7_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void  make_MOV_p360_k9_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void  make_MOV_p360_k12_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();        
        MicroOperand constant = new Constant(args.get(0).getValue(),12);
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        Microinstruction writeReg = new WriteReg(constant, mRegOp1);
        this.addMicroinstr(writeReg);
        
    }
    
    public void  make_MOV_p360_k16_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side        
        List<Operand> args = this.getArgs();        
        MicroOperand constant = new Constant(args.get(0).getValue(),16);
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        Microinstruction writeReg = new WriteReg(constant, mRegOp1);
        
        this.addMicroinstr(writeReg); 
    } 


    public void make_MOV_p362_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // MOVE dbl(Lmem), XAdst
        // -pgm does a full 32 bit write, but should only do 24 bits, fix if necessary, fixed -pgm
        List<Operand> args = this.getArgs();
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);

        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);

        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction readMemDouble = new ReadMemDouble(machine.getAddressResult(0),7,0,0,0, mRegOp1);
        
        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMemDouble);

    } 


public void make_MOV_p363_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // MOV [k8/k16, Smem]
        List<Operand> args = this.getArgs();
        MicroOperand constant = new Constant(args.get(0).getValue(),16);
        C55xMemoryAccessOperand c55xMem = (C55xMemoryAccessOperand)args.get(1);

        Microinstruction c55xMemAccess = new C55xComputeSmem(c55xMem, 0);
        Microinstruction writeMem = new WriteMem(constant, machine.getAddressResult(0));

        this.addMicroinstr(c55xMemAccess);
        this.addMicroinstr(writeMem); 
} 

public void make_MOV_p364_Instr(Machine machine) { 
            checkImplicitParallelism(); // to be on safe side
        // MOV HI(ACx), TAx
        List<Operand> args = this.getArgs();
        C55xRegisterOperand regOp = (C55xRegisterOperand)args.get(0);
        
        Register reg1 = args.get(0).getFirstReg(machine);
        Register reg2 = args.get(1).getFirstReg(machine);

        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        Microinstruction writeReg = new WriteReg(mRegOp1, 16, 16, mRegOp2);
        this.addMicroinstr(writeReg);
        // XXX TODO: test
}

public void make_MOV_p365_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // MOV src,dst
        
        List<Operand> args = this.getArgs();
        
        Register reg1 = args.get(0).getFirstReg(machine);
        Register reg2 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        Microinstruction writeReg  = new WriteReg(mRegOp1, mRegOp2);
        
        this.addMicroinstr(writeReg);
} 

    public void make_MOV_p367_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // MOV TAx, HI(ACx)
        // e.g. MOV T0,HI(AC0)
        // This instruction moves the content of the auxiliary or temporary register (TAx)
        // to the high part of the accumulator, ACx(31–16):
        // HI(ACx) = TAx
        List<Operand> args = this.getArgs();
        Register reg1 = args.get(0).getFirstReg(machine);
        Register reg2 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        Microinstruction writeReg = new WriteReg(mRegOp1, mRegOp2, 16, 16);
        this.addMicroinstr(writeReg);
    }

public void make_MOV_p368_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
       // MOV src,dst
        List<Operand> args = this.getArgs();
        Register reg1 = args.get(0).getFirstReg(machine);
        Register reg2 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        Microinstruction writeReg = new WriteReg(mRegOp1, mRegOp2);
        this.addMicroinstr(writeReg);
}

    public void make_MOV_p370_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        Register reg1 = args.get(0).getFirstReg(machine);
        Register reg2 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        Microinstruction writeReg  = new WriteReg(mRegOp1, mRegOp2);
        
        this.addMicroinstr(writeReg);
    } 


    public void make_MOV_p372_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        Register reg1 = args.get(0).getFirstReg(machine);
        Register reg2 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        Microinstruction writeReg  = new WriteReg(mRegOp1, mRegOp2);
        
        this.addMicroinstr(writeReg);
        
    } 
    

    public void make_MOV_p374_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_MOV_p375_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_MOV_p376_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_MOV_p377_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_MOV_p378_Instr(Machine machine) {
        // MOV dbl(Xmem), dbl(Ymem)
        // e.g. MOV dbl(*AR3),dbl(*(AR4+T1))
        // dbl(Ymem) = dbl(Xmem)
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();

        C55xMemoryAccessOperand dblXMem = (C55xMemoryAccessOperand)args.get(0);
        C55xMemoryAccessOperand dblYMem = (C55xMemoryAccessOperand)args.get(1);

        Microinstruction xMemAccess = new C55xComputeSmem(dblXMem, 0);
        Microinstruction readMemDouble = new ReadMemDouble(machine.getAddressResult(0), 
                                                           machine.getDataResult(0));
        Microinstruction yMemAccess = new C55xComputeSmem(dblYMem, 1);
        Microinstruction writeMemDouble = new WriteMemDouble(machine.getDataResult(0),
                                                             machine.getAddressResult(1));

        this.addMicroinstr(xMemAccess);
        this.addMicroinstr(readMemDouble);
        this.addMicroinstr(yMemAccess);
        this.addMicroinstr(writeMemDouble);
    }

    public void make_MOV_p379_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // MOV *AR5, *AR3
        List<Operand> args = this.getArgs();        
        C55xMemoryAccessOperand sMem1 = (C55xMemoryAccessOperand)args.get(0);
        C55xMemoryAccessOperand sMem2 = (C55xMemoryAccessOperand)args.get(1);

        Microinstruction sMemAccess1  = new C55xComputeSmem(sMem1,0);
        Microinstruction readMem      = new ReadMem(machine.getAddressResult(0), machine.getDataResult(0));
        Microinstruction sMemAccess2  = new C55xComputeSmem(sMem2,1);
        Microinstruction writeMem     = new WriteMem(machine.getDataResult(0), machine.getAddressResult(1));
     
        this.addMicroinstr(sMemAccess1);
        this.addMicroinstr(readMem);
        this.addMicroinstr(sMemAccess2);
        this.addMicroinstr(writeMem);
    } 

    public void make_MOV_p382_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // MOV HI(ACx), Smem
        List<Operand> args = this.getArgs();
        Register reg1 = args.get(0).getFirstReg(machine);
        C55xRegisterOperand regOp = (C55xRegisterOperand)args.get(0);
        
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMemoryAccessOperand sMem1 = (C55xMemoryAccessOperand)args.get(1);
        
        Microinstruction sMemAccess  = new C55xComputeSmem(sMem1,0);
        Microinstruction writeMem = new WriteMem(mRegOp1, machine.getAddressResult(0), regOp); // pass also Registeroperand for HI etc. info
        this.addMicroinstr(sMemAccess);
        this.addMicroinstr(writeMem);
    }
    
    public void make_MOV_p383_Instr(Machine machine) { // kps quality code
        checkImplicitParallelism(); // to be on safe side
        // MOV [rnd(]HI(ACx)[)], Smem
        // example: MOV rnd(HI(AC0)),*AR2+
        // This instruction stores the high part of the accumulator, ACx(31–16), to the
        // memory (Smem) location:
        // Smem = HI(ACx)
        // Rounding is performed in the D-unit shifter according to RDM, if the optional
        // rnd keyword is applied to the input operand.
        // used in e.g. iir32_t.dis
        List<Operand> args = this.getArgs();
        Register reg1 = args.get(0).getFirstReg(machine);
        C55xRegisterOperand regOp = (C55xRegisterOperand)args.get(0);
        
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMemoryAccessOperand sMem1 = (C55xMemoryAccessOperand)args.get(1);
        
        Microinstruction sMemAccess  = new C55xComputeSmem(sMem1,0);
        // pass also Registeroperand for HI etc. info
        // XXX TODO - support rnd()
        Microinstruction writeMem = new WriteMem(mRegOp1, machine.getAddressResult(0), regOp);
        this.addMicroinstr(sMemAccess);
        this.addMicroinstr(writeMem);
    } 

    public void make_MOV_p384_Instr(Machine machine) { // kps quality code
        checkImplicitParallelism(); // to be on safe side
        // MOV ACx << Tx, Smem
        // Smem = LO(ACx << Tx)
        // MOV AC0 << T2,*AR3+
        // used in e.g. iir32_t.dis
        List<Operand> args = this.getArgs();
        C55xShiftOperand c55xShiftOp = (C55xShiftOperand)args.get(0);
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(1);

        Microinstruction shift = new C55xShift(c55xShiftOp, 0, 0, 0);
        Microinstruction c55xMemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction writeMem = new WriteMem(machine.getDataResult(0),
                                                 16, // 16 bits
                                                 0, // offset
                                                 machine.getAddressResult(0));
        this.addMicroinstr(shift);
        this.addMicroinstr(c55xMemAccess);
        this.addMicroinstr(writeMem);
    }

    public void make_MOV_p385_Instr(Machine machine) { // kps quality code
        checkImplicitParallelism(); // to be on safe side
        // syntax: MOV [rnd(]HI(ACx << Tx)[)], Smem
        // e.g.  : MOV HI(AC0 << T2),*(AR4+T1)
        // This instruction shifts the accumulator, ACx, by the content of Tx and stores
        // high part of the accumulator, ACx(31–16), to the memory (Smem) location:
        // Smem = HI(ACx << Tx)
        // used in e.g. iir32_t.dis

        List<Operand> args = this.getArgs();
        C55xShiftOperand c55xShiftOp = (C55xShiftOperand)args.get(0);
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(1);

        Microinstruction shift = new C55xShift(c55xShiftOp, 0, 0, 0);
        Microinstruction c55xMemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction writeMem = new WriteMem(machine.getDataResult(0),
                                                 16, // HI: ACx(31–16)
                                                 16, // offset 16
                                                 machine.getAddressResult(0));
        this.addMicroinstr(shift);
        this.addMicroinstr(c55xMemAccess);
        this.addMicroinstr(writeMem);
    }

    public void make_MOV_p386_Instr(Machine machine) { // kps quality code
        // MOV ACx << #SHIFTW, Smem
        // e.g. MOV AC1 << #0,@#03h
        // Smem = LO(ACx << #SHIFTW)
        //
        // XXX TODO SXMD
        // This instruction shifts the accumulator, ACx, by the 6-bit value, SHIFTW, and
        // stores the low part of the accumulator, ACx(15–0), to the memory (Smem)
        // location:
        // Smem = LO(ACx << #SHIFTW)
        List<Operand> args = this.getArgs();
        C55xShiftOperand shiftOp = (C55xShiftOperand)args.get(0);
        C55xMemoryAccessOperand Smem = (C55xMemoryAccessOperand)args.get(1);

        Microinstruction shift = new C55xShift(shiftOp, 0, 0, 0);
        Microinstruction smemAccess = new C55xComputeSmem(Smem, 0);
        Microinstruction writeMem = new WriteMem(machine.getDataResult(0),
                                                 16, // 16 bits
                                                 0, // offset
                                                 machine.getAddressResult(0));

        this.addMicroinstr(shift);
        this.addMicroinstr(smemAccess);
        this.addMicroinstr(writeMem);
    }

    public void make_MOV_p387_Instr(Machine machine) {
        //      MOV HI(ACx << #SHIFTW), Smem
        // e.g. MOV HI(AC1 << #0),@#07h
        // This instruction shifts the accumulator, ACx, by the 6-bit value, SHIFTW, and
        // stores the high part of the accumulator, ACx(31–16), to the memory (Smem)
        // location:
        // Smem = HI(ACx << #SHIFTW)
        List<Operand> args = this.getArgs();
        C55xShiftOperand c55xShiftOp = (C55xShiftOperand)args.get(0);
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(1);

        Microinstruction shift = new C55xShift(c55xShiftOp, 0, 0, 0);
        Microinstruction c55xMemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction writeMem = new WriteMem(machine.getDataResult(0),
                                                 16, // HI: ACx(31–16)
                                                 16, // offset 16
                                                 machine.getAddressResult(0));
        
        this.addMicroinstr(shift);
        this.addMicroinstr(c55xMemAccess);
        this.addMicroinstr(writeMem);
    }

    public void make_MOV_p388_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_MOV_p389_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_MOV_p391_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_MOV_p393_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MOV_p395_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // MOV ACx, dbl(Lmem)
        List<Operand> args = this.getArgs();
        
        Register reg1 = args.get(0).getFirstReg(machine);
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(1);
        
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        
        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem,0);
        Microinstruction writeMemDouble = new WriteMemDouble(mRegOp1, machine.getAddressResult(0));
        
        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(writeMemDouble);
    } 

    public void make_MOV_p396_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_MOV_p398_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_MOV_p399_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
   
    public void make_MOV_p401_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        //      MOV pair(HI(ACx)), dbl(Lmem)
        // e.g. MOV [pair(HI(AC0)), dbl(*AR2+)]
        // used in dsplib fir2
        List<Operand> args = this.getArgs();
        
        C55xRegister ACx = (C55xRegister)args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mACx = new C55xMicroRegisterOperand(ACx);
        C55xRegister pair = ACx.getACxPair();
        C55xMicroRegisterOperand mPair = new C55xMicroRegisterOperand(pair);
        C55xMemoryAccessOperand Lmem = (C55xMemoryAccessOperand)args.get(1);

        Microinstruction extrACx =  new ExtractSignExtend(mACx,  16, 16, 0); //  ACx(31–16)
        Microinstruction extrPair = new ExtractSignExtend(mPair, 16, 16, 1); // pair(31–16)

        Microinstruction shift = new Shift(machine.getDataResult(0),
                                           new Constant(16,16), "LS", 2);
        Microinstruction and = new Logical(machine.getDataResult(1),
                                           new Constant(0xffff,16), "AND", 3);
        Microinstruction or = new Logical(machine.getDataResult(2),
                                          machine.getDataResult(3), "OR", 4);
                                          
               Microinstruction computeLmem = new C55xComputeSmem(Lmem, 0);
        Microinstruction writeMemDouble = new WriteMemDouble(machine.getDataResult(4),
                                                             machine.getAddressResult(0));
        this.addMicroinstr(extrACx);
        this.addMicroinstr(extrPair);
        this.addMicroinstr(shift);
        this.addMicroinstr(and);
        this.addMicroinstr(or);
        this.addMicroinstr(computeLmem);
        this.addMicroinstr(writeMemDouble);
    }



 
    public void make_MOV_p402_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // MOV pair(LO(ACx)), dbl(Lmem) 
        
        List<Operand> args = this.getArgs();

        Register reg1 = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(1);
        
        
        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction writeMemDouble = new WriteMemDouble(mRegOp1, machine.getAddressResult(0));
    } 

    public void make_MOV_p404_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        
        List<Operand> args = this.getArgs();
        Register reg1 = args.get(0).getFirstReg(machine);
        
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1, 16);
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(1);
        
        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction writeMem = new WriteMem(mRegOp1, machine.getAddressResult(0));

        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(writeMem);
    } 

    public void make_MOV_p405_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_MOV_p406_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_MOV_p407_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_MOV_p408_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void  make_MOV_p408_No20_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }


   
    // implemented by kps by copying from
    // make_MOV_p395_Instr()
    public void make_MOV_p412_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // MOV XAsrc, dbl(Lmem)
        List<Operand> args = this.getArgs();
        
        Register reg1 = args.get(0).getFirstReg(machine);
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(1);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
     
        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem,0);
        Microinstruction writeMemDouble = new WriteMemDouble(mRegOp1, machine.getAddressResult(0));
        
        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(writeMemDouble);
    } 

    public void make_MOV_p413_Instr(Machine machine) {
        // MOV::MOV
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_MPY_p417_Instr(Machine machine) { // kps quality code
        checkImplicitParallelism(); // to be on safe side
        // MPY[R] [ACx,] ACy
        // MPY AC0,AC1
        // ACy = ACy * ACx
        // This instruction performs a multiplication in the D-unit MAC. The input
        // operands of the multiplier are ACx(32–16) and ACy(32–16):
        
        List<Operand> args = this.getArgs();
        Register ACx = args.get(0).getFirstReg(machine);
        Register ACy = args.get(1).getFirstReg(machine);

        C55xMicroRegisterOperand mACx = new C55xMicroRegisterOperand(ACx);
        C55xMicroRegisterOperand mACy = new C55xMicroRegisterOperand(ACy);

        Microinstruction extractx = new ExtractSignExtend(mACx, 17, 16, 0); // ACx(32–16)
        Microinstruction extracty = new ExtractSignExtend(mACy, 17, 16, 1); // ACy(32–16)
        Microinstruction multiply = new MicroArithmetic(machine.getDataResult(0),
                                                        machine.getDataResult(1), "MUL", 2);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(2), mACy);

        this.addMicroinstr(extractx);
        this.addMicroinstr(extracty);
        this.addMicroinstr(multiply);
        this.addMicroinstr(writeReg);
    }

    public void make_MPY_p418_Instr(Machine machine) { // kps quality code
        checkImplicitParallelism(); // to be on safe side
        // MPY[R] Tx, [ACx,] ACy
        // e.g. MPY T1,AC0,AC1
        // This instruction performs a multiplication in the D-unit MAC. The input
        // operands of the multiplier are ACx(32–16) and the content of Tx, sign
        // extended to 17 bits:
        // ACy = ACx * Tx

        List<Operand> args = this.getArgs();
        Register reg0 = args.get(0).getFirstReg(machine);
        Register reg1 = args.get(1).getFirstReg(machine);
        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp0 = new C55xMicroRegisterOperand(reg0);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

        Microinstruction extend = new ExtractSignExtend(mRegOp0, 16, 0, 0); // Tx
        Microinstruction extract = new ExtractSignExtend(mRegOp1, 17, 16, 1); // ACx(32–16)

        Microinstruction multiply = new MicroArithmetic(machine.getDataResult(0),
                                                        machine.getDataResult(1), "MUL", 2);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(2), mRegOp2);
        this.addMicroinstr(extend);
        this.addMicroinstr(extract);
        this.addMicroinstr(multiply);
        this.addMicroinstr(writeReg);
        
    }
    public void make_MPYK_p420_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();

        MicroOperand constant = new Constant(args.get(0).getValue(),16);
        Register reg1 = args.get(1).getFirstReg(machine);
        Register reg2 = args.get(2).getFirstReg(machine);

        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

        Microinstruction extract  = new ExtractSignExtend(mRegOp1, 16, 16, 0);
        Microinstruction multiply = new MicroArithmetic(constant, machine.getDataResult(0), "MUL", 1);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(1), mRegOp2);
        
        this.addMicroinstr(extract);
        this.addMicroinstr(multiply);
        this.addMicroinstr(writeReg);
    }


    public void make_MPYM_p421_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // MPYM[R] Smem, Cmem, ACx
        List<Operand> args = this.getArgs();   
        C55xMemoryAccessOperand c55xSmem1 = (C55xMemoryAccessOperand)args.get(0);
        C55xMemoryAccessOperand c55xSmem2 = (C55xMemoryAccessOperand)args.get(1);
        Register reg1 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        Microinstruction c55xSmemAccess1 = new C55xComputeSmem(c55xSmem1, 0);
        Microinstruction readMem1 = new ReadMem(machine.getAddressResult(0), machine.getDataResult(0));
        Microinstruction c55xSmemAccess2 = new C55xComputeSmem(c55xSmem2, 1);
        Microinstruction readMem2 = new ReadMem(machine.getAddressResult(1), machine.getDataResult(1));
        Microinstruction multiply = new MicroArithmetic(machine.getDataResult(0),
                                                        machine.getDataResult(1)  , "MUL",2);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(2), mRegOp1);

        this.addMicroinstr(c55xSmemAccess1);
        this.addMicroinstr(readMem1);
        this.addMicroinstr(c55xSmemAccess2);
        this.addMicroinstr(readMem2);
        this.addMicroinstr(multiply);
        this.addMicroinstr(writeReg); 
    } 

    public void make_MPYM_p423_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // MPYM[R] [T3 = ]Smem, [ACx,] ACy
        List<Operand> args = this.getArgs();   
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);
        Register reg1 = args.get(1).getFirstReg(machine);
        Register reg2 = args.get(2).getFirstReg(machine);
    
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
    
        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0), machine.getDataResult(0));
        Microinstruction multiply = new MicroArithmetic(machine.getDataResult(0), mRegOp1, "MUL",1);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(1), mRegOp2);
     
        
        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(multiply);
        this.addMicroinstr(writeReg); 
    } 

    public void make_MPYMK_p424_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        
        List<Operand> args = this.getArgs();
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);

        MicroOperand constant = new Constant(args.get(1).getValue(),8);
        
        Register reg1 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0), machine.getDataResult(0));
        Microinstruction multiply = new MicroArithmetic(machine.getDataResult(0), constant, "MUL",1);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(1), mRegOp1);
        
        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(multiply);
        this.addMicroinstr(writeReg);
        
    } 
    

    public void make_MPYM_p425_Instr(Machine machine) { 

        // WORKS: used in dsplib fir
        // WORKS: used in dsplib convol

        checkImplicitParallelism(); // to be on safe side

        List<Operand> args = this.getArgs();
        C55xMemoryAccessOperand Xmem = (C55xMemoryAccessOperand)args.get(0);
        C55xMemoryAccessOperand Ymem = (C55xMemoryAccessOperand)args.get(1);
        Register reg1 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        Microinstruction XmemAccess = new C55xComputeSmem(Xmem, 0);
        ReadMem readXmem  = new ReadMem(machine.getAddressResult(0),
                                        machine.getDataResult(0));
        if (!Xmem.hasUnsMod()) readXmem.setSignExtend(ReadMem.sxUseSXMD);
        Microinstruction YmemAccess = new C55xComputeSmem(Ymem, 1);
        ReadMem readYmem  = new ReadMem(machine.getAddressResult(1),
                                        machine.getDataResult(1));
        if (!Ymem.hasUnsMod()) readYmem.setSignExtend(ReadMem.sxUseSXMD);
        Microinstruction multiply = new MicroArithmetic(machine.getDataResult(0),
                                                        machine.getDataResult(1), 
                                                        "MUL", 2);    
        Microinstruction writeReg = new WriteReg(machine.getDataResult(2), mRegOp1);

        this.addMicroinstr(XmemAccess);
        this.addMicroinstr(readXmem);
        this.addMicroinstr(YmemAccess);
        this.addMicroinstr(readYmem);
        this.addMicroinstr(multiply);
        this.addMicroinstr(writeReg); 
    }




    public void make_MPYM_p427_Instr(Machine machine) { 
        // MPYM[R][U] [T3 = ]Smem, Tx, ACx
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);
        
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        
        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0),
                                               machine.getDataResult(0));
        //if (true) throw new NullPointerException("foo");
        Microinstruction extendS = new ExtractSignExtend(machine.getDataResult(0),
                                                         16, 0, 1);
        Microinstruction extendT = new ExtractSignExtend(mRegOp1, 16, 0, 2);
        Microinstruction multiply = new MicroArithmetic(machine.getDataResult(1),
                                                        machine.getDataResult(2),
                                                        "MUL", 3);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(3), mRegOp2);
        
        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(extendS);
        this.addMicroinstr(extendT);
        this.addMicroinstr(multiply);
        this.addMicroinstr(writeReg);
        
    }

    
    public void make_MPY_p428_Instr(Machine machine) {
        // MPY::MAC
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this); 
    }
    


    public void make_MPY_p430_Instr(Machine machine) {  // kps quality code
        // MPY::MPY
        // MPY[R][40] [uns(]Xmem[)], [uns(]Cmem[)], ACx
        // :: MPY[R][40] [uns(]Ymem[)], [uns(]Cmem[)], ACy
        // e.g.
        // MPY uns(*AR1),*(CDP+T0),AC0 :: MPY uns(*AR1(T0)),*(CDP+T0),AC1
        // This instruction performs two parallel multiply operations in one cycle:
        // ACx = Xmem * Cmem
        // :: ACy = Ymem * Cmem

        // WORKS: used in dsplib fir2

        C55xOperation mpy1 = this;
        C55xOperation mpy2 = getImplicitlyParallelOperation();

        if (this.isImplicitlyParallel)
            throw new NullPointerException("bug: " + this);

        List<Operand> mpy1Args = mpy1.getArgs();
        List<Operand> mpy2Args = mpy2.getArgs();

        // check mpy2 opcode
        if (!mpy2.getMnemonic().equals("MPY"))
            throw new NullPointerException("not mpy " + mpy2);

        // mpy1
        C55xMemoryAccessOperand Xmem = (C55xMemoryAccessOperand)mpy1Args.get(0);
        C55xMemoryAccessOperand Cmem = (C55xMemoryAccessOperand)mpy1Args.get(1);        
        Register reg1 = mpy1Args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        Microinstruction XmemAccess = new C55xComputeSmem(Xmem, 0);
        ReadMem readXmem = new ReadMem(machine.getAddressResult(0),
                                                machine.getDataResult(0));
        if (!Xmem.hasUnsMod()) readXmem.setSignExtend(ReadMem.sxUseSXMD);

        Microinstruction CmemAccess = new C55xComputeSmem(Cmem, 1);
        ReadMem readCmem = new ReadMem(machine.getAddressResult(1),
                                                machine.getDataResult(1));
        if (!Cmem.hasUnsMod()) readCmem.setSignExtend(ReadMem.sxUseSXMD);

        Microinstruction multiply = new MicroArithmetic(machine.getDataResult(0),
                                                        machine.getDataResult(1),
                                                        "MUL", 2);

        Microinstruction writeReg = new WriteReg(machine.getDataResult(2), mRegOp1);

        this.addMicroinstr(XmemAccess);
        this.addMicroinstr(readXmem);
        this.addMicroinstr(CmemAccess);
        this.addMicroinstr(readCmem);
        this.addMicroinstr(multiply);
        this.addMicroinstr(writeReg);

        // mpy2
        C55xMemoryAccessOperand Ymem = (C55xMemoryAccessOperand)mpy2Args.get(0);
        // Cmem from mpy1
        Register reg2 = mpy2Args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

        Microinstruction YmemAccess = new C55xComputeSmem(Ymem, 2);
        ReadMem readYmem = new ReadMem(machine.getAddressResult(2),
                                       machine.getDataResult(3));
        if (!Ymem.hasUnsMod()) readYmem.setSignExtend(ReadMem.sxUseSXMD);

        Microinstruction multiply2 = new MicroArithmetic(machine.getDataResult(1),
                                                         machine.getDataResult(3),
                                                         "MUL", 4);
        Microinstruction writeReg2 = new WriteReg(machine.getDataResult(4), mRegOp2);

        this.addMicroinstr(YmemAccess);
        this.addMicroinstr(readYmem);
        this.addMicroinstr(multiply2);
        this.addMicroinstr(writeReg2);
    } 


    public void make_MPYM_p432_Instr(Machine machine) {
        // MPYM::MOV
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }

public void make_NEG_p434_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
         // neg [src],dst
        List<Operand> args = this.getArgs();
        Register reg1 = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        Register reg2 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        
        Microinstruction neg = new Neg(mRegOp1, 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp2);  
        
        this.addMicroinstr(neg);
        this.addMicroinstr(writeReg);
} 

    public void make_NOP_p436_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        Microinstruction nop = new Nop();
        this.addMicroinstr(nop);
    } 
    
    
    public void make_NOP16_p436_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        Microinstruction nop = new Nop();
        this.addMicroinstr(nop);
    } 
    
    public void make_NOT_p437_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        
        Register reg1 = args.get(0).getFirstReg(machine);
        Register reg2 = args.get(1).getFirstReg(machine);
        
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        
        Microinstruction not = new Logical(mRegOp1, "NOT", 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp2);
        
        this.addMicroinstr(not);
        this.addMicroinstr(writeReg);
        
    } 
    
    public void make_OR_p439_Instr(Machine machine) { // kps quality code
        // OR src, dst
        // dst = dst | src
        // e.g. OR AC1,AC0
        List<Operand> args = this.getArgs();
        Register src = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOpSrc = new C55xMicroRegisterOperand(src);
        Register dst = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOpDst = new C55xMicroRegisterOperand(dst);

        Microinstruction or = new Logical(mRegOpSrc, mRegOpDst, "OR", 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOpDst);

        this.addMicroinstr(or);
        this.addMicroinstr(writeReg);
    }

    public void make_OR_p440_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        MicroOperand constant         = new Constant(args.get(0).getValue(),16);
        Register reg1 = args.get(1).getFirstReg(machine);
        Register reg2 = args.get(2).getFirstReg(machine);
        
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        
        Microinstruction or = new Logical(constant, mRegOp1, "OR", 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp2);
        
        this.addMicroinstr(or);
        this.addMicroinstr(writeReg);
    } 


    public void make_OR_p442_Instr(Machine machine) {
        // OR Smem, src, dst
        // e.g. OR @#0ah,T0,T0 // XXX TODO OR @#0ah,AC0,T0 should not write 40 bits to T0!
        // example from fl2q_t.dis
        // dst = src | Smem

        List<Operand> args = this.getArgs();

        C55xMemoryAccessOperand Smem = (C55xMemoryAccessOperand)args.get(0);
        Register src = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOpSrc = new C55xMicroRegisterOperand(src);
        Register dst = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOpDst = new C55xMicroRegisterOperand(dst);

        Microinstruction smemAccess = new C55xComputeSmem(Smem, 0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0),
                                               machine.getDataResult(0));
        Microinstruction or = new Logical(machine.getDataResult(0), mRegOpSrc, "OR", 1);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(1), mRegOpDst);

        this.addMicroinstr(smemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(or);
        this.addMicroinstr(writeReg);
    }


    public void make_OR_p443_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // OR ACx << #SHIFTW, [ACy]
        List<Operand> args = this.getArgs();
        
        C55xShiftOperand c55xShiftOp = (C55xShiftOperand)args.get(0);
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        Microinstruction shift = new C55xShift(c55xShiftOp, c55xShiftOp.getOp1().getValue(), c55xShiftOp.getOp2().getValue(), 0);
        Microinstruction or = new Logical(machine.getDataResult(0), mRegOp1, "OR", 1);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(1), mRegOp1);
        this.addMicroinstr(shift);
        this.addMicroinstr(or);
        this.addMicroinstr(writeReg);
        
    }

    public void make_OR_p444_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_OR_p445_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    
    public void make_OR_p446_Instr(Machine machine) {  // kps bug fixed
        checkImplicitParallelism(); // to be on safe side
        // or k16, Smem
        List<Operand> args = this.getArgs();
        MicroOperand constant = new Constant(args.get(0).getValue(), 16);
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(1);
        
        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0),
                                               machine.getDataResult(0));
        Microinstruction or = new Logical(constant, machine.getDataResult(0), "OR", 1);
        Microinstruction writeMem = new WriteMem(machine.getDataResult(1),
                                                 machine.getAddressResult(0));
        
        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(or);
        this.addMicroinstr(writeMem);
    } 

    public void make_POP_p448_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        C55xMicroRegisterOperand  sp  = new
            C55xMicroRegisterOperand(machine.getRegister("sp"));
        
        Register reg1 = args.get(0).getFirstReg(machine);
        Register reg2 = args.get(1).getFirstReg(machine);
        
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        MicroOperand     constant   = new Constant(0x2L,16);
        Microinstruction readMem1   = new ReadMem(sp, machine.getDataResult(0));
        Microinstruction writeReg1  = new WriteReg(machine.getDataResult(0), mRegOp1);
        Microinstruction readMem2   = new ReadMem(sp, machine.getDataResult(1));
        Microinstruction writeReg2  = new WriteReg(machine.getDataResult(1), mRegOp2);
        Microinstruction add            = new MicroArithmetic(constant, sp, "ADD",2);
        Microinstruction writeReg   = new WriteReg(machine.getDataResult(2), sp);
        
        this.addMicroinstr(readMem1);
        this.addMicroinstr(writeReg1);
        this.addMicroinstr(readMem2);
        this.addMicroinstr(writeReg2);
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg);
        
    } 
    
    public void make_POP_p449_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // POP dst
        List<Operand> args = this.getArgs();
        C55xMicroRegisterOperand  sp  = new
            C55xMicroRegisterOperand(machine.getRegister("sp"));
        
        Register reg = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp = new C55xMicroRegisterOperand(reg);
        
        MicroOperand     constant  = new Constant(0x1L,16);
        Microinstruction readMem   = new ReadMem(sp, machine.getDataResult(0));
        Microinstruction writeReg1 = new WriteReg(machine.getDataResult(0), mRegOp);
        Microinstruction add       = new MicroArithmetic(constant, sp, "ADD",1);
        Microinstruction writeReg2 = new WriteReg(machine.getDataResult(1), sp);
        this.addMicroinstr(readMem);
        this.addMicroinstr(writeReg1);
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg2);
        
    } 

    public void make_POP_p450_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_POP_p451_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_POP_p452_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        //POP Smem pgm - dunno if this works
        List<Operand> args = this.getArgs();
        MicroOperand     constant  = new Constant(0x1L,16);
        
        C55xMicroRegisterOperand  sp  = new
            C55xMicroRegisterOperand(machine.getRegister("sp"));
        
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);
        
        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem,0);
        Microinstruction readMem   = new ReadMem(sp, machine.getDataResult(0));
        Microinstruction writeMem  = new WriteMem(machine.getDataResult(0), machine.getAddressResult(0));
        Microinstruction add       = new MicroArithmetic(constant, sp, "ADD",1);
        Microinstruction writeReg  = new WriteReg(machine.getDataResult(1), sp);

        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(writeMem);
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg);
        //throw new NullPointerException("not implemented: " + this); 
    } 


    public void make_POP_p453_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    
    public void make_POPBOTH_p454_Instr(Machine machine) { // kps quality code
        checkImplicitParallelism(); // to be on safe side
        // POPBOTH xdst
        // example: POPBOTH XAR5
        // The content of xdst(15–0) is loaded from the location addressed by SP and the
        // content of xdst(31–16) is loaded from the location addressed by SSP.
        // XXX TODO handle ssp
        // XXX TODO CHECK IF THIS WORKS IN iir32_t.dis
        // XXX TODO this does not handle taking only the bits 15-0
        List<Operand> args = this.getArgs();
        C55xMicroRegisterOperand sp = new C55xMicroRegisterOperand(machine.getRegister("sp"));

        Register reg1 = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);

        MicroOperand constant = new Constant(0x1L,16);
        Microinstruction readMem = new ReadMem(sp, mRegOp1);
        Microinstruction add = new MicroArithmetic(constant, sp, "ADD", 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), sp);
        this.addMicroinstr(readMem);
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg);
    } 

    public void make_PSH_p458_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // PSH src1, src2
        List<Operand> args = this.getArgs();        
        C55xMicroRegisterOperand  sp  = new
            C55xMicroRegisterOperand(machine.getRegister("sp"));

        Register reg1 = args.get(0).getFirstReg(machine);
        Register reg2 = args.get(1).getFirstReg(machine);
        
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        
        MicroOperand constant   = new Constant(0x2L,16);
        
        Microinstruction sub       = new MicroArithmetic(constant, sp, "SUB", 0);
        Microinstruction writeReg  = new WriteReg(machine.getDataResult(0), sp);
        Microinstruction writeMem1 = new WriteMem(mRegOp1,sp);
        Microinstruction writeMem2 = new WriteMem(mRegOp2,sp, 0x1L);
     
        this.addMicroinstr(sub);
        this.addMicroinstr(writeReg);
        this.addMicroinstr(writeMem1);
        this.addMicroinstr(writeMem2);
        
    }

    public void make_PSH_p459_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        //PSH src
        List<Operand> args = this.getArgs();
        C55xMicroRegisterOperand  sp  = new
            C55xMicroRegisterOperand(machine.getRegister("sp"));
        
        Register reg1 = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp = new C55xMicroRegisterOperand(reg1);
        
        
        MicroOperand constant  = new Constant(0x1L,16);

        Microinstruction sub      = new MicroArithmetic(constant, sp, "SUB",0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), sp);
        Microinstruction writeMem = new WriteMem(mRegOp,sp);

        
        this.addMicroinstr(sub);
        this.addMicroinstr(writeReg);
        this.addMicroinstr(writeMem);
        
    } 


    public void make_PSH_p460_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_PSH_p461_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    // implemented by kps, might be buggy -yeah fixed by pgm
    public void make_PSH_p462_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        //PSH Smem
        List<Operand> args = this.getArgs();
        C55xMicroRegisterOperand sp
            = new C55xMicroRegisterOperand(machine.getRegister("sp"));

        MicroOperand constant  = new Constant(0x1L,16);

        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);

        Microinstruction sub      = new MicroArithmetic(constant, sp, "SUB", 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), sp);

        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0),
                                               machine.getDataResult(1));

        Microinstruction writeMem = new WriteMem(machine.getDataResult(1),
                                                 sp);

        this.addMicroinstr(sub);         // subtract from sp ?
        this.addMicroinstr(writeReg);   // write new value of sp ?

        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(writeMem);  // write the value to memory
    }

    public void make_PSH_p463_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }


    public void make_PSHBOTH_p464_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        C55xMicroRegisterOperand  sp  = new
            C55xMicroRegisterOperand(machine.getRegister("sp"));
        
        Register reg1 = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp = new C55xMicroRegisterOperand(reg1);
        
        
        MicroOperand constant  = new Constant(0x1L,16);

        Microinstruction sub      = new MicroArithmetic(constant, sp, "SUB",0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), sp);
        Microinstruction writeMem = new WriteMem(mRegOp,sp);

        
        this.addMicroinstr(sub);
        this.addMicroinstr(writeReg);
        this.addMicroinstr(writeMem);
        


        
        //        throw new NullPointerException("not implemented: " + this); 


    } 


    public void make_RESET_p465_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_RET_p469_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        Microinstruction ret = new C55xRet();
        this.addMicroinstr(ret);
    }


    public void make_RETCC_p471_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_RETI_p473_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_ROL_p475_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_ROR_p477_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_ROUND_p479_Instr(Machine machine) {
        // ROUND [ACx,] ACy
        // ACy = rnd(ACx)
        // XXX TODO - kps hack, this as a MOV for now
        List<Operand> args = this.getArgs();
        Register ACx = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mACx = new C55xMicroRegisterOperand(ACx);
        Register ACy = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mACy = new C55xMicroRegisterOperand(ACy);

        Microinstruction writeReg = new WriteReg(mACx, mACy);
        this.addMicroinstr(writeReg); 
    }

    public void make_RPT_p482_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        MicroOperand constant = new Constant(args.get(0).getValue(),16);  
        Microinstruction repeat = new Repeat(constant.getValue());
        this.addMicroinstr(repeat);
    } 


public void make_RPT_p484_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
    //RPT CSR
            List<Operand> args = this.getArgs();
        Register reg1 = args.get(0).getFirstReg(machine);  
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        Microinstruction repeat = new Repeat(mRegOp1);
        this.addMicroinstr(repeat);
} 

    public void make_RPTADD_p487_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_RPTADD_p488_Instr(Machine machine) { // copied from rptsub and modified
        checkImplicitParallelism(); // to be on safe side
        //RPTADD CSR, k4
        List<Operand> args = this.getArgs();
        Register reg1 = args.get(0).getFirstReg(machine);  
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);

        MicroOperand constant  = new Constant(args.get(1).getValue(), 4);

        Microinstruction repeat = new Repeat(mRegOp1);
        Microinstruction add = new MicroArithmetic(constant, mRegOp1, "ADD", 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp1);

        this.addMicroinstr(repeat);
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg);


    }

    public void make_RPTBLOCAL_p490_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // RPTBLOCAL pmad
        List<Operand> args = this.getArgs();
        C55xProgramAddressOperand addressOp = (C55xProgramAddressOperand)args.get(0);
        long address = addressOp.getOffset();
        Microinstruction repeat = new Repeat(addressOp);
        this.addMicroinstr(repeat);
    }

    public void make_RPTB_p497_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        C55xProgramAddressOperand addressOp = (C55xProgramAddressOperand)args.get(0);
        long address = addressOp.getOffset();
        Microinstruction repeat = new Repeat(addressOp);
        this.addMicroinstr(repeat);
    }

    public void make_RPTCC_p500_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
public void make_RPTSUB_p503_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
    //RPTSUB CSR, k4
    List<Operand> args = this.getArgs();
    Register reg1 = args.get(0).getFirstReg(machine);  
    C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);


    MicroOperand constant  = new Constant(args.get(1).getValue(),4);

    Microinstruction repeat = new Repeat(mRegOp1);
    Microinstruction sub = new MicroArithmetic(constant, mRegOp1, "SUB", 0);
    Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp1);

    this.addMicroinstr(repeat);
    this.addMicroinstr(sub);
    this.addMicroinstr(writeReg);        

}


    public void make_SAT_p505_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_SFTCC_p507_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_SFTL_p510_Instr(Machine machine) {
        // SFTL ACx, Tx[, ACy]
        // e.g. SFTL AC2,T1,AC2
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        Register reg0 = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp0 = new C55xMicroRegisterOperand(reg0);
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

        // XXX: TODO support CARRY, etc.
        Microinstruction shift = new Shift(mRegOp0, mRegOp1, "LS", 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp2);

        this.addMicroinstr(shift);
        this.addMicroinstr(writeReg);
    }

public void make_SFTL_p511_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // SFTL ACx, #SHIFTW[, ACy]
        List<Operand> args = this.getArgs();
        Register reg1 = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        MicroOperand constant  = new Constant(args.get(1).getValue(),6);
        Microinstruction shift = new Shift(mRegOp1, constant, "LS",0);
        
        this.addMicroinstr(shift);
        
        if (args.size() == 2){
            Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp1);
            this.addMicroinstr(writeReg);
        }
        
        if (args.size() == 3){
            Register reg2 = args.get(2).getFirstReg(machine);
            C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
            Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp2);   
            this.addMicroinstr(writeReg);
        }
} 

    // implemented by kps
    public void make_SFTL_p513_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // SFTL dst, #1
        List<Operand> args = this.getArgs();
        Register reg1 = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);

        MicroOperand constant  = new Constant(1, 1); // value, bitsize
        Microinstruction shift = new Shift(mRegOp1, constant, "LS",0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp1);

        this.addMicroinstr(shift);
        this.addMicroinstr(writeReg);
    }

    public void make_SFTS_p516_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // SFTS ACx, Tx[, ACy]
        // e.g. SFTS AC0,T0,AC1
        List<Operand> args = this.getArgs();
        Register reg0 = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp0 = new C55xMicroRegisterOperand(reg0);
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

        // XXX: TODO support CARRY, etc.
        Microinstruction shift = new Shift(mRegOp0, mRegOp1, "AS", 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp2);

        this.addMicroinstr(shift);
        this.addMicroinstr(writeReg);
    }

    public void make_SFTSC_p518_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_SFTS_p520_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // SFTS ACx, #SHIFTW, [ACy]
        List<Operand> args = this.getArgs();

        if (args.size() == 2) { // SFTS ACx, #SHIFTW, kps quality code

            // XXX TODO CHECK IF THIS WORKS IN cifft32_t.dis
            Register reg1 = args.get(0).getFirstReg(machine);
            MicroOperand constant  = new Constant(args.get(1).getValue(),6);
            C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);

            Microinstruction shift = new Shift(mRegOp1, constant, "AS",0);
            Microinstruction writeReg = new WriteReg(machine.getDataResult(0),mRegOp1);

            this.addMicroinstr(shift);
            this.addMicroinstr(writeReg);
        }
        else { // SFTS ACx, #SHIFTW, ACy
            Register reg1 = args.get(0).getFirstReg(machine);
            MicroOperand constant  = new Constant(args.get(1).getValue(),6);
            Register reg2 = args.get(2).getFirstReg(machine);
            C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
            C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

            Microinstruction shift = new Shift(mRegOp1, constant, "AS",0);
            Microinstruction writeReg = new WriteReg(machine.getDataResult(0),mRegOp2);
        
            this.addMicroinstr(shift);
            this.addMicroinstr(writeReg);
        }


    }





    public void make_SFTSC_p522_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // SFTSC ACx, #SHIFTW[, ACy]
        List<Operand> args = this.getArgs();

        Register reg1 = args.get(0).getFirstReg(machine);
        MicroOperand constant  = new Constant(args.get(1).getValue(), 6);
        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

        Microinstruction shift = new Shift(mRegOp1, constant, "AS", 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp2);
        
        this.addMicroinstr(shift);
        this.addMicroinstr(writeReg);
        //throw new NullPointerException("XXX TODO CARRY: " + this);
    } 

    public void make_SFTS_p525_Instr(Machine machine){ 
        checkImplicitParallelism(); // to be on safe side
        
        // SFTL dst, #-1
        List<Operand> args = this.getArgs();
        Register reg1 = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        MicroOperand constant  = new Constant(-1, 1); // value, bitsize
        Microinstruction shift = new Shift(mRegOp1, constant, "AS",0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp1);
        
        this.addMicroinstr(shift);
        this.addMicroinstr(writeReg);
    } 
    
    
    
    public void make_SQA_p530_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    // kps
    public void make_SQAM_p531_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // SQAM[R] [T3 = ]Smem, [ACx,] ACy
        // ACy = ACx + (Smem * Smem)
        List<Operand> args = this.getArgs();
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);

        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);

        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0),
                                               machine.getDataResult(0));
        Microinstruction multiply = new MicroArithmetic(machine.getDataResult(0),
                                                        machine.getDataResult(0), "MUL", 1);

        Microinstruction add          = new MicroArithmetic(machine.getDataResult(1), mRegOp1,
                                                        "ADD", 2);
        Microinstruction writeReg     = new WriteReg(machine.getDataResult(2), mRegOp2);

        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(multiply);
        this.addMicroinstr(add);
        this.addMicroinstr(writeReg);
    }



    public void make_SQDST_p532_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_SQR_p535_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    // kps: toimii ju!
    public void make_SQRM_p536_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // SQRM[R] [T3 = ]Smem, ACx
        List<Operand> args = this.getArgs();
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);

        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);

        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0),
                                               machine.getDataResult(0));

        Microinstruction multiply = new MicroArithmetic(machine.getDataResult(0),
                                                        machine.getDataResult(0), "MUL", 1);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(1), mRegOp1);

        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(multiply);
        this.addMicroinstr(writeReg);
    } 

    public void make_SQS_p538_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_SQSM_p539_Instr(Machine machine) { // kps quality code
        // SQSM[R] [T3 = ]Smem, [ACx,] ACy
        // e.g. SQSM T3 = *AR1,AC0,AC0
        // ACy = ACx – (Smem * Smem)

        // WORKS?: used in dsplib sqrtv

        List<Operand> args = this.getArgs();
        C55xMemoryAccessOperand Smem = (C55xMemoryAccessOperand)args.get(0);

        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);

        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

        Microinstruction computeSmem = new C55xComputeSmem(Smem, 0);
        ReadMem readSmem = new ReadMem(machine.getAddressResult(0),
                                       machine.getDataResult(0));
        readSmem.setSignExtend(ReadMem.sxSigned); // always sign extend

        Register reg3 = machine.getRegister("t3");
        C55xMicroRegisterOperand mRegOp3 = new C55xMicroRegisterOperand(reg3);
        Microinstruction writeT3 = new WriteReg(machine.getDataResult(0), mRegOp3);

        Microinstruction multiply = new MicroArithmetic(machine.getDataResult(0),
                                                        machine.getDataResult(0), "MUL", 1);

        Microinstruction sub = new MicroArithmetic(machine.getDataResult(1), mRegOp1,
                                                   "SUB", 2);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(2), mRegOp2);

        this.addMicroinstr(computeSmem);
        this.addMicroinstr(readSmem);
        if (Smem.hasT3EQ())
            this.addMicroinstr(writeT3);
        this.addMicroinstr(multiply);
        this.addMicroinstr(sub);
        this.addMicroinstr(writeReg);
    }

    public void make_SUB_p541_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side        
        //SUB dual(Lmem), [ACx,] ACy - not completed

        List<Operand> args = this.getArgs();
        
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);
        
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        Register reg2 = args.get(2).getFirstReg(machine);

        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        
        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem,0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0), machine.getDataResult(0));
        Microinstruction dual = new C55xDual(machine.getAddressResult(0), mRegOp1, mRegOp2);
        
        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(dual);
    } 


    public void make_SUB_p543_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_SUB_p545_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_SUB_p547_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    // implemented by kps (i.e. probably broken)
    public void make_SUB_p551_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        // SUB [src,] dst
        List<Operand> args = this.getArgs();
        
        if (args.size() == 1) {
            Register dst = args.get(0).getFirstReg(machine);
            C55xMicroRegisterOperand dstOp = new C55xMicroRegisterOperand(dst);

            Microinstruction sub = new MicroArithmetic(dstOp, dstOp, "SUB", 0);
            Microinstruction writeReg = new WriteReg(machine.getDataResult(0), dstOp);
            this.addMicroinstr(sub);
            this.addMicroinstr(writeReg);
        }

        if (args.size() == 2) {
                 Register src = args.get(0).getFirstReg(machine);
            C55xMicroRegisterOperand srcOp = new C55xMicroRegisterOperand(src);
            Register dst = args.get(1).getFirstReg(machine);
            C55xMicroRegisterOperand dstOp = new C55xMicroRegisterOperand(dst);
        
            Microinstruction sub = new MicroArithmetic(srcOp, dstOp, "SUB", 0);
            Microinstruction writeReg = new WriteReg(machine.getDataResult(0), dstOp);
        
            this.addMicroinstr(sub);
            this.addMicroinstr(writeReg);
        }
    }

public void make_SUB_p552_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // SUB K16, [src,] dst
        
        List<Operand> args = this.getArgs();
        MicroOperand constant = new Constant(args.get(0).getValue(),16);
        
        if (args.size() == 2){
            Register reg1 = args.get(1).getFirstReg(machine);
            C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
            Microinstruction sub = new MicroArithmetic(constant, mRegOp1, "SUB", 0);
            Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp1);
            this.addMicroinstr(sub);
            this.addMicroinstr(writeReg);
        }
        
        if (args.size() == 3){
     
            Register reg1 = args.get(1).getFirstReg(machine);
            C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
            Register reg2 = args.get(2).getFirstReg(machine);
            C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        
            Microinstruction sub      = new MicroArithmetic(constant, mRegOp1, "SUB", 0);
            Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp2);
        
            this.addMicroinstr(sub);
            this.addMicroinstr(writeReg);
        }

} 


    public void make_SUB_p556_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // SUB Smem, [src], dst
        List<Operand> args = this.getArgs();
        
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);
        
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        
        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        
        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0), machine.getDataResult(0));
               Microinstruction sub      = new MicroArithmetic(machine.getDataResult(0), mRegOp1, "SUB", 1);
               Microinstruction writeReg = new WriteReg(machine.getDataResult(1), mRegOp2);
        
        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(sub);
        this.addMicroinstr(writeReg);
    } 


    public void make_SUB_p558_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_SUB_p560_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_SUB_p561_Instr(Machine machine) { // kps quality code
        checkImplicitParallelism(); // to be on safe side
        // SUB ACx << #SHIFTW, ACy
        // ACy = ACy – (ACx << #SHIFTW)
        List<Operand> args = this.getArgs();
        C55xShiftOperand c55xShiftOp = (C55xShiftOperand)args.get(0);
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);

        Microinstruction shift = new C55xShift(c55xShiftOp,
                                               c55xShiftOp.getOp1().getValue(),
                                               c55xShiftOp.getOp2().getValue(), 0);
        Microinstruction sub = new MicroArithmetic(machine.getDataResult(0), mRegOp1, "SUB", 1);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(1), mRegOp1);

        this.addMicroinstr(shift);
        this.addMicroinstr(sub);
        this.addMicroinstr(writeReg);
    }

    public void make_SUB_p562_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_SUB_p563_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_SUB_p564_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_SUB_p565_Instr(Machine machine) {
        //      SUB Smem  << #16, [ACx,], ACy
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_SUB_p566_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_SUB_p567_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_SUB_p569_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // SUB uns[(Smem)], ACx, ACy -implement uns properly
        List<Operand> args = this.getArgs();
        
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);
        
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);

        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

        
        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
               Microinstruction sub      = new MicroArithmetic(machine.getAddressResult(0), mRegOp1, "SUB", 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp2);
        
        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(sub);
        this.addMicroinstr(writeReg);
    }


    public void make_SUB_p570_Instr(Machine machine) { // kps quality code
        // SUB [uns(]Smem[)] << #SHIFTW, [ACx,] ACy
        // e.g. SUB *AR2+ << #1,AC0,AC0
        // ACy = ACx – (Smem << #SHIFTW)
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        C55xShiftOperand c55xShift = (C55xShiftOperand)args.get(0);
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)c55xShift.getOp1();
        long c55xImmValue = c55xShift.getOp2().getValue();
        Constant shiftVal = new Constant(c55xImmValue, 6);

        Register ACx = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOpACx = new C55xMicroRegisterOperand(ACx);
        Register ACy = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOpACy = new C55xMicroRegisterOperand(ACy);

        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0),
                                               machine.getDataResult(0));

        Microinstruction shift = new Shift(machine.getDataResult(0), shiftVal, "AS", 1);
        Microinstruction sub = new MicroArithmetic(machine.getDataResult(1),
                                                   mRegOpACx, "SUB", 2);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(2), mRegOpACy);
        
        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(shift);
        this.addMicroinstr(sub);
        this.addMicroinstr(writeReg); 
    }
    
    public void make_SUB_p572_Instr(Machine machine) { // kps quality code
        checkImplicitParallelism(); // to be on safe side
        // SUB dbl(Lmem), [ACx,] ACy
        // example: SUB dbl(*AR7+),AC0,AC0
        // ACy = ACx – dbl(Lmem)

        // XXX TODO CHECK IF THIS WORKS IN cifft32_t.dis

        List<Operand> args = this.getArgs();

        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction readMemDouble = new ReadMemDouble(machine.getAddressResult(0), 
                                                           machine.getDataResult(0));
        Microinstruction sub = new MicroArithmetic(machine.getDataResult(0), mRegOp1, "SUB", 1);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(1), mRegOp2);

        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMemDouble);
        this.addMicroinstr(sub);
        this.addMicroinstr(writeReg);
    } 

public void make_SUB_p573_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
         // SUB ACx, dbl(Lmem), ACy
        List<Operand> args = this.getArgs();
        Register reg1 = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);

        C55xMemoryAccessOperand lMem = (C55xMemoryAccessOperand)args.get(1);

        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

        Microinstruction lMemAccess = new C55xComputeSmem(lMem, 0);
        Microinstruction readMemDouble = new ReadMemDouble(machine.getAddressResult(0), 
                                                           machine.getDataResult(0));
        Microinstruction sub       = new MicroArithmetic(mRegOp2, machine.getDataResult(0), "SUB",1);
        Microinstruction writeReg  = new WriteReg(machine.getDataResult(1), mRegOp2);

        this.addMicroinstr(lMemAccess);
        this.addMicroinstr(readMemDouble);
        this.addMicroinstr(sub);
        this.addMicroinstr(writeReg);
}


    public void make_SUB_p574_Instr(Machine machine) { // kps quality code
        checkImplicitParallelism(); // to be on safe side
        // SUB Xmem, Ymem, ACx
        // e.g. SUB *AR0+,*AR1+,AC0
        // ACx = (Xmem << #16) – (Ymem << #16)

        // WORKS: used in dsplib sub

        List<Operand> args = this.getArgs();
        C55xMemoryAccessOperand Xmem = (C55xMemoryAccessOperand)args.get(0);
        C55xMemoryAccessOperand Ymem = (C55xMemoryAccessOperand)args.get(1);
        Register reg = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp = new C55xMicroRegisterOperand(reg);

        Microinstruction xmemAccess = new C55xComputeSmem(Xmem, 0);
        ReadMem readXmem = new ReadMem(machine.getAddressResult(0), 
                                       machine.getDataResult(0));
        if (!Xmem.hasUnsMod()) readXmem.setSignExtend(ReadMem.sxUseSXMD);
        Microinstruction xshift = new Shift(machine.getDataResult(0),
                                            new Constant(16, 16), "AS", 1);
        Microinstruction ymemAccess = new C55xComputeSmem(Ymem, 1);
        ReadMem readYmem = new ReadMem(machine.getAddressResult(1), 
                                       machine.getDataResult(2));
        if (!Ymem.hasUnsMod()) readYmem.setSignExtend(ReadMem.sxUseSXMD);
        Microinstruction yshift = new Shift(machine.getDataResult(2),
                                            new Constant(16, 16), "AS", 3);

        Microinstruction sub = new MicroArithmetic(machine.getDataResult(3),
                                                   machine.getDataResult(1),
                                                   "SUB", 4);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(4), mRegOp);

        this.addMicroinstr(xmemAccess);
        this.addMicroinstr(readXmem);
        this.addMicroinstr(xshift);
        this.addMicroinstr(ymemAccess);
        this.addMicroinstr(readYmem);
        this.addMicroinstr(yshift);
        this.addMicroinstr(sub);
        this.addMicroinstr(writeReg);
    }

    public void make_SUB_p575_Instr(Machine machine) {
        // SUB::MOV
        checkImplicitParallelism();
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_SUB_xxx_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    
    public void make_SUBADD_p578_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // SUBADD Tx, Smem, ACx DOES NOT WORK -pgm
        List<Operand> args = this.getArgs();
        Register reg1 = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(1);
        
        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2); 

        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        this.addMicroinstr(c55xSmemAccess);

    } 
    

    public void make_SUBADD_p580_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_SUBC_p582_Instr(Machine machine) { // kps quality code
        // SUBC @#00h,AC0,AC0
        // SUBC Smem, [ACx,] ACy
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        // XXX TODO: CARRY??? ETC
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);
        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0),
                                               machine.getDataResult(0));
        Microinstruction shift = new Shift(machine.getDataResult(0),
                                           new Constant(15, 16), "AS", 1);
        Microinstruction sub = new MicroArithmetic(machine.getDataResult(1),
                                                   mRegOp1, "SUB", 2);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(2), mRegOp2);

        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(shift);
        this.addMicroinstr(sub);
        this.addMicroinstr(writeReg);
    }

    public void make_SWAP_p585_Instr(Machine machine) { // kps quality code
        checkImplicitParallelism(); // to be on safe side
        // e.g. SWAP T0,T2
        // The content of T0 is moved to T2 and the content of T2 is moved to T0
        // XXX TODO test in cifft32_t.dis
        List<Operand> args = this.getArgs();
        Register reg1 = args.get(0).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        Register reg2 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

        MicroOperand zero = new Constant(0, 64);
        // move reg1 to tmp
        Microinstruction move = new MicroArithmetic(mRegOp1, zero, "ADD", 0);
        // write reg2 to reg1
        Microinstruction writeReg1 = new WriteReg(mRegOp2, mRegOp1);
        // write tmp to reg2
        Microinstruction writeReg2 = new WriteReg(machine.getDataResult(0), mRegOp2);

        this.addMicroinstr(move);
        this.addMicroinstr(writeReg1);
        this.addMicroinstr(writeReg2);
    }

    public void make_SWAPP_p590_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_SWAP4_p595_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_TRAP_p597_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_XCC_p600_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // XCC [label], cond
        List<Operand> args = this.getArgs();
        C55xConditionFieldOperand condOp = (C55xConditionFieldOperand)args.get(0);
        Microinstruction cond = new C55xCondition(condOp);
        Microinstruction partial = new C55xPartialExec();
        this.addMicroinstr(cond);
        this.addMicroinstr(partial); 
    }
    
    
    public void make_XCCPART_p603_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        List<Operand> args = this.getArgs();
        C55xConditionFieldOperand condOp = (C55xConditionFieldOperand)args.get(0);
        Microinstruction cond = new C55xCondition(condOp);
        Microinstruction partial = new C55xPartialExec();
        this.addMicroinstr(cond);
        this.addMicroinstr(partial);
        
        
    }

    
    public void make_XOR_p607_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side
        // XOR src,dst
            List<Operand> args = this.getArgs();
        Register reg1 = args.get(0).getFirstReg(machine);
        Register reg2 = args.get(1).getFirstReg(machine);
        
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);

        Microinstruction xor = new Logical(mRegOp1, mRegOp2, "XOR", 0);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(0), mRegOp2);
        
        this.addMicroinstr(xor);
        this.addMicroinstr(writeReg); 
    }
    
    public void make_XOR_p608_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }
    public void make_XOR_p610_Instr(Machine machine) {
        // XOR Smem, src, dst -pgm
        checkImplicitParallelism(); // to be on safe side
        
        List<Operand> args = this.getArgs();
                
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(0);
        Register reg1 = args.get(1).getFirstReg(machine);
        Register reg2 = args.get(2).getFirstReg(machine);

        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0),
                                               machine.getDataResult(0));
        Microinstruction xor = new Logical(machine.getDataResult(0), mRegOp2, "XOR", 1);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(1), mRegOp2);
        
        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(xor);
        this.addMicroinstr(writeReg);
    }
    
    public void make_XOR_p611_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_XOR_p612_Instr(Machine machine) { // kps quality code
        // XOR #8191 << 16,AC0,AC0
        // XOR k16 << #16, [ACx,] ACy
        // This instruction performs a bitwise exclusive-OR (XOR) operation between an
        // accumulator (ACx) content and a 16-bit unsigned constant, k16, shifted left by
        // 16 bits:
        // ACy = ACx ^ (k16 <<< #16)
        List<Operand> args = this.getArgs();
        C55xShiftOperand c55xShiftOp = (C55xShiftOperand)args.get(0);

        Register reg1 = args.get(1).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp1 = new C55xMicroRegisterOperand(reg1);
        Register reg2 = args.get(2).getFirstReg(machine);
        C55xMicroRegisterOperand mRegOp2 = new C55xMicroRegisterOperand(reg2);
        
        Microinstruction shift = new C55xShift(c55xShiftOp, 0, 0, 0);
        Microinstruction xor = new Logical(machine.getDataResult(0), mRegOp1, "XOR", 1);
        Microinstruction writeReg = new WriteReg(machine.getDataResult(1), mRegOp2);

        this.addMicroinstr(shift);
        this.addMicroinstr(xor);
        this.addMicroinstr(writeReg);
    }

    public void make_XOR_p613_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        throw new NullPointerException("not implemented: " + this);
    }

    public void make_XOR_p614_Instr(Machine machine) { 
        checkImplicitParallelism(); // to be on safe side        
        // XOR k16, Smem
        List<Operand> args = this.getArgs();
        MicroOperand constant = new Constant(args.get(0).getValue(),16);
        C55xMemoryAccessOperand c55xSmem = (C55xMemoryAccessOperand)args.get(1);
        Microinstruction c55xSmemAccess = new C55xComputeSmem(c55xSmem, 0);
        Microinstruction readMem = new ReadMem(machine.getAddressResult(0),
                                               machine.getDataResult(0));
        Microinstruction xor = new Logical(constant, machine.getDataResult(0), "XOR", 1);
        Microinstruction writeMem = new WriteMem(machine.getDataResult(1),
                                                 machine.getAddressResult(0));

        this.addMicroinstr(c55xSmemAccess);
        this.addMicroinstr(readMem);
        this.addMicroinstr(xor);
        this.addMicroinstr(writeMem);
    } 


    public void make_UNKNOWN_Instr(Machine machine) {
        checkImplicitParallelism(); // to be on safe side
        machine.setSimulationRunning(false);
        //Do nothing here - pgm
        //throw new NullPointerException("not implemented: " + this);
    }


    
}
