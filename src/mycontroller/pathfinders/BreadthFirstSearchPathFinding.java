/**
 * Group 23
 */
package mycontroller.pathfinders;

import mycontroller.Route;
import utilities.Coordinate;
import world.World;
import world.WorldSpatial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The PathFinder that uses the Breadth First Search Algorithm.
 */
public class BreadthFirstSearchPathFinding implements PathFinder{
    /**
     * Dummy values, to be put in manipulateArray so that it process the
     * distance to every coordinates
     */
    private static final Coordinate PROCESS_EVERYTHING =
            new Coordinate(-1,-1);

    /**
     * To mark a coordinate as has not been reached
     */
    private static final int NOT_REACHED = Integer.MAX_VALUE;

    /**
     * distanceArray is used to execute the algorithm
     */
    private int[][] distanceArray;

    private Route route;

    /**
     * Dictates whether to avoid the traps or not
     */
    private boolean avoidTrap;

    /**
     * Instantiates a new Breadth first search path finding.
     *
     * @param route the route
     */
    public BreadthFirstSearchPathFinding(Route route) {
        distanceArray = new int[World.MAP_HEIGHT][World.MAP_WIDTH];
        this.route = route;
    }

    @Override
    public List<Coordinate> findBestPath(Coordinate currentCoordinate,
                                         Coordinate destination,
                                         WorldSpatial.Direction orientation,
                                         boolean avoidTrap) {
        resetDistanceArray(currentCoordinate);
        this.avoidTrap = avoidTrap;
        /**
         * Based on the orientation of the car, defines where the next possible
         * coordinates. A car must move one coordinate forward or backward at
         * first, therefore there is only a maximum of two possible points
         * right after the initial coordinate.
         */
        List<Coordinate> initialPossibleCoordinates =
                calculateInitialPossibleCoordinate
                        (currentCoordinate, orientation);

        /**
         * Manipulating distArray based on avoidTrap
         */
        if(avoidTrap){
            manipulateArray(initialPossibleCoordinates, destination);

            /**
             * Should the coordinate, after processing, is found as unreachable,
             * it recalculates everything again with the exception of being
             * able to go through the trap. This happens when the only way to
             * get to the destination is through the trap.
             */
            if(!isReachable(destination)) {
                this.avoidTrap = false;
                resetDistanceArray(currentCoordinate);
                initialPossibleCoordinates = calculateInitialPossibleCoordinate
                        (currentCoordinate, orientation);
                manipulateArray(initialPossibleCoordinates, destination);
            }

        } else {

            manipulateArray(initialPossibleCoordinates, destination);
        }

        /**
         * returning the processed path
         */
        return backtrack(currentCoordinate, destination);
    }

    @Override
    public Coordinate findNearestCoordinate(List<Coordinate> coordinates,
                                            Coordinate currentCoordinate,
                                            WorldSpatial.Direction orientation,
                                            List<Coordinate>
                                                        unreachableCoordinates){

        Coordinate nearestCoordinate = null;

        /**
         * resetting distArray values
         */
        resetDistanceArray(currentCoordinate);

        /**
         * initial values
         */
        int minimumDistance = Integer.MAX_VALUE;
        List<Coordinate> initialPossibleCoordinates =
                calculateInitialPossibleCoordinate
                        (currentCoordinate, orientation);

        /**
         * Calculates distances to every coordinates from source, turning off
         * the avoid trap
         */
        avoidTrap = false;
        manipulateArray(initialPossibleCoordinates, PROCESS_EVERYTHING);
        avoidTrap = true;

        /**
         * Finding the coordinate with the smallest distance
         */
        for(Coordinate coordinate: coordinates){
            int x = coordinate.x;
            int y = coordinate.y;

            /**
             * Should it be unreachable, it is blocked and added to the
             * unreachable coordinates list
             */
            if(!isReachable(new Coordinate(x, y))) {

                /**
                 * Marking the coordinate and blocking it so that it is not
                 * calculated again
                 */
                unreachableCoordinates.add(coordinate);
                route.blockFromSource(x, y);
            } else if(minimumDistance > distanceArray[y][x]){

                /**
                 * Updating the nearest coordinates
                 */
                minimumDistance = distanceArray[y][x];
                nearestCoordinate = coordinate;
            }
        }

        return nearestCoordinate;
    }

    /**
     * To check whether a coordinate is reachable, can only be used after
     * manipulateArray.
     *
     * @param destination the coordinate to check
     * @return true if a coordinate is reachable and false otherwise
     */
    private boolean isReachable(Coordinate destination) {

        /**
         * If after manipulationArray, the coordinate's distance value is not
         * updated, then it is unreachable
         */
        return distanceArray[destination.y][destination.x] != NOT_REACHED;
    }

    /**
     * Resetting distance array values
     * @param currentCoordinate the current coordinate of the car
     */
    private void resetDistanceArray(Coordinate currentCoordinate){
        /**
         * Assigning every coordinate as very far except the starting point
         */
        for (int i = 0; i < World.MAP_HEIGHT; i++) {
            for (int j = 0; j < World.MAP_WIDTH; j++) {
                distanceArray[i][j] = NOT_REACHED;
            }
        }

        distanceArray[currentCoordinate.y][currentCoordinate.x] = 0;
    }

    /**
     * Based on the current coordinate and the car orientation, the method
     * dictates the first coordinates the car can reach and therefore which
     * values to update in distArray.
     * @param currentCoordinate the current coordinate of the car
     * @param orientation the orientation of the car
     * @return a list of initial possible coordinates
     */
    private List<Coordinate> calculateInitialPossibleCoordinate
            (Coordinate currentCoordinate,  WorldSpatial.Direction orientation){
        ArrayList<Coordinate> initialPossibleCoordinates = new ArrayList<>();

        /**
         * Based on the orientation, the first coordinates a car can reach
         * are either the one in front of it, the one behind it, or both.
         */
        if (orientation == WorldSpatial.Direction.EAST ||
                orientation == WorldSpatial.Direction.WEST){
            int possibleX1 = currentCoordinate.x + 1;
            int possibleX2 = currentCoordinate.x - 1;
            int possibleY = currentCoordinate.y;

            /**
             * Should it be a valid coordinate that a car can pass through it
             * is added to the possible coordinates
             */
            if(Route.isWithinMap(possibleX1, possibleY) &&
                    !route.isBlocked(possibleX1, possibleY) &&
                    (!avoidTrap || !route.toAvoid(new Coordinate
                            (possibleX1, possibleY)))) {

                distanceArray[possibleY][possibleX1] = DISTANCE;
                initialPossibleCoordinates.add(
                        new Coordinate(possibleX1, possibleY));
            }

            if(Route.isWithinMap(possibleX2, possibleY) &&
                    !route.isBlocked(possibleX2, possibleY) &&
                    (!avoidTrap || !route.toAvoid(new Coordinate
                            (possibleX2, possibleY)))) {

                distanceArray[possibleY][possibleX2] = DISTANCE;
                initialPossibleCoordinates.add(
                        new Coordinate(possibleX2, possibleY));
            }

        } else if (orientation == WorldSpatial.Direction.SOUTH ||
                orientation == WorldSpatial.Direction.NORTH){
            int possibleY1 = currentCoordinate.y + 1;
            int possibleY2 = currentCoordinate.y - 1;
            int possibleX = currentCoordinate.x;

            if(Route.isWithinMap(possibleX, possibleY1) &&
                    !route.isBlocked(possibleX, possibleY1) &&
                    (!avoidTrap || !route.toAvoid(new Coordinate
                            (possibleX, possibleY1)))) {

                distanceArray[possibleY1][possibleX] = DISTANCE;
                initialPossibleCoordinates.add(
                        new Coordinate(possibleX, possibleY1));
            }

            if(Route.isWithinMap(possibleX, possibleY2) &&
                    !route.isBlocked(possibleX, possibleY2) &&
                    (!avoidTrap || !route.toAvoid(new Coordinate
                            (possibleX, possibleY2)))) {

                distanceArray[possibleY2][possibleX] = DISTANCE;
                initialPossibleCoordinates.add(
                        new Coordinate(possibleX, possibleY2));
            }

        }

        return initialPossibleCoordinates;
    }


    /**
     * Manipulating distance array to calculate the best path
     * @param possibleCoordinates possible center coordinates, its
     *                            surrounding values will be updated
     * @param destination the destination
     */
    private void manipulateArray(List<Coordinate> possibleCoordinates,
                                 Coordinate destination){
        boolean reachedDestination = false;

        /**
         * When there is no more possible coordinates to update, it finishes
         */
        if(possibleCoordinates.isEmpty()) return;

        /**
         * Gathering next coordinates as the centre point for its surrounding
         * values to be updated
         */
        ArrayList<Coordinate> nextPossibleCoordinates = new ArrayList<>();

        /**
         * iterate through coordinates and update their surrounding coordinates
         */
        for (Coordinate coordinate: possibleCoordinates){
            /**
             * Convert the array into index
             */
            int sourceX = coordinate.x;
            int sourceY = coordinate.y;

            /**
             * Update the surrounding values should it fulfill the conditions
             */
            for (int i = 0; i < NUM_OF_POSSIBLE_DIRECTION; i++) {

                /**
                 * One of the surrounding coordinates to update
                 */
                int nextX = sourceX + DIRECTIONS_DELTA[i];
                int nextY = sourceY +
                        DIRECTIONS_DELTA[(i+1)%NUM_OF_POSSIBLE_DIRECTION];

                /**
                 * Ignoring invalid values
                 */
                if(!Route.isWithinMap(nextX, nextY)){

                    continue;
                }

                /**
                 * Update the distance values should it be faster, not a
                 * wall and if it must avoid a trap, it is not a trap
                 */

                if(!route.isBlocked(nextX, nextY) &&
                        distanceArray[sourceY][sourceX] + DISTANCE <
                                distanceArray[nextY][nextX] &&
                        (!avoidTrap || !route.toAvoid(new Coordinate(nextX
                                , nextY)))){

                    distanceArray[nextY][nextX] =
                            distanceArray[sourceY][sourceX] + DISTANCE;
                    nextPossibleCoordinates.add(new Coordinate(nextX, nextY));

                }
            }
//            printDistArray();
            /**
             * Reaching a destination means that the algorithm should stop
             */
            if(sourceX == destination.x && sourceY == destination.y){
                reachedDestination = true;
                break;
            }
        }
//        printDistArray();
//        System.out.println(nextPossibleCoordinates.size());
        /**
         * Recursively update the distance of every coordinate from the source
         * until it reaches the destination or if there is no more values to
         * update
         */
        if (!nextPossibleCoordinates.isEmpty() && !reachedDestination){
            manipulateArray(nextPossibleCoordinates, destination);
        }
    }

    /**
     * Converting the distance array into a list of coordinates which
     * represents a path
     *
     * @param startingCoordinate the source coordinate
     * @param destination the destination
     * @return
     */
    private List<Coordinate> backtrack(Coordinate startingCoordinate,
                                       Coordinate destination){
        /**
         * Start to track the path from the destination
         */
        int currentX = destination.x;
        int currentY = destination.y;

        List<Coordinate> path = new ArrayList<>();

        /**
         * adding the destination to the path
         */
        path.add(destination);

        /**
         * Backtracking from the destination, keep finding the path until the
         * source coordinate is reached
         */
        while(currentX != startingCoordinate.x ||
                currentY != startingCoordinate.y){
            /**
             * Checking the surroundings
             */
            for (int i = 0; i < NUM_OF_POSSIBLE_DIRECTION; i++) {

                /**
                 * One of the surroundings
                 */
                int nextX = currentX + DIRECTIONS_DELTA[i];
                int nextY = currentY +
                        DIRECTIONS_DELTA[(i+1)%NUM_OF_POSSIBLE_DIRECTION];

                /**
                 * Ignoring invalid values
                 */
                if(nextX < 0 || nextX >= World.MAP_WIDTH || nextY < 0
                        || nextY >= World.MAP_HEIGHT){
                    continue;
                }
                /**
                 * Should the value of the current coordinate be higher than
                 * the neighouring values, the path is added. As the
                 * difference of values is, at maximum, to be 1, any path
                 * chosen will be fine.
                 */
                if(distanceArray[currentY][currentX] -
                        distanceArray[nextY][nextX] == DISTANCE){
                    path.add(new Coordinate(nextX, nextY));
                    /**
                     * Change the currently processed coordinate
                     */
                    currentX = nextX;
                    currentY = nextY;

                    break;
                }
            }

        }

        /**
         * as it was backtracked, the list has to be reversed
         */
        Collections.reverse(path);
        return path;
    }

    private void printDistArray(){
        for (int i = World.MAP_HEIGHT - 1; i >= 0; i--) {
            int[] x = distanceArray[i];
            for (int y :
                    x) {
                if (y == Integer.MAX_VALUE){
                    System.out.print("-- ");
                } else{
                    System.out.printf("%2d ", y);
                }
            }
            System.out.printf("\n");
        }
        System.out.printf("\n");
    }
}
