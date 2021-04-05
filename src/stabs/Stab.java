/**
 * Stab.java
 */

package stabs;

import java.util.ArrayList;

import misc.Expression;
import main.*;
import pseudoOp.*;


/**
 * A piece of debugging information in the stabs format.
 * 
 * @author Mikko Reinikainen
 * @author Kristian Söderblom
 */
public class Stab extends PseudoOp {

    /** stab types */
    /* http://theory.uwinnipeg.ca/gnu/gdb/stabs_69.html#SEC69 */
    public static final long N_GSYM        = 32;  // 0x20
    public static final long N_FNAME        = 34;  // 0x22
    public static final long N_FUN        = 36;  // 0x24
    public static final long N_STSYM        = 38;  // 0x26
    public static final long N_LCSYM        = 40;  // 0x28
    public static final long N_MAIN        = 42;  // 0x2a
    public static final long N_ROSYM        = 44;  // 0x2c
    public static final long N_PC        = 48;  // 0x30
    public static final long N_NSYMS        = 50;  // 0x32
    public static final long N_NOMAP        = 52;  // 0x34
    public static final long N_OBJ        = 56;  // 0x38
    public static final long N_OPT        = 60;  // 0x3c
    public static final long N_RSYM        = 64;  // 0x40
    public static final long N_M2C        = 66;  // 0x42
    public static final long N_SLINE        = 68;  // 0x44
    public static final long N_DSLINE        = 70;  // 0x46
    public static final long N_BSLINE        = 72;  // 0x48
    public static final long N_BROWS        = 72;  // 0x48
    public static final long N_DEFD        = 74;  // 0x4a
    public static final long N_FLINE        = 76;  // 0x4c
    public static final long N_EHDECL        = 80;  // 0x50
    public static final long N_MOD2        = 80;  // 0x50
    public static final long N_CATCH        = 84;  // 0x54
    public static final long N_SSYM        = 96;  // 0x60
    public static final long N_ENDM        = 98;  // 0x62
    public static final long N_SO        = 100; // 0x64
    public static final long N_LSYM        = 128; // 0x80
    public static final long N_BINCL        = 130; // 0x82
    public static final long N_SOL        = 132; // 0x84
    public static final long N_PSYM        = 160; // 0xa0
    public static final long N_EINCL        = 162; // 0xa2
    public static final long N_ENTRY        = 164; // 0xa4
    public static final long N_LBRAC        = 192; // 0xc0
    public static final long N_EXCL        = 194; // 0xc2
    public static final long N_SCOPE        = 196; // 0xc4
    public static final long N_RBRAC        = 224; // 0xe0
    public static final long N_BCOMM        = 226; // 0xe2
    public static final long N_ECOMM        = 228; // 0xe4
    public static final long N_ECOML        = 232; // 0xe8
    public static final long N_WITH        = 234; // 0xea
    public static final long N_NBTEXT        = 240; // 0xf0
    public static final long N_NBDATA        = 242; // 0xf2
    public static final long N_NBBSS        = 244; // 0xf4
    public static final long N_NBSTS        = 246; // 0xf6
    public static final long N_NBLCS        = 248; // 0xf8


    /** Attributes */

    // string field: http://theory.uwinnipeg.ca/gnu/gdb/stabs_4.html#SEC4
    private String string;
    private String name;
    // symbol descr: http://theory.uwinnipeg.ca/gnu/gdb/stabs_70.html#SEC70
    private char symDesc;
    private String typeInfo;
    private String typeRef;
    private ArrayList<StabTypeInfo> typeDef;
    private long type;
    private long other;
    private long desc;
    private Expression value;

    /**
     * Constructs a new stabs pseudo op
     *
     * @param string the stabs string (null for stabn)
     * @param type the type of the stab
     * @param other the other field, almost always unused
     * @param desc the desc field
     * @param value the value of the stab
     * @return the new stabs pseudo op
     */
    public Stab(String string,
                long type, long other, long desc,
                Expression value) {
        this.string = string;
        this.type = type;
        this.other = other;
        this.desc = desc;
        this.value = value;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public char getSymDesc() {
        return symDesc;
    }

    public void setSymDesc(char symDesc) {
        this.symDesc = symDesc;
    }


    public String getTypeInfo() {
        return typeInfo;
    }

    public void setTypeInfo(String typeInfo) {
        this.typeInfo = typeInfo;
    }


    public String getTypeRef() {
        return typeRef;
    }

    public void setTypeRef(String typeRef) {
        this.typeRef = typeRef;
    }


    public ArrayList<StabTypeInfo> getTypeDef() {
        return typeDef;
    }

    public void setTypeDef(ArrayList<StabTypeInfo> typeDef) {
        this.typeDef = typeDef;
    }


    public String toString() {
        if (string != null) {
            return ".stabs\t"
                + "\"" + string + "\","
                + type + ","
                + other + ","
                + desc + ","
                + value;
        }
        else {
            if (value != null) {
                return ".stabn "
                    + type + ","
                    + other + ","
                    + desc + ","
                    + value;
            }
            else {
                return ".stabd "
                    + type + ","
                    + other + ","
                    + desc;
            }
        }
    }

    public static void debugPrintln(String s) {
        if (Main.getOptions().getDebugStabs())
            System.out.println(s);
    }

}
