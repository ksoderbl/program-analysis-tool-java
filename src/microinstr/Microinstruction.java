
package microinstr;
import program.Program;
import machine.Machine;

public abstract class Microinstruction{

    private String name;
    private int resultOffset = 0;

    public Microinstruction(String name){
        this.name = name;
    }
    
    public String getName(){
        return name;
    }

    public void setResultOffset(int offset) {
        this.resultOffset = offset;
    }
    public final int getResultOffset() {
        return resultOffset;
    }

    /** the code of the microinstruction */
    public abstract void execute(Program program, Machine machine);

}
