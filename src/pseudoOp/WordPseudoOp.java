/**
 * WordPseudoOp.java
 *
 * @author Kristian Söderblom
 */

package pseudoOp;

import misc.Expression;
import java.util.ArrayList;
import java.util.Iterator;
import main.*;

/**
 * word pseudo op
 */
public class WordPseudoOp extends PseudoOp {

    /** Attributes */
    private ArrayList exprList;

    public ArrayList getExpressions() {
        return exprList;
    }

    /**
     * Constructs a word pseudo op
     *
     * @param expr expression
     * @return the new word pseudo op
     */
    public WordPseudoOp(ArrayList exprList) {
        if (exprList.size() < 1)
            Main.fatal("exprList size < 1 in WordPseudoOp constructor.");
        this.exprList = exprList;
    }

    public String toString() {
        String s = ".word\t";
        Iterator it = exprList.iterator();
        Expression expr = (Expression)it.next();

        s += expr.toString();
        while (it.hasNext()) {
            expr = (Expression)it.next();
            s += ", " + expr.toString();
        }

        return s;
    }

}
