/**
 * Group 23
 */
package mycontroller.states;

import mycontroller.pathfinders.PathFinder;
import world.Car;

/**
 * The type of state that tracks healing tiles.
 */
public class HealingState extends CoordinateTrackerStates {
    private Car car;
    /**
     * The constant MAXIMUM_HEALTH_TO_HEAL.
     */
    public static int MAXIMUM_HEALTH_TO_HEAL = 100;

    /**
     * Instantiates a new Healing state.
     *
     * @param pathFinder the path finder
     * @param car        the car
     */
    public HealingState(PathFinder pathFinder, Car car) {
        super(pathFinder);
        this.car = car;
    }



    @Override
    public boolean isFinished() {
        /**
         * This state is finished when the car has healed to the values
         * determined
         */
        return car.getHealth() == MAXIMUM_HEALTH_TO_HEAL;
    }
}