/**
 * EndIncludeFileStab.java
 */

package stabs;

import misc.Expression;

/**
 * @author Kristian Söderblom
 */
public class EndIncludeFileStab extends Stab {

    /**
     * Constructs an end include file stab pseudo op.
     * Type is N_EINCL (162).
     *
     * @param string the stabs string
     * @param type the type of the stab
     * @param other the other field, almost always unused
     * @param desc the desc field
     * @param value the value of the stab
     * @return the new stabs pseudo op
     */
    public EndIncludeFileStab(String string,
                              long type, long other, long desc,
                              Expression value) {
        super(string, type, other, desc, value);
    }
}
