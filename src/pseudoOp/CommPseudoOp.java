/**
 * CommPseudoOp.java
 *
 * @author Kristian Söderblom
 */

package pseudoOp;

// CHECK
// http://www.gnu.org/software/binutils/manual/gas-2.9.1/html_chapter/as_7.html

/**
 * "common symbol" pseudo op
 */
public class CommPseudoOp extends PseudoOp {
    /** Attributes */

    private String symbol;
    private long length;
    private long alignment;

    public String getSymbol() {
        return symbol;
    }
    public long getLength() {
        return length;
    }
    public long getAlignment() {
        return alignment;
    }

    /**
     * Constructs a comm pseudo op
     *
     * @param symbol name of the common symbol
     * @param length length of the memory area
     * @param align alignment of the memory area
     * @return the new comm pseudo op
     */
    public CommPseudoOp(String symbol, long length, long alignment) {
        this.symbol = symbol;
        this.length = length;
        this.alignment = alignment;
    }

    public String toString() {
        return ".comm\t" + symbol + "," + length + "," + alignment;
    }

}
