/**
 * C55xDisSectionPseudoOp.java
 *
 * @author Kristian Söderblom
 */

package c55x.dis;

import pseudoOp.PseudoOp;

/**
 * section pseudo op
 */
public class C55xDisSectionPseudoOp extends PseudoOp {

    /** Attributes */
    private String sectionType;
    private String name;
    private String id; // can be null
    private long numItems;
    private String itemType;
    private long addr;


    /**
     * Constructs a new section pseudo op
     *
     * @param name the name of the section
     * @return the new pseudo op
     */
    // TEXT Section .text, 0x94 bytes at 0x0
    // TEXT Section .text:unpacki32, 0x127 bytes at 0x0
    public C55xDisSectionPseudoOp(String sectionType,
                                  String name,
                                  String id,
                                  long numItems,
                                  String itemType,
                                  long addr) {
        this.sectionType = sectionType; // e.g. "TEXT"
        this.name = name;               // e.g. ".text"
        this.id = id;                   // e.g. "unpacki32"
        this.numItems = numItems;       // e.g. 0x94
        this.itemType = itemType;       // e.g. "bytes"
        this.addr = addr;               // e.g. 0x0
    }

    public Long getAddr() {
        return new Long(addr);
    }

    public String getSectionType() {
        return sectionType;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "" + sectionType + " Section " + name
            + (id == null ? "" : ":" + id)
            + ", "
            + "0x" + (Long.toHexString(numItems)).toUpperCase()
            + " " + itemType + " at "
            + "0x" + (Long.toHexString(addr)).toUpperCase();
    }

}
