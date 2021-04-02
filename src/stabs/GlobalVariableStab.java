/**
 * GlobalVariableStab.java
 */

package stabs;

import misc.Expression;
import main.*;

/**
 * @author Kristian Söderblom
 */
public class GlobalVariableStab extends Stab {

    /**
     * Constructs a new global variable stab
     *
     * @param string the stabs string
     * @param type the type of the stab
     * @param other the other field, almost always unused
     * @param desc the desc field
     * @param value the value of the stab
     * @return the new stabs pseudo op
     */
    public GlobalVariableStab(String string,
                              long type, long other, long desc,
                              Expression value) {
        super(string, type, other, desc, value);
        //Main.warn("global variable: " + string);
    }
}
