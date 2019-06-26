/**
 * Group 23
 */
package mycontroller.pathfinders;

import utilities.Coordinate;
import world.WorldSpatial;

import java.util.List;

/**
 * The interface Path finder.
 */
public interface PathFinder {
    /**
     * The constant DISTANCE as distance from one coordinate to the next
     * coordinate is always 1.
     */
    int DISTANCE = 1;

    /**
     * The constant NUM_OF_POSSIBLE_DIRECTION, a car can only go to one of
     * the four directions from a coordinate.
     */
    int NUM_OF_POSSIBLE_DIRECTION = 4;

    /**
     * The constant DIRECTIONS_DELTA, used to calculate possible next
     * coordinates surrounding the source coordinate.
     */
    int[] DIRECTIONS_DELTA = new int[]{1, 0, -1, 0};

    List<Coordinate> UNREACHABLE = null;

    /**
     * Generate a sequence of points from the starting current coordinate to
     * the destination
     *
     * @param currentCoordinate the current coordinate
     * @param destination       the destination
     * @param orientation       the orientation of the car
     * @return the list
     */
    List<Coordinate> findBestPath(Coordinate currentCoordinate,
                                  Coordinate destination,
                                  WorldSpatial.Direction orientation,
                                  boolean avoidTrap);

    /**
     * Find the nearest coordinate from a list of coordinates and detects the
     * unreachable coordinates should it exists
     * @param coordinates the list of coordinates
     * @param currentCoordinate the current coordinate of the car
     * @return nearest coordinate
     */
    Coordinate findNearestCoordinate(List<Coordinate> coordinates,
                                     Coordinate currentCoordinate,
                                     WorldSpatial.Direction orientation,
                                     List<Coordinate>
                                             unreachableCoordinates);

}
