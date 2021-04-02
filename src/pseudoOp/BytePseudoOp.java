/**
 * BytePseudoOp.java
 *
 * @author Kristian Söderblom
 */

package pseudoOp;

import misc.Expression;

/**
 * byte pseudo op
 */
public class BytePseudoOp extends PseudoOp {

    /** Attributes */
    private Expression expr;

    public Expression getExpression() {
        return expr;
    }

    /**
     * Constructs a byte pseudo op
     *
     * @param expr expression
     * @return the new byte pseudo op
     */
    public BytePseudoOp(Expression expr) {
        this.expr = expr;
    }

    public String toString() {
        return ".byte\t" + expr;
    }

}
