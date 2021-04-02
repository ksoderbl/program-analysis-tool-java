package stabs;

public class StabTypeRef extends StabTypeInfo {
    
    private String typename;

    public StabTypeRef(String typename) {
        this.typename = typename;
    }

    public String toString() {
        return typename;
    }
}
