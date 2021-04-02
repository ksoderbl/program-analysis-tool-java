/**
 * ParenthesisExpression.java
 */

package misc;

import program.Symbols;
import main.*;

/**
 * An expression in parenthesis.
 *
 * @author Kristian Söderblom
 */

public class ParenthesisExpression extends Expression {

    private Expression expr;

    public ParenthesisExpression(Expression expr) {
        this.expr = expr;
    }

    public String toString() {
        return "(" + expr + ")";
    }

    public long evaluate(Symbols syms) {
        return expr.evaluate(syms);
    }

}
