/**
 * Group 23
 */
package mycontroller.states;

import mycontroller.pathfinders.PathFinder;
import utilities.Coordinate;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The type of states that tracks coordinates.
 */
public abstract class CoordinateTrackerStates implements State{

    /**
     * The pathfinding strategy used
     */
    private PathFinder pathFinder;

    /**
     * The coordinates to track
     */
    private Set<Coordinate> importantCoordinates;

    /**
     * Recording added coordinates
     */
    private Set<Coordinate> coordinatesHistory;

    /**
     * Instantiates a new Coordinate tracker states.
     *
     * @param pathFinder the path finder
     */
    public CoordinateTrackerStates(PathFinder pathFinder) {
        this.pathFinder = pathFinder;
        this.importantCoordinates = new HashSet<>();
        this.coordinatesHistory = new HashSet<>();
    }


    @Override
    public Coordinate getCoordinate(Coordinate currentCoordinate,
                                    WorldSpatial.Direction orientation) {

        List<Coordinate> unreachableCoordinates = new ArrayList<>();

        /**
         * Finding the closes coordinate from the car
         */
        Coordinate nearestCoordinate =
                pathFinder.findNearestCoordinate(
                        new ArrayList<>(importantCoordinates),
                        currentCoordinate, orientation,
                        unreachableCoordinates);

        /**
         * When unreachable coordinates are found, they are to be deleted
         */
        for (Coordinate coordinate: unreachableCoordinates)
            importantCoordinates.remove(coordinate);

        return nearestCoordinate;
    }

    @Override
    public boolean offerImportantCoordinate(Coordinate coordinate) {

        /**
         * If a coordinate has been added before, it will not get added again
         */
        if(!coordinatesHistory.contains(coordinate)){
            coordinatesHistory.add(coordinate);
            importantCoordinates.add(coordinate);
            return true;
        }
        return false;
    }

    @Override
    public abstract boolean isFinished();

    @Override
    public boolean isCoordinateExist() {
        return !importantCoordinates.isEmpty();
    }

    @Override
    public void removeCoordinate(Coordinate coordinate) {

        /**
         * Remove certain coordinates
         */
        if(!importantCoordinates.contains(coordinate)) return;
        importantCoordinates.remove(coordinate);
    }

}
