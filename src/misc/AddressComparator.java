
package misc;

import java.util.*;
public class AddressComparator implements Comparator<Long> {
    
    public int compare(Long i1, Long i2) {
        if (i1.longValue() < i2.longValue())
            return -1;
        if (i1.longValue() > i2.longValue())
            return 1;
        else return 0;
    }
}
