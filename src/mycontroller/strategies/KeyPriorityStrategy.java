/**
 * Group 23
 */
package mycontroller.strategies;

import mycontroller.Route;
import mycontroller.pathfinders.PathFinder;
import mycontroller.states.*;
import utilities.Coordinate;
import world.Car;

public class KeyPriorityStrategy implements StrategyFactory {

    private boolean avoidTrap = false;
    private boolean healCommences = false;
    private boolean interrupt = false;
    private Route route;
    private State explore, heal, getKey, exit;
    private Car car;

    public KeyPriorityStrategy(Route route, Car car, PathFinder pathFinder) {
        this.route = route;
        this.car = car;
        this.explore = new ExplorationState(this.route);
        this.heal = new HealingState(pathFinder, car);
        this.getKey = new GettingKeyState(pathFinder);
        this.exit = new ExitingState(pathFinder);
    }

    @Override
    public Coordinate decideNextCoordinate(Coordinate currentCoordinate) {

        /**
         * Based on the strategy, it determines the state a car should be in
         */
        State currentState = null;
        avoidTrap = false;

        /**
         * Keep healing until heal state is finished
         */
        if(healCommences){
            currentState = heal;
            if(heal.isFinished()){
              healCommences = false;
          }
        } else if(!healCommences &&
                car.getHealth() <= MINIMUM_HEALTH && heal.isCoordinateExist()){
            /**
             * Heal when car is low on health and a heal tile exists
             */
            currentState = heal;
            healCommences = true;
        } else if(car.getKeys().size() == car.numKeys &&
                exit.isCoordinateExist()){
            /**
             * Exit when all the keys are found and an exit tile exists
             */
            currentState = exit;
        }  else if ((!heal.isCoordinateExist() ||
                car.getHealth() > MINIMUM_HEALTH) &&
                getKey.isCoordinateExist()){
            /**
             * Only get key when the car's health is above certain threshold
             */
            currentState = getKey;
        } else if (!healCommences){
            /**
             * If it is not healing, it must explore
             */
            avoidTrap = true;
            currentState = explore;
        }

        /**
         * Based on the states, the next coordinate is determined
         */
        Coordinate nextCoordinate =
                currentState.getCoordinate(currentCoordinate,
                        car.getOrientation());

        /**
         * If there is no nextCoordinate, the process is repeated
         */
        if(nextCoordinate == null) {
            return decideNextCoordinate(currentCoordinate);
        }

        return nextCoordinate;
    }

    @Override
    public boolean avoidTrap() {
        return avoidTrap;
    }

    @Override
    public void updateData(Coordinate coordinate, ImportantData type) {

        /**
         * Adding the data to certain states based on the type
         */
        State trackerStates = null;
        switch (type){
            case KEY:
                trackerStates = getKey;
                break;
            case HEALING:
                trackerStates = heal;
                break;
            case EXIT:
                trackerStates = exit;
                break;
        }
        assert trackerStates != null;
        boolean accepted = trackerStates.offerImportantCoordinate(coordinate);

        interrupt = accepted && type == ImportantData.KEY;
    }

    @Override
    public boolean interrupt(){
        return interrupt;
    }
}
