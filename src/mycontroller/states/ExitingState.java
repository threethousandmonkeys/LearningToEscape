/**
 * Group 23
 */
package mycontroller.states;
import mycontroller.pathfinders.PathFinder;

/**
 * The type state that records exits.
 */
public class ExitingState extends CoordinateTrackerStates {
    /**
     * Instantiates a new Exiting state.
     *
     * @param pathFinder the path finder
     */
    public ExitingState(PathFinder pathFinder) {
        super(pathFinder);
    }

    @Override
    public boolean isFinished() {
        /**
         * It can not finish
         */
        return false;
    }
}