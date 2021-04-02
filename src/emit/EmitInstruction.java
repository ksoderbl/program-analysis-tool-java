/**
 * EmitInstruction.java
 * 
 * Initial version
 *
 * @date 13.2.2005
 *
 * @author Ilari L.
 */
package emit;

import instr.*;

/**
 * Interface for emitting a single instruction.
 * Class that implements this interface, takes in 
 * an <code>Instruction</code> object of program's
 * intermediate representation and emits it formatted 
 * according to target language syntax.
 */
public interface EmitInstruction {
    
    /**
     * Function for emitting the instruction.
     *
     * @return Instruction as formatted string to emit
     */
    public String emit(Instruction instruction);

}
