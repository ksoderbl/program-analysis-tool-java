package instr;

public class ProgramEntry extends Instruction
{
    public ProgramEntry(String entry) {
        super.addOperation("program_entry", null, "program_entry");
        Operation op = super.getOperation(0);
        op.setCall(false);
        op.setBranchTarget(entry);
        super.setAddr(new Long(-8));
    }

    public boolean isProgramEntry() {
        return true;
    }

}
