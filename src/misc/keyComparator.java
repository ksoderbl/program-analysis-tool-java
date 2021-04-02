
package misc;

import java.util.*;
import java.lang.*;

public class keyComparator implements Comparator{
    
    public int compare(Object o1, Object o2){
        Integer i1 = (Integer)o1;
        Integer i2 = (Integer)o2;
        if (i1.intValue() > i2.intValue())
            return -1;
        if (i1.intValue() < i2.intValue())
            return 1;
        else return 0;
    }
}
