/**
 * ProcedureScopeStaticVariableStab.java
 */

package stabs;

import misc.Expression;
import main.*;

/**
 * @author Kristian Söderblom
 */
public class ProcedureScopeStaticVariableStab extends Stab {

    /**
     * Constructs a new procedure scope static variable stab
     *
     * @param string the stabs string
     * @param type the type of the stab
     * @param other the other field, almost always unused
     * @param desc the desc field
     * @param value the value of the stab
     * @return the new stabs pseudo op
     */
    public ProcedureScopeStaticVariableStab(String string,
                                            long type, long other, long desc,
                                            Expression value) {
        super(string, type, other, desc, value);
        //Main.warn("procedure scope static variable: " + string);
    }
}
