/**
 * NumExpression.java
 */

package misc;

import program.Symbols;

/**
 * An Num expression.
 *
 * @author Kristian Söderblom
 */

public class NumExpression extends Expression {

    private long num;

    public NumExpression(long num) {
        this.num = num;
    }

    public String toString() {
        return "" + num;
    }

    public long evaluate(Symbols sym) {
        return num;
    }
}
