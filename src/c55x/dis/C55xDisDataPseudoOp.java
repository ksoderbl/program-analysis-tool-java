/**
 * C55xDisDataPseudoOp.java
 *
 * @author Kristian Söderblom
 */

package c55x.dis;

import pseudoOp.PseudoOp;

/**
 * data pseudo op
 */
public class C55xDisDataPseudoOp extends PseudoOp {

    /** Attributes */
    private Long addr;
    private String data;
    private String dataType;

    /**
     * Constructs a new data pseudo op
     *
     * @param foo bar
     * @return the new pseudo op
     */

    public C55xDisDataPseudoOp(String addr,
                               String data,
                               String dataType) {
        this.addr = Long.valueOf(addr, 16);
        this.data = data;
        this.dataType = dataType;
    }

    public Long getAddr() {
        return addr;
    }

    public String getData() {
        return data;
    }

    public long getDataValue(){
        return Long.decode("0x"+data);
    }

    // 000000: fda8             .word 0xfda8
    public String toString() {
        return "   " + dataType
            + " 0x" + data;
    }

}
