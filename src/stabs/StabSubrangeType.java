package stabs;

public class StabSubrangeType extends StabTypeInfo {
    private String subrangeoftype;
    private long size; // bytes
    private long min;
    private long max;

    public StabSubrangeType(String subrangeoftype,
                            long size, long min, long max) {
        //Main.warn("StabSubrangeType: " + subrangeoftype + " " + size + " "
        //          + min + " " + max);
        this.subrangeoftype = subrangeoftype;
        this.size = size;
        this.min = min;
        this.max = max;
    }

}

