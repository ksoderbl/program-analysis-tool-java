
package program;

import input.Input;
import main.Main;

public class BranchTarget
{
    private String label = null;
    private Long offset = new Long(0);

    /**
     * Constructs a new branch target
     */
    public BranchTarget(String label, Long offset) {
        this.label = label;
        this.offset = offset;
    }
    public BranchTarget(String label) {
        this.label = label;
    }
    public BranchTarget(Long addr) {
        this.offset = addr;
    }

    // find the address of this branchtarget
    public Long getAddress(Program program) {
        if (label != null) {
            Symbols labels = program.getLabels();
            Long addr = (Long)labels.get(label);
            if (addr == null) {
                Main.warn("BranchTarget.getAddress: addr for label " + label
                          + " is null.");
            }
            return new Long(addr.longValue() + offset.longValue());
        }
        return offset;
    }

    public String getLabel() {
        return label;
    }
    public Long getOffset() {
        return offset;
    }

    public String toString() {
        if (this.label != null) {
            String s = this.label;
            if (this.offset < 0)
                s += "-" + -this.offset;
            else if (this.offset > 0)
                s += "+" +  this.offset;
            return s;
        }
        return "0x" + Long.toHexString(offset.longValue());
    }
}
