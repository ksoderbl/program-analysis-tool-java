
package basicblocks;

import java.util.*;
import java.lang.*;

public class BBEdgeComparator implements Comparator<BasicBlockEdge> {
    
    public int compare(BasicBlockEdge i1, BasicBlockEdge i2){
        if (i1.getExecutions() > i2.getExecutions())
            return -1;
        if (i1.getExecutions() < i2.getExecutions())
            return 1;
        else return 0;
    }
}
