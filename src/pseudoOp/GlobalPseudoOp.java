/**
 * GlobalPseudoOp.java
 *
 * @author Kristian Söderblom
 */

package pseudoOp;

/**
 * global pseudo op
 */
public class GlobalPseudoOp extends PseudoOp {

    /** Attributes */
    private String symbol;

    public String getSymbol() {
        return symbol;
    }

    /**
     * Constructs a new global pseudo op
     *
     * @param symbol the global symbol
     * @return the new pseudo op
     */
    public GlobalPseudoOp(String symbol) {
        this.symbol = symbol;
    }

    public String toString() {
        return ".global\t" + symbol;
    }

}
