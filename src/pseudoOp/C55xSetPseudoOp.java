/**
 * C55xSetPseudoOp.java
 *
 * @author Kristian Söderblom
 */

package pseudoOp;

/**
 * c55xSet pseudo op
 */
public class C55xSetPseudoOp extends PseudoOp {

    /** Attributes */
    private String symbol;
    private long value;

    public String getSymbol() {
        return symbol;
    }

    /**
     * Constructs a new c55x .set pseudo op
     *
     * @param symbol the symbol
     * @param value the value of the symbol
     * @return the new pseudo op
     */
    public C55xSetPseudoOp(String symbol, long value) {
        this.symbol = symbol;
        this.value = value;
    }

    public String toString() {
        return "" + symbol + " .set " + value;
    }

}
