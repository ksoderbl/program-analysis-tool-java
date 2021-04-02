/**
 * Expression.java
 */

package misc;

import program.Symbols;

/**
 * An expression.
 *
 * @author Kristian Söderblom
 */

public abstract class Expression {

    public abstract long evaluate(Symbols syms);

}
