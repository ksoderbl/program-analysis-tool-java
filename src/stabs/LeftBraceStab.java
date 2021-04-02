/**
 * LeftBraceStab.java
 */

package stabs;

import misc.Expression;

/**
 * @author Kristian Söderblom
 */
public class LeftBraceStab extends Stab {

    // http://theory.uwinnipeg.ca/gnu/gdb/stabs_14.html#SEC14

    /**
     * Constructs a new left brace stab.
     * Type is N_LBRAC.
     *
     * @param string the stabs string (should be null)
     * @param type the type of the stab
     * @param other the other field, almost always unused
     * @param desc the desc field
     * @param value the value of the stab
     * @return the new stabs pseudo op
     */
    public LeftBraceStab(String string,
                         long type, long other, long desc,
                         Expression value) {
        super(string, type, other, desc, value);
    }
}
