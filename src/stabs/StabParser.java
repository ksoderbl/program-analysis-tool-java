
package stabs;

import java.util.StringTokenizer;
import java.util.ArrayList;
import main.*;
import misc.Expression;
import stabs.*;

import pseudoOp.*;

class Attributes {
    private int size;
    private String info;


    void setSize(int size) {
        this.size = size;
    }

    int getSize() {
        return size;
    }

    /* the remaining string after attributes have been removed */
    void setInfo(String info) {
        this.info = info;
    }

    String getInfo() {
        return info;
    }

    public String toString() {
        return "size:" + size;
    }
}


/**
 * A parser for stab (symbol table) directives.
 *
 * @author Kristian Söderblom
 */

public class StabParser {

    /**
     *
     */
    private static Stab parseStabd(long type,
                                   long other,
                                   long desc) {
        boolean ds = Main.getOptions().getDebugStabs();

        if (type == 0) {
            ;
        }
        else {
            Main.warn("Unknown stabd of type " + type);
            if (ds)
                Main.fatal("ARGH");
        }

        return new Stab(null, type, other, desc, null);
    }

    /**
     *
     */
    private static Stab parseStabn(long type,
                                   long other,
                                   long desc,
                                   Expression value) {
        boolean ds = Main.getOptions().getDebugStabs();

        if (type == 0) {
            ;
        }
        else if (type == Stab.N_SLINE) { // 68
            // http://theory.uwinnipeg.ca/gnu/gdb/stabs_11.html#SEC11
            return new LineNumberStab(null, type, other, desc, value);
        }
        else if (type == Stab.N_EINCL) { // 162
            // http://theory.uwinnipeg.ca/gnu/gdb/stabs_10.html#SEC10
            return new EndIncludeFileStab(null, type, other, desc, value);
        }
        else if (type == Stab.N_LBRAC) { // 192
            // http://theory.uwinnipeg.ca/gnu/gdb/stabs_14.html#SEC14
            return new LeftBraceStab(null, type, other, desc, value);
        }
        else if (type == Stab.N_RBRAC) { // 224
            // http://theory.uwinnipeg.ca/gnu/gdb/stabs_14.html#SEC14
            return new RightBraceStab(null, type, other, desc, value);
        }

        else {
            Main.warn("Unknown stabn of type " + type);
            if (ds)
                Main.fatal("ARGH");
        }

        return new Stab(null, type, other, desc, value);
    }

    /* stuff after type descriptor @ */
    private static Attributes parseTypeAttribute(String info,
                                                 Attributes attr) {
        // http://theory.uwinnipeg.ca/gnu/gdb/stabs_4.html#SEC4
        if (attr == null)
            attr = new Attributes();
        char first = info.charAt(0);
        int firstColon = info.indexOf(';');
        if (firstColon == -1)
            Main.fatal("Type attribute not terminated.");
        String remain = info.substring(firstColon + 1, info.length());
        //Main.warn("Info : " + info);
        //Main.warn("Rema : " + remain);


        if (first == 's') {
            String s = info.substring(1, firstColon);
            //Main.warn("s = " + s);
            // size
            int size = Integer.parseInt(s);
            //Main.warn("size = " + size);
            attr.setSize(size / 8);
        } else
            Main.fatal("Unknown type attribute '" + first + "'");

        attr.setInfo(remain);

        return attr;
    }

    /* stuff after type descriptor r */
    /* size is sizeof this type, if > 0 */
    private static StabTypeInfo parseSubrangeType(String info,
                                                 Attributes attr) {

        // http://theory.uwinnipeg.ca/gnu/gdb/stabs_38.html#SEC38
        // http://theory.uwinnipeg.ca/gnu/gdb/stabs_32.html#SEC32
        //debugPrintln("                  [range type]");
        StringTokenizer st = new StringTokenizer(info, ";");
        String type = null, lower = null, upper = null;
        int colons = 0;
        String remain = null;

        //Main.warn("info = " + info);

        for (int i = 0; i < info.length(); i++) {
            if (info.charAt(i) == ';')
                colons++;
            if (colons == 3)
                remain = info.substring(i + 1, info.length());
        }

        if (colons < 3)
            Main.fatal("< 3 colons in subrange type");

        //Main.warn("remain = '" + remain + "'");
        //Main.warn("remain len = " + remain.length());
        type = st.nextToken();
        lower = st.nextToken();
        upper = st.nextToken();

        long lo, hi, size = 0;

        // kps - these may be buggy for 64 bit types !!!
        try {
            lo = Long.decode(lower).longValue();
        }
        catch (NumberFormatException e) {
            lo = Long.MIN_VALUE;
        }
        try {
            hi = Long.decode(upper).longValue();
        }
        catch (NumberFormatException e) {
            hi = Long.MAX_VALUE;
        }

        //Main.warn("lower = " + lower);
        //Main.warn("lo    = " + lo);
        //Main.warn("upper = " + upper);
        //Main.warn("hi    = " + hi);

        /*
         * http://theory.uwinnipeg.ca/gnu/gdb/stabs_33.html#SEC33
         * If the upper bound of a subrange is 0 and the lower bound is
         * positive, the type is a floating point type, and the lower bound
         * of the subrange indicates the number of bytes in the type.
         */
        if (lo > 0 && hi == 0)
            return new StabFloatingPointType(type, lo);

        if (attr != null)
            size = attr.getSize();
        
        // guess byte size
        if (size <= 0) {
            if (hi < 256L)
                size = 1;
            else if (hi < 65536L)
                size = 2;
            else if (hi < 4294967296L)
                size = 4;
            else
                size = 8;
        }
        //Main.warn("size for " + info + " is " + size);

        return new StabSubrangeType(type, size, lo, hi);
    }

    /**
     *
     */
    private static StabTypeInfo parseTypeDef(String stuff, Attributes attr) {
        char typedesc = 0;
        String info = null;
        boolean ds = Main.getOptions().getDebugStabs();

        //debugPrintln("          TypeDef: type stuff = '" + stuff + "'");

        if (stuff.length() > 0) {
            typedesc = stuff.charAt(0);
            if (typedesc == '(')
                // kps - make '(' be part of the typeInfo
                info = stuff.substring(0, stuff.length());
            else
                info = stuff.substring(1, stuff.length());
        }

        // http://theory.uwinnipeg.ca/gnu/gdb/stabs_71.html#SEC71
        if (typedesc == 0) {
            Main.fatal("????????");
            return null;
        }

        if (typedesc == 0)
            return null;
        else if (typedesc == '-') {
            // http://theory.uwinnipeg.ca/gnu/gdb/stabs_35.html#SEC35
        }
        else if (typedesc == '(') {
            return new StabTypeRef(info);
        } else if (typedesc == '*') {
            String type = info;
            //debugPrintln("                  [pointer to type " + type + "]");
        } else if (typedesc == '@') {
            attr = parseTypeAttribute(info, attr);
            info = attr.getInfo();
            return parseTypeDef(info, attr);
        } else if (typedesc == 'a') {
            //debugPrintln("                  [array]");
        } else if (typedesc == 'e') {
            //debugPrintln("                  [enumeration type]");
        } else if (typedesc == 'k') {
            String type = info;
            //debugPrintln("                  [const qualified type "
            //                 + type + "]");
        } else if (typedesc == 'r') {
            return parseSubrangeType(info, attr);
        } else if (typedesc == 'R') {
            //debugPrintln("                  [builtin floating type]");
        } else if (typedesc == 's') {
            //debugPrintln("                  [structure type]");
        } else if (typedesc == 'u') {
            // http://theory.uwinnipeg.ca/gnu/gdb/stabs_44.html#SEC44
            /*
             * Following the `u' is the number of bytes in the union.
             * After that is a list of union element descriptions. Their
             * format is name:type, bit offset into the union, number of
             * bytes for the element;.
             */
            //debugPrintln("                  [union]");
        } else if (ds)
            Main.fatal("Unknown typedesc '" + typedesc + "'");

        return null;
    }

    /**
     *
     */
    private static void symdescNotSupported(String string,
                                            long type, char symdesc) {
        boolean ds = Main.getOptions().getDebugStabs();

        Main.warn("StabParser: symdesc '" + symdesc
                  + "' not supported for stab type " + type + ":");
        Main.warn("StabParser: string is '" + string + "'");
        if (ds) {
            Main.fatal("Fix the code please.");
        }
    }

    private static Stab makeStab(String string,
                                 long type,
                                 long other,
                                 long desc,
                                 Expression value,
                                 char symdesc) {
        boolean ds = Main.getOptions().getDebugStabs();

        // stab symbol types:
        // http://theory.uwinnipeg.ca/gnu/gdb/stabs_69.html#SEC69
        // symbol descriptors:
        // http://theory.uwinnipeg.ca/gnu/gdb/stabs_70.html#SEC70
        if (type == Stab.N_GSYM) { // 32
            if (symdesc == 'G')
                return new GlobalVariableStab
                    (string, type, other, desc, value);
            symdescNotSupported(string, type, symdesc);
        }
        else if (type == Stab.N_FUN) { // 36
            if (symdesc == 'F')
                // http://theory.uwinnipeg.ca/gnu/gdb/stabs_12.html#SEC12
                return new GlobalFunctionStab
                    (string, type, other, desc, value);
            if (symdesc == 'f')
                // http://theory.uwinnipeg.ca/gnu/gdb/stabs_12.html#SEC12
                return new FileScopeFunctionStab
                    (string, type, other, desc, value);
            if (symdesc == 0)
                // example:         .stabs  "",36,0,0,.Lscope1-main
                // kps - this should be some subclass of Stab
                return new Stab(string, type, other, desc, value);

            symdescNotSupported(string, type, symdesc);
        }
        else if (type == Stab.N_STSYM) { // 38
            if (symdesc == 'V')
                // http://theory.uwinnipeg.ca/gnu/gdb/stabs_22.html#SEC22
                return new ProcedureScopeStaticVariableStab
                    (string, type, other, desc, value);
            symdescNotSupported(string, type, symdesc);
        }
        else if (type == Stab.N_LCSYM) { // 40
            if (symdesc == 'S')
                return new FileScopeVariableStab
                    (string, type, other, desc, value);
            symdescNotSupported(string, type, symdesc);
        }
        else if (type == Stab.N_OPT) { // 60
            return new DebuggerOptionsStab(string, type, other, desc, value);
        }
        else if (type == Stab.N_RSYM) { // 64
            if (symdesc == 'P')
                // http://theory.uwinnipeg.ca/gnu/gdb/stabs_20.html#SEC20
                return new RegisterVariableStab
                    (string, type, other, desc, value);
            symdescNotSupported(string, type, symdesc);
        }
        else if (type == Stab.N_SO) { // 100
            // http://theory.uwinnipeg.ca/gnu/gdb/stabs_9.html#SEC9
            return new SourceFileStab(string, type, other, desc, value);

        }
        else if (type == Stab.N_LSYM) { // 128
            if (symdesc == 't' || symdesc == 'T')
                // http://theory.uwinnipeg.ca/gnu/gdb/stabs_43.html#SEC43
                return new TypeNameStab(string, type, other, desc, value);
            if (symdesc == '(')
                // http://theory.uwinnipeg.ca/gnu/gdb/stabs_18.html#SEC18
                return new StackVariableStab(string, type, other, desc, value);
            symdescNotSupported(string, type, symdesc);
        }
        else if (type == Stab.N_BINCL) { // 130
            // http://theory.uwinnipeg.ca/gnu/gdb/stabs_10.html#SEC10
            return new BeginIncludeFileStab(string, type, other, desc, value);
        }
        else if (type == Stab.N_PSYM) { // 160
            // http://theory.uwinnipeg.ca/gnu/gdb/stabs_24.html#SEC24
            if (symdesc == 'p')
                return new ParameterVariableStab
                    (string, type, other, desc, value);
            symdescNotSupported(string, type, symdesc);
        }

        Main.warn("StabParser: Unknown stabs of type "
                  + type + ": \"" + string + "\"");
        if (ds)
            Main.fatal("ARGH");
        return new Stab(string, type, other, desc, value);
    }

    /**
     *
     */
    public static Stab parse(String string,
                             long type,
                             long other,
                             long desc,
                             Expression value) {

        boolean ds = Main.getOptions().getDebugStabs();
        String name = null, next = null;
        char symdesc = 0; // unknown
        String typeInfo = null, typeRef = null;
        ArrayList typeDef = null; // list for type information

        if (value == null)
            return parseStabd(type, other, desc);
        if (string == null)
            return parseStabn(type, other, desc, value);

        /* ------------------------------------------------------------ */
        /* ------------------------------------------------------------ */
        /* ------------------------------------------------------------ */

        // figure out name, symdesc and typeInfo parts of 'string'

        int colon = string.indexOf(":");

        // name is the name of the symbol represented by the stab
        if (colon == -1) {
            // no colon in string
            name = string;
        }
        else {
            String rest; // stuff after colon
 
            /*
             * The symbol-descriptor following the `:' is an alphabetic
             * character that tells more specifically what kind of symbol
             * the stab represents. If the symbol-descriptor is omitted,
             * but type information follows, then the stab represents
             * a local variable.
             */
            name = string.substring(0, colon);
            rest = string.substring(colon + 1, string.length());
            if (rest.length() > 0) {
                symdesc = rest.charAt(0);
                if (symdesc == '(')
                    // kps - make '(' be part of the typeInfo
                    typeInfo = rest.substring(0, rest.length());
                else
                    typeInfo = rest.substring(1, rest.length());
            }
        }

        /*
         * At this point we've figured out name, symbol-descriptor
         * and type-information. Now let's try to figure out what information
         * the type-information holds.
         */

        // http://theory.uwinnipeg.ca/gnu/gdb/stabs_70.html#SEC70

        /*
         * type-information is either a type-number, or `type-number='.
         * A type-number alone is a type reference, referring directly to a
         * type that has already been defined.
         */
        // let's figure out if this is a type reference or a type definition
        if (typeInfo != null) {
            int equals = typeInfo.indexOf("=");

            if (equals == -1) {
                // type reference
                typeRef = typeInfo;
            }
            else {
                /*
                 * In a type definition, if the character that follows the
                 * equals sign is non-numeric then it is a type-descriptor,
                 * and tells what kind of type is about to be defined. Any
                 * other values following the type-descriptor vary, depending
                 * on the type-descriptor.
                 * See section Table of Type Descriptors, for a list of
                 * type-descriptor values:
                 * http://theory.uwinnipeg.ca/gnu/gdb/stabs_71.html#SEC71
                 * If a number follows the `=' then the number is a
                 * type-reference. For a full description of types, section
                 * Defining Types:
                 * http://theory.uwinnipeg.ca/gnu/gdb/stabs_29.html#SEC29
                 */
                typeDef = new ArrayList();

                StringTokenizer st = new StringTokenizer(typeInfo, "=");
                while (st.hasMoreTokens()) {
                    String s = st.nextToken();
                    StabTypeInfo stuff = parseTypeDef(s, null);
                    typeDef.add(stuff);
                }
            }
        }

        /* ------------------------------------------------------------ */
        /* ------------------------------------------------------------ */
        /* ------------------------------------------------------------ */

        Stab stab = makeStab(string, type, other, desc, value, symdesc);
        if (stab == null)
            Main.fatal("ARGH");
        stab.setName(name);
        stab.setSymDesc(symdesc);
        stab.setTypeInfo(typeInfo);
        stab.setTypeRef(typeRef);
        stab.setTypeDef(typeDef);
        
        return stab;
    }


    public static void debugPrintln(String s) {
        if (Main.getOptions().getDebugStabs())
            System.out.println(s);
    }
    public static void debugPrint(String s) {
        if (Main.getOptions().getDebugStabs())
            System.out.print(s);
    }
}
