/**
 * Group 23
 */
package mycontroller.strategies;

import utilities.Coordinate;


/**
 * The interface Strategy algorithm calculates the best point to go to for the
 * car.
 */
public interface StrategyFactory {
    /**
     * The constant MINIMUM_HEALTH.
     */
    int MINIMUM_HEALTH = 50;

    /**
     * The enum Important data.
     */
    enum ImportantData {
        /**
         * Key important data.
         */
        KEY,
        /**
         * Healing important data.
         */
        HEALING,
        /**
         * Exit important data.
         */
        EXIT}

    /**
     * Decide next tile coordinate.
     *
     * @param currentCoordinate the current coordinate
     * @return the coordinate
     */
    Coordinate decideNextCoordinate(Coordinate currentCoordinate);

    /**
     * dictates whether the car should avoid traps or not
     *
     * @return true if the car should avoid traps and false otherwise
     */
    boolean avoidTrap();

    /**
     * strategy can interrupt a series of command
     *
     * @return the boolean
     */
    boolean interrupt();

    /**
     * Update important data.
     *
     * @param coordinate the coordinate
     * @param type       the type
     */
    void updateData(Coordinate coordinate, ImportantData type);
}
