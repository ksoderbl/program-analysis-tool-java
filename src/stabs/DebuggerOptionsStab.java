/**
 * DebuggerOptionsStab.java
 */

package stabs;

import misc.Expression;

/**
 * @author Kristian Söderblom
 */
public class DebuggerOptionsStab extends Stab {

    /**
     * Constructs a debugger options stab pseudo op.
     * Type is N_OPT (60).
     *
     * @param string the stabs string
     * @param type the type of the stab
     * @param other the other field, almost always unused
     * @param desc the desc field
     * @param value the value of the stab
     * @return the new stabs pseudo op
     */
    public DebuggerOptionsStab(String string,
                               long type, long other, long desc,
                               Expression value) {
        super(string, type, other, desc, value);
    }
}
