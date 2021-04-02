package instr;

public class ProgramExit extends Instruction
{
    public ProgramExit() {
        super.addOperation("program_exit", null, "program_exit");
        super.setAddr(new Long(-4));
    }

    public boolean isProgramExit() {
        return true;
    }
}
