

/** 
 * @author Peter Majorin
 *
 * A variable is an instance of a simple type of 
 * gcc; like int, char.
 */

package stabs;

public class Variable{

    private String name;
    private Type type;

    public Variable(Type type, String name){
        this.type = type;
        this.name = name;
    }
    
    public String getName(){
        return name;
    }

    public Type getType(){
        return type;
    }
}
