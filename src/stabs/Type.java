
package stabs;

/**
 *  @author Peter Majorin
 *  a generic representation for a type, not stabs-specific
 */

public class Type {

    private String typeName;
    private String instanceName;
    private String typeDesc;
    private String symbolDesc;

    public Type(String name){
            this.typeName = name;
    }

    // name of type
    public String getName(){
        return typeName;
    }
    
    public String getInstanceName(){
        return instanceName;
    }


    public void setTypeDesc(String typeDesc){
        this.typeDesc = typeDesc;
    }

    // e.g. =ar 
    public String getTypeDesc(){
        return typeDesc;
    }
    
    public void setSymbolDesc(String symbolDesc){
        this.symbolDesc = symbolDesc;
    }

    // e.g. :G
    public String getSymbolDesc(){
        return symbolDesc;
    }


}
