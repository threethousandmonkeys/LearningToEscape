/**
 * Group 23
 */
package mycontroller.states;

import utilities.Coordinate;
import world.WorldSpatial;

/**
 * The interface State.
 */
public interface State {


    /**
     * Gets nearest coordinate based on the state.
     *
     * @param currentCoordinate the current coordinate
     * @param orientation       the orientation
     * @return the nearest coordinate
     */
    Coordinate getCoordinate(Coordinate currentCoordinate,
                             WorldSpatial.Direction orientation);


    /**
     * Add important coordinate to each state.
     *
     * @param coordinate the coordinate
     * @return the boolean
     */
    boolean offerImportantCoordinate(Coordinate coordinate);

    /**
     * Remove coordinate.
     *
     * @param coordinate the coordinate
     */
    void removeCoordinate(Coordinate coordinate);

    /**
     * Check if a state is finished
     *
     * @return the boolean
     */
    boolean isFinished();

    /**
     * Check if a tracked coordinate exists
     * @return true if it exists and false otherwise
     */
    boolean isCoordinateExist();
}
