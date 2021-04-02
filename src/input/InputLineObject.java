package input;

import program.Label;
import instr.Instruction;
import pseudoOp.PseudoOp;

public interface InputLineObject
{
    public Instruction getInstruction();
    public Label getLabel();
    public PseudoOp getPseudoOp();
}
