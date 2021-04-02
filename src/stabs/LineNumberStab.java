/**
 * LineNumberStab.java
 */

package stabs;

import misc.Expression;

/**
 * @author Kristian Söderblom
 */
public class LineNumberStab extends Stab {

    /*
     * http://theory.uwinnipeg.ca/gnu/gdb/stabs_11.html#SEC11
     *
     * An N_SLINE symbol represents the start of a source line. The desc
     * field contains the line number and the value contains the code
     * address for the start of that source line. On most machines the
     * address is absolute; for stabs in sections (see section Using Stabs
     * in Their Own Sections), it is relative to the function in which
     * the N_SLINE symbol occurs.
     */

    /**
     * Constructs a new line number stab.
     * Type is N_SLINE.
     *
     * @param string the stabs string (should be null)
     * @param type the type of the stab
     * @param other the other field, almost always unused
     * @param desc the desc field
     * @param value the value of the stab
     * @return the new stabs pseudo op
     */
    public LineNumberStab(String string,
                          long type, long other, long desc,
                          Expression value) {
        super(string, type, other, desc, value);
    }
}
