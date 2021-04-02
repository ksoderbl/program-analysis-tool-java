/**
 * State.java
 */

package analysis;

/**
 * State of the program at one point.
 *
 * @author Mikko Reinikainen
 * @see StateSet
 */

public interface State {
    /**
     * Compares if two states are equal
     *
     * @return true if the two states are equal
     */
    public abstract boolean equals(State state);

    /**
     * Returns the state represented as a String
     * 
     * @return the state as a string
     */
    public abstract String toString();
}
