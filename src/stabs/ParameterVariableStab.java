/**
 * ParameterVariableStab.java
 */

package stabs;

import misc.Expression;

/**
 * @author Kristian Söderblom
 */
public class ParameterVariableStab extends Stab {

    /**
     * Constructs a new parameter variable stab.
     * Type is N_PSYM (160).
     *
     * @param string the stabs string
     * @param type the type of the stab
     * @param other the other field, almost always unused
     * @param desc the desc field
     * @param value the value of the stab
     * @return the new stabs pseudo op
     */
    public ParameterVariableStab(String string,
                                 long type, long other, long desc,
                                 Expression value) {
        super(string, type, other, desc, value);
    }
}
