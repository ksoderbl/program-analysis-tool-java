/**
 * UnaryExpression.java
 */

package misc;

import program.Symbols;
import main.*;

/**
 * A Unary expression.
 *
 * @author Kristian Söderblom
 */

public class UnaryExpression extends Expression {

    private Expression expr;
    private String oper;

    public UnaryExpression(String oper,
                           Expression expr) {
        this.expr = expr;
        this.oper = oper;
    }

    public String toString() {
        return "" + oper + "" + expr;
    }

    public long evaluate(Symbols syms) {
        long val;

        val = expr.evaluate(syms);

        if (oper.equals("+"))
            return +val;
        if (oper.equals("-"))
            return -val;
        if (oper.equals("~"))
            return ~val;
        if (oper.equals("!")) 
            return (val == 0 ? 1 : 0);
        Main.fatal("couldn't evaluate expression '"+this+"'");
        return 0;
    }

}
