
package misc;

import java.util.*;
import java.lang.*;

public class keyComparator implements Comparator<Integer> {
    
    public int compare(Integer i1, Integer i2){
        if (i1.intValue() > i2.intValue())
            return -1;
        if (i1.intValue() < i2.intValue())
            return 1;
        else return 0;
    }
}
