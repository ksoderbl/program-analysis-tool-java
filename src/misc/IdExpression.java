/**
 * IdExpression.java
 */

package misc;

import program.Symbols;
import main.*;

/**
 * An Id expression.
 *
 * @author Kristian Söderblom
 */

public class IdExpression extends Expression {

    private String id;

    public IdExpression(String id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String toString() {
        return id;
    }
    public long evaluate(Symbols syms) {
        if (syms == null) // no symbols: test mode
            return 0;
        Long l = (Long)syms.get(id);
            // kps - ok ?
        if (l == null) {
            Main.warn("IdExpression: '"+id+" not found: shouldn't happen???");
            return 0;
        }
        return l.longValue();
    }
}
