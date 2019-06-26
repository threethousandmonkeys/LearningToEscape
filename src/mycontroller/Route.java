/**
 * Group 23
 */
package mycontroller;
import java.util.ArrayList;
import java.util.HashMap;

import mycontroller.pathfinders.PathFinder;
import tiles.MapTile;
import utilities.Coordinate;
import world.World;

/**
 * The class Route tracks the status of each coordinate in the map.
 */
public class Route {
    /**
     * gridMap will dictates whether a coordinate is blocked, to avoid unless
     * necessary, or if it has been explored
     */
    private int[][] gridMap = new int[World.MAP_HEIGHT][World.MAP_WIDTH];
    private HashMap<Coordinate,MapTile> map;
    /**
     * The constant BLOCKED.
     */
    public static final int BLOCKED = -1;
    /**
     * The constant TRAP_OR_ROAD.
     */
    public static final int TRAP_OR_ROAD = 0;
    /**
     * The constant TO_AVOID.
     */
    public static final int TO_AVOID = -2;


    /**
     * Instantiates a new Route.
     *
     * @param map      the map
     */
    public Route(HashMap<Coordinate, MapTile> map) {
        this.map = map;
        buildMap();
    }

    /**
     * Builds the gridMap based on the map.
     */
    public void buildMap(){
        for(Coordinate coord: map.keySet()){
            MapTile currLoc = map.get(coord);

            /**
             * if current location is BLOCKED, then mark this grid as BLOCKED
             */
            if(currLoc.isType(MapTile.Type.WALL)){
                gridMap[coord.y][coord.x] = BLOCKED;
            }
            /**
             * Otherwise, it is marked as passable
             */
            else{

                gridMap[coord.y][coord.x] = TRAP_OR_ROAD;
            }
        }
    }

    /**
     * Check whether a coordinate is in the map or not
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return returns true when coordinate is in the map and false otherwise
     */
    public static boolean isWithinMap(int x, int y){

        return !(x < 0 || x >= World.MAP_WIDTH || y < 0
                || y >= World.MAP_HEIGHT);
    }

    /**
     * Block coordinate.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void blockCoordinate(int x, int y){
        gridMap[y][x] = Route.BLOCKED;
    }


    /**
     * Block from source takes a coordinate and keeps blocking surrounding
     * coordinates.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void blockFromSource(int x, int y){
        blockCoordinate(x, y);
        ArrayList<Coordinate> possibleCoordinate = new ArrayList<>();
        possibleCoordinate.add(new Coordinate(x, y));
        blockFromSource(possibleCoordinate);
    }

    private void blockFromSource(ArrayList<Coordinate> possibleCoordinates){
        if(possibleCoordinates.isEmpty()) return;
        ArrayList<Coordinate> nextPossibleCoordinates = new ArrayList<>();

        /**
         * Keep blocking surrounding coordinates until a wall is reached
         */
        for (Coordinate possibleCoordinate: possibleCoordinates) {
            int sourceX = possibleCoordinate.x;
            int sourceY = possibleCoordinate.y;
            for (int i = 0; i < PathFinder.NUM_OF_POSSIBLE_DIRECTION; i++) {
                int nextX = sourceX + PathFinder.DIRECTIONS_DELTA[i];
                int nextY = sourceY + PathFinder.DIRECTIONS_DELTA
                        [(i+1)%PathFinder.NUM_OF_POSSIBLE_DIRECTION];
                if(!isWithinMap(nextX, nextY) || isBlocked(nextX, nextY))
                    continue;

                blockCoordinate(nextX, nextY);
                nextPossibleCoordinates.add(new Coordinate(nextX, nextY));
            }
        }

        blockFromSource(nextPossibleCoordinates);
    }

    /**
     * Update a coordinate in the map as explored.
     *
     * @param coordinate the coordinate
     */
    public void updateMap(Coordinate coordinate){
        int x = coordinate.x;
        int y = coordinate.y;
        if(!isWithinMap(x, y) || gridMap[y][x] == BLOCKED ||
                gridMap[y][x] == TO_AVOID){
            return;
        }
        gridMap[y][x] += 1;
    }

    /**
     * Set a coordinate to avoid.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void setToAvoid(int x, int y){
        if(isWithinMap(x, y)) gridMap[y][x] = TO_AVOID;
    }

    /**
     * To avoid boolean.
     *
     * @param coordinate the coordinate
     * @return the boolean
     */
    public boolean toAvoid(Coordinate coordinate){
        return gridMap[coordinate.y][coordinate.x] == TO_AVOID;
    }

    /**
     * To check whether a coordinate is blocked or not.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true if a coordinate is blocked and false otherwise
     */
    public boolean isBlocked(int x, int y){
        return gridMap[y][x] == BLOCKED;
    }

    /**
     * Get gridMap.
     *
     * @return the gridMap
     */
    public int[][] getGridMap() {
        return gridMap;
    }


    /**
     * Prints the grid map.
     */
    public void printGridMap(){
        for (int i = World.MAP_HEIGHT-1; i >= 0; i--) {
            for (int a : gridMap[i]){
                System.out.printf("%2d ", a);
            }
            System.out.println();
        }
        System.out.println();
    }

}
