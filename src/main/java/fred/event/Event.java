package fred.event;

/**
 * Base interface for event propagation cycles.
 *
 * Author:  Fred Deng
 */
public interface Event {
    /**
     *
     * @param cycle the current cycle
     * @return whether this event is fired successfully in this cycle, its dependents should be tryFire if it is.
     */
    boolean tryFire(long cycle);

    /**
     *
     * @param lastCycle the last cycle
     * @return whether this event was fired in the latest cycle
     */
    boolean isLastFired(long lastCycle);
}
