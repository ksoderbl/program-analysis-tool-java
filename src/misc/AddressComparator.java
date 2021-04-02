
package misc;

import java.util.*;
import java.lang.*;

public class AddressComparator implements Comparator{
    
    public int compare(Object o1, Object o2){
        Long i1 = (Long)o1;
        Long i2 = (Long)o2;
        if (i1.longValue() < i2.longValue())
            return -1;
        if (i1.longValue() > i2.longValue())
            return 1;
        else return 0;
    }
}
