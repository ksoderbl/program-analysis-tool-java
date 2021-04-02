/**
 * EmitC55xInstruction.java
 * 
 * @author Kristian Söderblom
 */
package c55x.instr;

import instr.*;
import emit.EmitInstruction;
import main.*;

import java.io.PrintStream;
import java.util.ListIterator;
import java.util.List;

/**
 * This class is responsible for emitting a single instruction.
 * It takes in an <code>Instruction</code> object of program's
 * intermediate representation and emits it formatted according to
 * C55x assembly instruction set syntax.
 */
public class EmitC55xInstruction implements EmitInstruction {
    /**
     * Constructor for EmitC55xInstruction class.
     */
    public EmitC55xInstruction() {
        // ... ?
    }

    /**
     * Function for emitting the instruction.
     *
     * @return Instruction as formatted string to emit
     */
    public String emit(Instruction instruction) {
        return instruction.emit();
    }
}
