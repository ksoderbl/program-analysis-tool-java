/**
 * SourceFileStab.java
 */

package stabs;

import misc.Expression;
import main.*;

/**
 * @author Kristian Söderblom
 */
/* http://theory.uwinnipeg.ca/gnu/gdb/stabs_9.html#SEC9 */
public class SourceFileStab extends Stab {

    /**
     * Constructs a new source file stab pseudo op.
     * Type is N_SO (100).
     *
     * @param string the stabs string
     * @param type the type of the stab
     * @param other the other field, almost always unused
     * @param desc the desc field
     * @param value the value of the stab
     * @return the new stabs pseudo op
     */
    public SourceFileStab(String string,
                          long type, long other, long desc,
                          Expression value) {
        super(string, type, other, desc, value);
    }
}
