/**
 * Group 23
 */
package mycontroller;

import controller.CarController;
import mycontroller.pathfinders.BreadthFirstSearchPathFinding;
import mycontroller.pathfinders.PathFinder;
import mycontroller.strategies.KeyPriorityStrategy;
import mycontroller.strategies.StrategyFactory;
import tiles.*;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;

import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * The type My ai controller.
 */
public class MyAIController extends CarController{
	private enum Commands {
        /**
         * Forward commands.
         */
        FORWARD,
        /**
         * Reverse commands.
         */
        REVERSE,
        /**
         * Left commands.
         */
        LEFT,
        /**
         * Right commands.
         */
        RIGHT,
        /**
         * Brake commands.
         */
        BRAKE,
        /**
         * None commands.
         */
        NONE}


    private ArrayList<Coordinate> recordCoordinate = new ArrayList<>();
	private Queue<Commands> commandsQueue = new LinkedList<>();
	private Queue<Coordinate> pathQueue;
	private StrategyFactory strategy;

    private HashMap<Coordinate, MapTile> map = super.getMap();
    private PathFinder pathFinder;

    private Route route;

    /**
     * Instantiates a new My ai controller.
     *
     * @param car the car
     */
	public MyAIController(Car car) {
		super(car);
        route = new Route(map);
        pathFinder = new BreadthFirstSearchPathFinding(route);
        pathQueue = new LinkedList<>();
        strategy = new KeyPriorityStrategy(this.route, car, pathFinder);

	}

	@Override
	public void update() {
        /**
         *  Update the map based on the TRAP information given
         */
        updateMap();

        Coordinate currentCoordinate = getCurrentCoordinate();



        /**
         * Checking if collision is imminent, commands are interrupted and
         * the car brakes
         */
        checkOncomingCollision();


        if ( commandsQueue.isEmpty()){

            /**
             * Generate the next coordinate the car should go through
             */
			Coordinate destination =
                    strategy.decideNextCoordinate(currentCoordinate);


            /**
             * Generate a list of coordinates that the car has to go through
             * using certain path finding calculation
             */
            List<Coordinate> path =
                    pathFinder.findBestPath
                            (currentCoordinate, destination, getOrientation(),
                                    strategy.avoidTrap());

            /**
             * If a path is defined as unreachable, the coordinate is blocked
             * and the path is recalculated until a reachable path is found
             */
            while(path == PathFinder.UNREACHABLE){
                route.blockFromSource(destination.x, destination.y);
                destination =  strategy.decideNextCoordinate(currentCoordinate);

                path = pathFinder.findBestPath
                        (currentCoordinate, destination, getOrientation(),
                                strategy.avoidTrap());
            }

            /**
             * Coordinates queue for checkOncomingCollision
             */
            pathQueue = new LinkedList<>(path);
            pathQueue.poll();

            /**
             * Converting a list of coordinates into commands based on the car
             * condition
             */
            setCommandSequence(path);

		}


        /**
         * Based on the next command in the queue, a command is given to the car
         */
        Commands nextCommand = commandsQueue.poll();
		assert nextCommand != null;
		switch (nextCommand){
			case LEFT:
				turnLeft();
				break;
			case RIGHT:
				turnRight();
				break;
			case REVERSE:
				applyReverseAcceleration();
				break;
			case FORWARD:
				applyForwardAcceleration();
				break;
			case BRAKE:
				applyBrake();
				break;
            case NONE:
                break;
		}


	}

    /**
     * Check the next coordinate where the car will head to and it will stop
     * should it be a trap that kills or a wall
     */
    private void checkOncomingCollision(){
	    Coordinate nextPath = pathQueue.poll();
	    if (nextPath != null && route.isBlocked(nextPath.x, nextPath.y)){
	        commandsQueue = new LinkedList<>();
	        commandsQueue.add(Commands.BRAKE);
        }
    }


    /**
     * setCommandSequence takes a list of coordinates that the car has to go
     * through and turns it into a list of commands the car has to do to reach
     * that point
     *
     * @param coordinates the list of coordinates
     */
    private void setCommandSequence(List<Coordinate> coordinates){
        /**
         * Get current coordinates and current orientation
         */
		Coordinate currentCoordinate = getCurrentCoordinate();
		WorldSpatial.Direction currentOrientation = getOrientation();

        /**
         * Accepted values of direction
         */
		final int[] RIGHT_DIRECTION = {1,0};
		final int[] LEFT_DIRECTION = {-1,0};
		final int[] UP_DIRECTION = {0,1};
		final int[] DOWN_DIRECTION = {0,-1};

        /**
         * Dictates to whether the car is facing forward or not
         */
		boolean faceForward = false;

        /**
         * Dictates whether acceleration is applied, when acceleration is
         * already applied, no more acceleration is needed. This is to
         * prevent crashing into the walls.
         */
        boolean accelerationApplied = false;

        /**
         * Process commands based on each coordinates
         */
		for(Coordinate coordinate: coordinates){

            /**
             * Changing the next coordinate and the current coordinate into
             * acceptable values of direaction
             */
			int deltaX = coordinate.x - currentCoordinate.x;
			int deltaY = coordinate.y - currentCoordinate.y;
			int[] direction = {deltaX, deltaY};
//            System.out.println(Arrays.toString(direction));
            /**
             * Based on the car orientation and where the car is supposed to
             * go, it gives the correct command and the new orientation should
             * the car change orientation
             */
			switch(currentOrientation){
				case EAST:

					if (Arrays.equals(direction, RIGHT_DIRECTION) &&
                            !accelerationApplied){

						commandsQueue.add(Commands.FORWARD);
						accelerationApplied = true;
						faceForward = true;

					} else if (Arrays.equals(direction, LEFT_DIRECTION) &&
                            !accelerationApplied){

                        accelerationApplied = true;
						commandsQueue.add(Commands.REVERSE);

					}  else if (Arrays.equals(direction, DOWN_DIRECTION)){

						commandsQueue.add(Commands.RIGHT);
						if (faceForward){
							currentOrientation = WorldSpatial.Direction.SOUTH;
						} else {
							currentOrientation = WorldSpatial.Direction.NORTH;
						}

					} else if (Arrays.equals(direction, UP_DIRECTION)){

                        commandsQueue.add(Commands.LEFT);
                        if (faceForward){
                            currentOrientation = WorldSpatial.Direction.NORTH;
                        } else {
                            currentOrientation = WorldSpatial.Direction.SOUTH;
                        }

                    } else if (accelerationApplied){
                        commandsQueue.add(Commands.NONE);
                    }
                    break;
                case WEST:
                    if (Arrays.equals(direction, RIGHT_DIRECTION) &&
                            !accelerationApplied){

                        accelerationApplied = true;
                        commandsQueue.add(Commands.REVERSE);

                    } else if (Arrays.equals(direction, LEFT_DIRECTION) &&
                            !accelerationApplied){

                        commandsQueue.add(Commands.FORWARD);
                        accelerationApplied = true;
                        faceForward = true;

                    } else if (Arrays.equals(direction, UP_DIRECTION)){

                        commandsQueue.add(Commands.RIGHT);
                        if (faceForward){
                            currentOrientation = WorldSpatial.Direction.NORTH;
                        } else {
                            currentOrientation = WorldSpatial.Direction.SOUTH;
                        }

                    } else if (Arrays.equals(direction, DOWN_DIRECTION)){

                        commandsQueue.add(Commands.LEFT);
                        if (faceForward){
                            currentOrientation = WorldSpatial.Direction.SOUTH;
                        } else {
                            currentOrientation = WorldSpatial.Direction.NORTH;
                        }

                    } else if (accelerationApplied){
                        commandsQueue.add(Commands.NONE);
                    }
                    break;
                case NORTH:
                    if (Arrays.equals(direction, RIGHT_DIRECTION)){

                        commandsQueue.add(Commands.RIGHT);
                        if (faceForward){
                            currentOrientation = WorldSpatial.Direction.EAST;
                        } else {
                            currentOrientation = WorldSpatial.Direction.WEST;
                        }

                    } else if (Arrays.equals(direction, LEFT_DIRECTION)){

                        commandsQueue.add(Commands.LEFT);
                        if (faceForward){
                            currentOrientation = WorldSpatial.Direction.WEST;
                        } else {
                            currentOrientation = WorldSpatial.Direction.EAST;
                        }

                    } else if (Arrays.equals(direction, UP_DIRECTION) &&
                            !accelerationApplied){

                        accelerationApplied = true;
                        commandsQueue.add(Commands.FORWARD);
                        faceForward = true;

                    } else if (Arrays.equals(direction, DOWN_DIRECTION) &&
                            !accelerationApplied){

                        accelerationApplied = true;
                        commandsQueue.add(Commands.REVERSE);

                    } else if (accelerationApplied){
                        commandsQueue.add(Commands.NONE);
                    }
                    break;
                case SOUTH:
                    if (Arrays.equals(direction, RIGHT_DIRECTION)){

                        commandsQueue.add(Commands.LEFT);
                        if (faceForward){
                            currentOrientation = WorldSpatial.Direction.EAST;
                        } else {
                            currentOrientation = WorldSpatial.Direction.WEST;
                        }

                    } else if (Arrays.equals(direction, LEFT_DIRECTION)){

                        commandsQueue.add(Commands.RIGHT);
                        if (faceForward){
                            currentOrientation = WorldSpatial.Direction.WEST;
                        } else {
                            currentOrientation = WorldSpatial.Direction.EAST;
                        }

                    } else if (Arrays.equals(direction, UP_DIRECTION) &&
                            !accelerationApplied){

                        accelerationApplied = true;
                        commandsQueue.add(Commands.REVERSE);

                    } else if (Arrays.equals(direction, DOWN_DIRECTION) &&
                            !accelerationApplied){

                        accelerationApplied = true;
                        commandsQueue.add(Commands.FORWARD);
                        faceForward = true;

                    } else if (accelerationApplied){
                        commandsQueue.add(Commands.NONE);
                    }
                    break;
            }

            currentCoordinate = coordinate;
        }

        /**
         * Braking everytime
         */
        commandsQueue.add(Commands.BRAKE);
    }

    /**
     * updateMap updates the traps found in the car's view and include these
     * information in the map data and Route's gridMap
     */
    private void updateMap() {

        /**
         * Getting and iterating through the tiles that the car has viewed
         */
        HashMap<Coordinate, MapTile> currentView = getView();
        MapTile newTile, currentTile;
        for(Coordinate coord : currentView.keySet()) {
            newTile = currentView.get(coord);

            /**
             * Updating the Route.gridMap based on the types of tile
             */
            if(newTile.isType(MapTile.Type.TRAP) && newTile instanceof MudTrap){

                /**
                 * MudTraps are blocked
                 */
                route.blockCoordinate(coord.x, coord.y);
            } else if (newTile.isType(MapTile.Type.TRAP) &&
                    (newTile instanceof LavaTrap ||
                            newTile instanceof GrassTrap)){

                /**
                 * Lava trap and grass traps are to be avoided unless necessary
                 */
                route.setToAvoid(coord.x, coord.y);
            } else {
                /**
                 * Otherwise update the coordinates as explored
                 */
                route.updateMap(coord);
            }


            /**
             * Based on the trap tiles, the important data that strategy
             * needs are updated
             */
            if(newTile.isType(MapTile.Type.TRAP)
                    && newTile instanceof LavaTrap
                    && ((LavaTrap) newTile).getKey() > 0){
                strategy.updateData(coord, StrategyFactory.ImportantData.KEY);
            } else if (newTile.isType(MapTile.Type.TRAP) &&
                    newTile instanceof HealthTrap){
                strategy.updateData
                        (coord, StrategyFactory.ImportantData.HEALING);
            } else if (newTile.isType(MapTile.Type.FINISH)){
                strategy.updateData(coord, StrategyFactory.ImportantData.EXIT);
            }

            /**
             * Updating the maps
             */
            Coordinate tmp = new Coordinate(coord.x, coord.y);
            currentTile = map.get(tmp);
            if(currentTile != null && newTile.getType() != currentTile.getType()
                    & !recordCoordinate.contains(coord)) {
                map.put(tmp, newTile);
                recordCoordinate.add(coord);
            }
        }
    }

    /**
     * Converting position from string into a coordinate
     *
     * @return the converted string into coordinate
     */
	private Coordinate getCurrentCoordinate() {
		return new Coordinate(getPosition());
	}

}
