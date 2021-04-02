
package program;

import input.Input;
import input.InputLineObject;
import instr.Instruction;
import pseudoOp.PseudoOp;

public class Label implements InputLineObject
{
    private String name;

    /** the input file */
    private Input input = null;

    /** line number */
    private int line = -1;

    /** address of label */
    private Long addr;


    /**
     * Constructs a new label
     */
    public Label(String name, Input input) {
        this.name = name;
        this.input = input;
        addr = new Long(-1);
    }

    /**
     * Constructs a new (global) label
     */
    public Label(String name) {
        this.name = name;
        this.input = null;
        addr = new Long(-1);
    }




    /**
     * @param input input of the instruction
     */
    public void setInput(Input input) {
        this.input = input;
    }

    /**
     * @param line line in the input file
     */
    public void setLine(int line) {
        this.line = line;
    }


    public String toString() {
        return name;
    }

    /**
     * @return input of the symbol
     */
    public Input getInput() {
        return input;
    }

    /**
     * @return name of the symbol
     */
    public String getName() {
        return name;
    }



    /**
     * @return address of the label
     */
    public Long getAddr() {
        return addr;
    }

    /**
     * @param addr address of the label
     */
    public void setAddr(Long addr) {
        this.addr = addr;
    }

    public void setAddr(String addr) {
        this.addr = Long.valueOf(addr, 16); // assume hex
    }


    // stuff needed for interface InputLineObject
    public Instruction getInstruction() {
        return null;
    }
    public Label getLabel() {
        return this;
    }
    public PseudoOp getPseudoOp() {
        return null;
    }

}
