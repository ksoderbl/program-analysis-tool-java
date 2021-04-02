/**
 * PseudoOp.java
 *
 * @date 21.3.2005 
 *
 * @author Kristian Söderblom
 */

// CHECK
// http://www.gnu.org/software/binutils/manual/gas-2.9.1/html_chapter/as_7.html

package pseudoOp;

import input.InputLineObject;
import instr.Instruction;
import program.Label;

/**
 */
public class PseudoOp implements InputLineObject {
    /**
     * Constructs a new pseudo op
     *
     * @return the new pseudo op
     */
    public PseudoOp() {    }

    // stuff needed for interface InputLineObject
    public Instruction getInstruction() {
        return null;
    }
    public Label getLabel() {
        return null;
    }
    public PseudoOp getPseudoOp() {
        return this;
    }
}
