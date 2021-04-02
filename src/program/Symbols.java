/**
 * Symbols.java
 */

package program;

import cfg.CFGNode;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Symbol table.
 *
 * @author Mikko Reinikainen
 */

public class Symbols extends HashMap {

    /**
     * @return contents of the symbol table as a String
     */
    public String toString() {
        String result = "(" + size() + " elements):\n";

        Iterator iterator = keySet().iterator();

        while (iterator.hasNext()) {
            Object o = iterator.next();
            Object key = o;
            
            if (o instanceof Integer)
                key = o.toString();

            Object value = get(key);
            if (value instanceof CFGNode) {
                result = result + key + " = node "
                    + ((CFGNode) value).getName() + "\n";
            }
            else if (value instanceof Long) {
                result = result + key + " = 0x"
                    + Long.toHexString( ((Long)value).longValue() ) + "\n";
            }
            else {
                result = result + key + " = " + value + "\n";
            }
        }

        return result;
    }
}
