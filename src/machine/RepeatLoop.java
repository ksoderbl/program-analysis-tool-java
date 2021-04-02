
package machine;
import machine.Register;

public class RepeatLoop{
    long start;
    long end;
    Register counter; 

    public String toString() {
        return " RepeatLoop: "
            + " start:0x" + Long.toHexString(start)
            + " end:0x" + Long.toHexString(end)
            + " reg:" + counter
            + " regval:0x" + Long.toHexString(counter.getValue())
            + "(" + counter.getValue() + ")";
    }
    
    public RepeatLoop(Register counter, long start, long end){
        this.start = start;
        this.end = end;
        this.counter = counter;
    }
    
    public RepeatLoop(Register counter, long end){
        this.counter = counter;
        this.end = end;
    }
    
    public long getCounter(){
        return counter.getValue();
    }

    public boolean decrementCounter(){
        if (counter.getValue() == 0) return true;
        counter.setValue(counter.getValue()-1);
        return false;
    }

    public long getStartAddr(){
        return start;
    }

    public long getEndAddr(){
        return end;
    }
}
