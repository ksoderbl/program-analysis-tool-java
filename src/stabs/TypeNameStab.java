/**
 * TypeNameStab.java
 */

package stabs;

import misc.Expression;
import main.*;

/**
 * @author Kristian Söderblom
 */
public class TypeNameStab extends Stab {

    /**
     * Constructs a new stack variable stab pseudo op
     *
     * @param string the stabs string
     * @param type the type of the stab
     * @param other the other field, almost always unused
     * @param desc the desc field
     * @param value the value of the stab
     * @return the new stabs pseudo op
     */
    public TypeNameStab(String string,
                        long type, long other, long desc,
                        Expression value) {
        super(string, type, other, desc, value);
        //Main.warn("type name: " + string);
    }
}
