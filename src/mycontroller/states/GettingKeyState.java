/**
 * Group 23
 */
package mycontroller.states;

import mycontroller.pathfinders.PathFinder;
import utilities.Coordinate;
import world.WorldSpatial;


/**
 * The type of state that tracks keys.
 */
public class GettingKeyState extends CoordinateTrackerStates {

    /**
     * Instantiates a new Getting key state.
     *
     * @param pathFinder the path finder
     */
    public GettingKeyState(PathFinder pathFinder) {
        super(pathFinder);
    }

    @Override
    public boolean isFinished() {
        return !isCoordinateExist();
    }


    @Override
    public Coordinate getCoordinate(Coordinate currentCoordinate,
                                    WorldSpatial.Direction orientation) {

        /**
         * When a key coordinate is needed, a nearest coordinate is outputted
         * and no longer to be tracked
         */
        Coordinate nearestCoordinate = super.getCoordinate(currentCoordinate,
                orientation);
        removeCoordinate(nearestCoordinate);
        return nearestCoordinate;
    }
}