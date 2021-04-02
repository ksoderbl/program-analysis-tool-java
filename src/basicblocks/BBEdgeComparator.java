
package basicblocks;

import java.util.*;
import java.lang.*;

public class BBEdgeComparator implements Comparator{
    
    public int compare(Object o1, Object o2){
        BasicBlockEdge i1 = (BasicBlockEdge)o1;
        BasicBlockEdge i2 = (BasicBlockEdge)o2;
        if (i1.getExecutions() > i2.getExecutions())
            return -1;
        if (i1.getExecutions() < i2.getExecutions())
            return 1;
        else return 0;
    }
}
