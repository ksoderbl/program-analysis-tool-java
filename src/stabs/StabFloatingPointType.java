package stabs;

public class StabFloatingPointType extends StabTypeInfo {
    private String subrangeoftype;
    private long size; // bytes

    public StabFloatingPointType(String subrangeoftype, long size) {
        //Main.warn("StabFloatingPointType: " + subrangeoftype + " " + size);
        this.subrangeoftype = subrangeoftype;
        this.size = size;
    }
}
