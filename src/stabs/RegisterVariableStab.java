/**
 * RegisterVariableStab.java
 */

package stabs;

import misc.Expression;

/**
 * @author Kristian Söderblom
 */
/* http://theory.uwinnipeg.ca/gnu/gdb/stabs_20.html#SEC20 */
public class RegisterVariableStab extends Stab {

    /**
     * Constructs a new register variable stab pseudo op.
     * Type is N_RSYM (64).
     *
     * @param string the stabs string
     * @param type the type of the stab
     * @param other the other field, almost always unused
     * @param desc the desc field
     * @param value the value of the stab
     * @return the new stabs pseudo op
     */
    public RegisterVariableStab(String string,
                                long type, long other, long desc,
                                Expression value) {
        super(string, type, other, desc, value);
    }
}
