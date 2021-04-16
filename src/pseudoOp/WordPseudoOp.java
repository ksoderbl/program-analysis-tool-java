/**
 * WordPseudoOp.java
 *
 * @author Kristian Söderblom
 */

package pseudoOp;

import misc.Expression;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import main.*;

/**
 * word pseudo op
 */
public class WordPseudoOp extends PseudoOp {

    /** Attributes */
    private List<Expression> exprList;

    public List<Expression> getExpressions() {
        return exprList;
    }

    /**
     * Constructs a word pseudo op
     *
     * @param expr expression
     * @return the new word pseudo op
     */
    public WordPseudoOp(List<Object> exprList) {
        if (exprList.size() < 1)
            Main.fatal("exprList size < 1 in WordPseudoOp constructor.");
        this.exprList = new ArrayList<Expression>();
        for ( Object o : exprList ) {
            this.exprList.add((Expression)o);
        }
    }

    public String toString() {
        String s = ".word\t";
        Iterator<Expression> it = exprList.iterator();
        Expression expr = it.next();

        s += expr.toString();
        while (it.hasNext()) {
            expr = it.next();
            s += ", " + expr.toString();
        }

        return s;
    }

}
