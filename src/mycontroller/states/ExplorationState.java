/**
 * Group 23
 */
package mycontroller.states;

import mycontroller.pathfinders.PathFinder;
import mycontroller.Route;
import utilities.Coordinate;
import world.World;
import world.WorldSpatial;

import java.util.*;

/**
 * The type Exploration method for the car.
 */
public class ExplorationState implements State {
    /**
     * The constant that marks if a coordinate is unexplored
     */
    public static final int UNEXPLORED = 0;

    /**
     * direction modifier
     */
    private int modifier;
    private Route route;
    private boolean finished = false;

    /**
     * a map that marks which coordinate has or has not been explored
     */
    private int[][] explorationMap;


    /**
     * the coordinate with smallest value means it is unexplored
     */
    private int smallestValue;

    /**
     * Instantiates a new Exploration state.
     *
     * @param route the exploration map
     */
    public ExplorationState(Route route) {
        this.explorationMap = route.getGridMap();
        this.route = route;
    }

    @Override
    public Coordinate getCoordinate(Coordinate currentCoordinate,
                                    WorldSpatial.Direction orientation) {
        /**
         * initializing initial value
         */
        smallestValue = Integer.MAX_VALUE;

        /**
         * Finding the smallest value of the coordinate that has not been
         * explored
         */
        for (int[] row : explorationMap) {
            int currentValue;

            try{
                /**
                 * Finding minimum value of a row
                 */
                currentValue = Arrays.stream(row)
                                    .filter(value -> value != Route.BLOCKED &&
                                            value != Route.TO_AVOID)
                                    .min()
                                    .getAsInt();
            } catch (NoSuchElementException e){
                /**
                 * Happens when all members of row is Route.Blocked
                 */
                currentValue = Integer.MAX_VALUE;
            }

            /**
             * Updating the smallest value
             */
            if (smallestValue > currentValue){
                smallestValue = currentValue;
            }
        }
        /**
         * Initial possible coordinate which are the coordinates around the car
         */
        modifier = orientationPriorityModifier(orientation);
        Set<Coordinate> initialPossibleCoordinates = new LinkedHashSet<>();
        Set<Coordinate> addedCoordinates = new HashSet<>();
        addedCoordinates.add(currentCoordinate);
        initialPossibleCoordinates.add(currentCoordinate);


        /**
         * Find the nearest unexplored spot
         */
        return findNearestUnexploredSpot(initialPossibleCoordinates,
                addedCoordinates);
    }

    @Override
    public boolean offerImportantCoordinate(Coordinate coordinate) {
        /**
         * There is no coordinates to track
         */
        return false;
    }

    @Override
    public boolean isCoordinateExist() {
        /**
         * There's always coordinate to explore
         */
        return true;
    }

    @Override
    public void removeCoordinate(Coordinate coordinate) {
        /**
         * There is nothing to remove
         */
    }

    /**
     * A priority modifier for finding unexplored spots, the integer returned
     * adds a number that will push the starting values of the PathFinder
     * .DIRECTIONS_DELTA
     * @param orientation current orientation of the car
     * @return the number modifier
     */
    private int orientationPriorityModifier(WorldSpatial.Direction orientation){
        switch (orientation){
            case EAST:
                return 0;
            case NORTH:
                return 1;
            case WEST:
                return 2;
            case SOUTH:
                return 3;
        }
        return 0;
    }

    /**
     * Find nearest unexplored coordinate.
     *
     * @param possibleCoordinate the possible coordinate
     * @param addedCoordinates   to record coordinates that has been checked
     * @return the nearest unexplored coordinate
     */
    public Coordinate findNearestUnexploredSpot(Set<Coordinate>
                                                          possibleCoordinate,
                                                Set<Coordinate>
                                                        addedCoordinates){
        Set<Coordinate> nextPossibleCoordinate = new LinkedHashSet<>();



        /**
         * Iterate through the possible coordinates and adding the next
         * possible coordinates
         */
        for (Coordinate currentCoordinate: possibleCoordinate) {

            /**
             * Iterate through each coordinate starting from the ones nearest to
             * the car to the furthest to find the nearest coordinate with
             * the smallest value
             */
            int currentX = currentCoordinate.x;
            int currentY = currentCoordinate.y;

            for (int i = 0; i < PathFinder.NUM_OF_POSSIBLE_DIRECTION; i++) {
                    int index1 = (i + modifier) %
                            PathFinder.NUM_OF_POSSIBLE_DIRECTION;
                    int index2 =
                            (i + modifier + 1) %
                                    PathFinder.NUM_OF_POSSIBLE_DIRECTION;
                int nextX = currentX + PathFinder.DIRECTIONS_DELTA[index1];
                int nextY = currentY + PathFinder.DIRECTIONS_DELTA[index2];
                Coordinate coordinate = new Coordinate(nextX, nextY);
                /**
                 * Ignore values outside the map
                 */
                if(!Route.isWithinMap(nextX, nextY)) continue;

                /**
                 * If the nearest unexplored coordinate is found, it is
                 * returned, if not, its surroundings will be processed
                 */
                if(explorationMap[nextY][nextX] == smallestValue &&
                        !route.toAvoid(coordinate)){

                    return new Coordinate(nextX, nextY);

                } else if (!route.isBlocked(nextX, nextY) &&
                        !addedCoordinates.contains(coordinate)){
                    /**
                     * If coordinate has not been checked, it will be added
                     * into the next checked coordinate
                     */

                    nextPossibleCoordinate.add(coordinate);

                    /**
                     * Record checked coordinates
                     */
                    addedCoordinates.add(coordinate);
                }


            }
        }

        /**
         * checking the neighbouring coordinates of the next possible
         * coordinates
         */
        return findNearestUnexploredSpot(nextPossibleCoordinate,
                addedCoordinates);
    }

    @Override
    public boolean isFinished() {
        /**
         * This state is finished when there is no more unexplored coordinates
         */

        if(finished) return true;
        for (int[] row: explorationMap){
            if(Arrays.stream(row).anyMatch(x -> x == UNEXPLORED)){
                return false;
            }
        }
        finished = true;
        return true;
    }

    /**
     * Prints the exploration map
     */
    private void printExplorationMap(){
        for (int i = World.MAP_HEIGHT - 1; i >= 0; i--) {
            int[] x = explorationMap[i];
            for (int y :
                    x) {
                System.out.printf("%2d ", y);
            }
            System.out.printf("\n");
        }
        System.out.printf("\n");
    }
}
