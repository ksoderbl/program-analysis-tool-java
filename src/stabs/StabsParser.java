
package stabs;

import java.util.*;
import java.io.*;
import input.Input;
import program.Program;
import main.UserOptions;
import misc.Expression;

public class StabsParser{
    //    public Hashtable SymbolDesc = new Hashtable();

    public static void main (String[] args) throws Exception{  
        boolean noSeparators = false;
        String line = "";
        System.out.println(parseNumberField("*ghjhgj12343ffdfg"));
        System.out.println(parseStringField("*ghjhgj12343ffdfg"));
        Vector<Integer> ints = new Vector<Integer>();
        ints = parseValueField("(23,43)");
        
        for (int i=0; i < ints.size(); i++){
            System.out.println(ints.elementAt(i));
            
        }
        line = getParseString();
        // parseString(line);
    }

    public static void parseString(String line, Program program){
        
        UserOptions options = program.getOptions();
        boolean noSeparators = false;
        if (options.getDebugStabs()) System.out.println("* Stabs string *");
        if (options.getDebugStabs()) System.out.println(line);
        // line = getParseString();
        String token = null;
        String typeName = null;
        StringTokenizer st = new StringTokenizer(line,";");
        if (st.countTokens() == 0){
            if (options.getDebugStabs()) System.out.println("Error: empty string.");
            return;
        }
        if (st.countTokens() == 1) noSeparators = true;
        else noSeparators = false;
        while (st.hasMoreTokens()){
            int nestSym = 0;
            StringTokenizer symbol = new StringTokenizer(st.nextToken(),":");                
            if (symbol.countTokens() > 1){
                while (symbol.hasMoreTokens()){
                    nestSym++;
                    int nestType = 0;
                    StringTokenizer type = new StringTokenizer(symbol.nextToken(),"=");
                    if (type.countTokens() > 1){
                        while (type.hasMoreTokens()){
                            nestType++;
                            String typeS = type.nextToken();
                            switch (nestType){
                            case 1:
                                if (options.getDebugStabs()) System.out.print("left:symdesc:" +typeS);
                                getSymbolDesc(typeS, program, typeName);
                                break;
                            default:
                                if (options.getDebugStabs()) System.out.print("right:typedesc:" +typeS);
                                getTypeDesc(typeS, program);
                                break;
                            }  
                        }
                    }
                    // we have now the left item of : , which doesnt contain a =
                    // which means a type like int:
                    else{
                        String symDesc = type.nextToken();
                        switch(nestSym){
                        case 1:
                            if (options.getDebugStabs()) System.out.println("left:Type: "+ symDesc);
                            typeName = symDesc;
                            break;
                        default:
                             if (options.getDebugStabs()) System.out.print("right: Symdesc: " + symDesc);
                            getSymbolDesc(symDesc, program, typeName);
                            break;
                        }
                        
                    }
                    
                }
            }
            // No ; or : found in entire string
            else if (noSeparators) 
                 if (options.getDebugStabs()) System.out.println("This is just a string, with no ; or :");
            // no : , but check now for =, which can occur
            else { 
                int nestType = 0;
                StringTokenizer type = new StringTokenizer(symbol.nextToken(),"=");
                if (type.countTokens() > 1){
                    while (type.hasMoreTokens()){
                        nestType++;
                        String typeS = type.nextToken();
                        switch (nestType){
                        case 1:
                            if (options.getDebugStabs()) System.out.print("left:symdesc:" +typeS);
                            getSymbolDesc(typeS, program, typeName);
                            break;
                        default:
                            if (options.getDebugStabs())  System.out.print("right:typedesc:" +typeS);
                            getTypeDesc(typeS, program);
                            break;
                        }  
                    }
                }
                // no : or = found in string
                else if (options.getDebugStabs()) System.out.println("type-number:"+type.nextToken());
            }
        }

        
    }

    
    // The symbol descriptor is the character which follows the colon in many stabs, and 
    // which tells what kind of stab it is.
    public static void getSymbolDesc(String token, Program program, String typeName){
        UserOptions options = program.getOptions();
        char sym1 = token.charAt(0);
        char sym2 = token.charAt(1);
        Vector<Integer> args = new Vector<Integer>();
        HashMap<String, Type> types = program.getDebugData().getTypes();
        HashMap<String, Variable> vars = program.getDebugData().getVariables();
        Type type;
        switch (sym1){
        case 'a':
            if (options.getDebugStabs()) System.out.println(" parameter passed by ref in register");
            break;
        case 'f':
            if (options.getDebugStabs())  System.out.println(" static Function");
            break; 
        case 'F':
            if (options.getDebugStabs()) System.out.println(" Global Function");
            break;
        case 'G':
            if (options.getDebugStabs()) System.out.println(" global variable");
            args = parseValueField(token.substring(1, token.length()));
            type = types.get(args.get(1).toString());
            if (type == null){
                type = new Type(typeName);
                types.put(args.get(1).toString(), type);
                Variable var = new Variable(type, typeName);
                vars.put(args.get(1).toString(), var);
            }
            else {
                Variable var = new Variable(type, typeName);
                vars.put(typeName, var);
            }
            // type = (Type)types.get(typeName);
            //type.setSymbolDesc("G");
            break;
        case 'p':
            if (options.getDebugStabs())  System.out.println(" func param on stack");
        case 'r':
            if (options.getDebugStabs()) System.out.println(" registervariable ");
            break;
        case 't':
            if (options.getDebugStabs()) System.out.println(" type name");
            args = parseValueField(token.substring(1, token.length()));
            type = new Type(typeName);
            types.put(args.get(1).toString(), type);
            break;
        case 'T':
            if (options.getDebugStabs()) System.out.println(" enumeration, structure or union");
            args = parseValueField(token.substring(1, token.length()));
            type = new Type(typeName);
            types.put(args.get(1).toString(), type);
            
            break;  
        default:
            if (options.getDebugStabs()){
                if (java.lang.Character.isDigit(sym1)) System.out.println(" digit");
                else System.out.println(" Unknown symdesc.");
            }
        }
    }

    //The type descriptor is the character which follows the type number and an equals sign. 
    //It specifies what kind of type is being defined. See section The String Field, for more 
    //information about their use.

    public static void getTypeDesc(String token, Program program){
        UserOptions options = program.getOptions();
        char type1 = token.charAt(0);
        char type2 = token.charAt(1);
        switch (type1){
        case '*':
            if (options.getDebugStabs()) System.out.println(" pointer to type-info");
             break;
        case 'a':
            if (options.getDebugStabs()) System.out.println(" array");
            break;
        case 'f':
            if (options.getDebugStabs()) System.out.println(" Function type");
            break; 
        case 'g':
            if (options.getDebugStabs()) System.out.println(" range type ");
            break;  
        case 'p':
            if (options.getDebugStabs()) System.out.println(" packed array");
            break;
        case 'r':
            if (options.getDebugStabs()) System.out.println(" range type ");
            break;
        case 's':
            if (options.getDebugStabs()){
                System.out.print(" structure type ");
                System.out.println(" type num: "+parseNumberField(token));
                System.out.println(" Type: "+parseStringField(token));
            }
            break;
        default:
            if (options.getDebugStabs()){
                if (java.lang.Character.isDigit(type1)) System.out.println("digit");
                else System.out.println(" Unknown type");
            }
        }
    }

    public static int parseNumberField(String string){
        int a = 0, b = 0;
        while (!java.lang.Character.isDigit(string.charAt(a))){a++;}
        while (java.lang.Character.isDigit(string.charAt(a+b))){b++;}
        //        return string.substring(a, a+b);
        Integer integer = Integer.parseInt(string.substring(a, a+b));
        return integer.intValue();  
        
    }
    
    public static String parseStringField(String string){
        int a = 0;
        while (!java.lang.Character.isDigit(string.charAt(a))){a++;}
        while (java.lang.Character.isDigit(string.charAt(a))){a++;}
        return string.substring(a, string.length());
    }

    // parses a parenthesis with format (#1,#2,..#n) where n is the number of fields
    // and stores integers into a vector
    public static Vector<Integer> parseValueField(String string){
        String newStr;
        Vector<Integer> num = new Vector<Integer>();
        newStr = string.replace('(',' ');
        newStr = newStr.replace(')',' ');
        newStr = newStr.trim();
        StringTokenizer numbers = new StringTokenizer(newStr,",");
        while (numbers.hasMoreTokens()){
            Integer integer = Integer.parseInt(numbers.nextToken());
            num.addElement(integer);
        }
        
        return num;
    }



    public static void parseType(long type, String string, Input input, Program program) {
        UserOptions options = program.getOptions();
        // stab string: http://theory.uwinnipeg.ca/gnu/gdb/stabs_4.html
        // example:
        // "long double:t(0,14)=r(0,1);8;0;"
        // "name:symbol-descriptor type-information"
        // name              : 
        // symbol-descriptor : http://theory.uwinnipeg.ca/gnu/gdb/stabs_70.html#SEC70
        // type-information  : http://theory.uwinnipeg.ca/gnu/gdb/stabs_71.html#SEC71
        // name              : long double
        // symbol-descriptor : t
        // type-information  : (0,14)=r(0,1);8;0;
        if (!options.getDebugStabs())
            return;

        if (type == Stab.N_GSYM) {
            System.out.println("STABS: global symbol: " + string);
        }
        else if (type == Stab.N_FUN) {
            System.out.println("STABS: function name: " + string);
        }
        else if (type == Stab.N_LCSYM) {
            System.out.println("STABS: static variable: " + string);
        }
        else if (type == Stab.N_OPT) {
            System.out.println("STABS: debugger options: " + string);
        }
        else if (type == Stab.N_RSYM) {
            System.out.println("STABS: register variable: " + string);
        }
        else if (type == Stab.N_SO) {
            System.out.println("STABS: path and name of source file: " + string);
            input.addSourceFile(string);
        }
        else if (type == Stab.N_LSYM) {
            System.out.println("STABS: stack variable: " + string);
        }
        else if (type == Stab.N_BINCL) {
            System.out.println("STABS: beginning of an include file: "
                               + string);
        }
        else if (type == Stab.N_PSYM) {
            System.out.println("STABS: parameter variable: " + string);
        }
        else if (type == Stab.N_EINCL) {
            System.out.println("STABS: end of an include file: " + string);
        }
        else {
            System.out.println("STABS: unsupported stabs type " + type
                               + ": \"string\"");
        }
        /*System.out.println("STABS: stabs:"
                           + " type  = " + type
                           + " other = " + other
                           + " desc  = " + desc
                           + " value = " + value);*/
    }
    

    public static String getParseString(){
        String line ="";
        System.out.println("* Stabs string *");
        /*table.add(type);*/
        //line = "var_const:S1";
        //line = "argv:p20=*21=*2";
 
        //line = "hello.c";

        // char char_vec[3] = {'a','b','c'};
        //line = "char_vec:G19=ar1;0;2;2";

        // enum e_places {first,second=3,last};
        //line = "e_places:T22=efirst:0,second:3,last:4,;";

        //struct s_tag {
        //int   s_int;
        //float s_float;
        //char  s_char_vec[8];
        // struct s_tag* s_next;
        //} g_an_s;
        //line = "s_tag:T16=s20s_int:1,0,32;s_float:12,32,32;s_char_vec:17=ar1;0;7;2,64,64;s_next:18=*16,128,32;;";

        //line = "int:t1=r1;-2147483648;2147483647;";

        // int (*g_pf)();
        //line = "g_pf:G24=*25=f1";
        line = "char_vec:G(0,21)=ar(0,22)=r(0,22);0000000000000;0037777777777;;0;4;(0,23)=ar(0,22);0;9;(0,2)";        
        System.out.println(line);        
        System.out.println("* parse starts *");
        return line;
        
    }

    public static void stabsDebug(String line){
        System.out.println(line);
    }

    public static void parseStabs(String string,
                                  long type,
                                  long other,
                                  long desc,
                                  Expression value,
                                  Program program,
                                  Input input)
    {
        parseString(string, program);
        parseType(type, string, input, program);
    }
}
