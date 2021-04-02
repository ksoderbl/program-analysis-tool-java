/**
 * BinaryExpression.java
 */

package misc;

import program.Symbols;
import main.*;

/**
 * An Binary expression.
 *
 * @author Kristian Söderblom
 */

public class BinaryExpression extends Expression {

    private Expression expr1, expr2;
    private String oper;

    public BinaryExpression(Expression expr1,
                            String oper,
                            Expression expr2) {
        this.expr1 = expr1;
        this.oper = oper;
        this.expr2 = expr2;
    }

    public String toString() {
        return "" + expr1 + "" + oper + "" + expr2;
    }

    public long evaluate(Symbols syms) {
        long val1, val2;

        val1 = expr1.evaluate(syms);
        val2 = expr2.evaluate(syms);

        if (oper.equals("+"))
            return (val1 + val2);
        if (oper.equals("-"))
            return (val1 - val2);
        if (oper.equals("*"))
            return (val1 * val2);
        if (oper.equals("/"))
            return (val1 / val2);
        if (oper.equals("%"))
            return (val1 % val2);

        if (oper.equals("|"))
            return (val1 | val2);
        if (oper.equals("^"))
            return (val1 ^ val2);
        if (oper.equals("&"))
            return (val1 & val2);

        if (oper.equals("==")) {
            if (val1 == val2)
                return 1;
            return 0;
        }

        if (oper.equals("!=")) {
            if (val1 != val2)
                return 1;
            return 0;
        }

        if (oper.equals("<")) {
            if (val1 < val2)
                return 1;
            return 0;
        }

        if (oper.equals(">")) {
            if (val1 > val2)
                return 1;
            return 0;
        }

        if (oper.equals(">=")) {
            if (val1 >= val2)
                return 1;
            return 0;
        }

        if (oper.equals("<=")) {
            if (val1 <= val2)
                return 1;
            return 0;
        }

        if (oper.equals("<<")) {
            return val1 << val2;
        }

        if (oper.equals(">>")) {
            return val1 >> val2;
        }

        Main.fatal("Unknown operator in BinaryExpression '"+this+"'");
        return 0;
    }

}
