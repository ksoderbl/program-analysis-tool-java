/**
 * StateSet.java
 */

package analysis;

import java.util.HashSet;
import java.util.Iterator;

import main.*;

/**
 * Set of program states
 *
 * @author Mikko Reinikainen
 * @see State
 */

public class StateSet {

    private HashSet<State> states;

    /**
     * Constructs a new state set
     *
     * @return the new state set
     */
    public StateSet() {
        states = new HashSet<State>();
    }

    /**
     * Adds a state to the state set
     *
     * @return true if the state set did not already contain the state
     */
    public boolean add(State state) {
        if (contains(state)) {
            return false;
        } else {
            boolean result = states.add(state);
            if (!result) {
                Main.warn("Tried to add a duplicate entry to state set " + this + ".\nThis should not happen!");
            }
            return result;
        }
    }

    /**
     * Removes all states from this state set
     */
    public void clear() {
        states.clear();
    }

    /**
     * Returns true if this state set contains the specified state
     * 
     * @param state the specified state
     * @return true if this state set contains the specified state
     */
    public boolean contains(State state) {
        // quick check of if contains the same State instance
        if (states.contains(state)) {
            return true;
        }

        Iterator<State> iter = iterator();
        State containedState;

        // compare each contained state with the specified state
        while (iter.hasNext()) {
            containedState = iter.next();
            if (containedState.equals(state)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return true if this state set is empty
     */
    public boolean isEmpty() {
        return states.isEmpty();
    }

    /**
     * @return an iterator over the states of this state set
     */
    public Iterator<State> iterator() {
        return states.iterator();
    }

    /**
     * Removes the specified state from this state set if it is present.
     *
     * @param state state to be removed from this state set
     * @return true if this state set contained the specified state
     */
    public boolean remove(State state) {
        // quick check of if contains the same State instance
        if (states.contains(state)) {
            return actualRemove(state);
        }

        Iterator<State> iter = iterator();
        State containedState;

        // compare each contained state with the specified state
        while (iter.hasNext()) {
            containedState = iter.next();
            if (containedState.equals(state)) {
                return actualRemove(containedState);
            }
        }

        return false;
    }

    /**
     * @return number of states in this state set
     */
    public int size() {
        return states.size();
    }

    /**
     * @return this state set as a String
     */
    public String toString() {
        String result = null;
        Iterator<State> iter = iterator();
        while (iter.hasNext()) {
            State state = iter.next();
            if (result == null) {
                result = state.toString();
            } else {
                result = result + " " + state.toString();
            }
        }
        return result;
    }

    /**
     * Remove the specified State instance from this state set. Expects
     * that the state is found in this state set.
     */
    private boolean actualRemove(State state) {
        boolean result = states.remove(state);
        if (!result) {
            Main.warn("Could not remove an existing state from state set " + this + ".\nThis should not happen!");
        }
        return result;
    }

}
