/**
 * RightBraceStab.java
 */

package stabs;

import misc.Expression;

/**
 * @author Kristian Söderblom
 */
public class RightBraceStab extends Stab {

    // http://theory.uwinnipeg.ca/gnu/gdb/stabs_14.html#SEC14

    /**
     * Constructs a new right brace stab.
     * Type is N_RBRAC.
     *
     * @param string the stabs string (should be null)
     * @param type the type of the stab
     * @param other the other field, almost always unused
     * @param desc the desc field
     * @param value the value of the stab
     * @return the new stabs pseudo op
     */
    public RightBraceStab(String string,
                          long type, long other, long desc,
                          Expression value) {
        super(string, type, other, desc, value);
    }
}
