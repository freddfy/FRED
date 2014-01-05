package fred.event;

/**
 * Keep track of cycles in event propagation.
 *
 * Author:  Fred Deng
 */
public abstract class EventCycleAware implements Event {
    private long lastFiredCycle;

    @Override
    public boolean tryFire(long cycle) {
        if (lastFiredCycle < cycle && fired()) {
            lastFiredCycle = cycle;
            return true;
        }
        return false;
    }

    abstract protected boolean fired();

    @Override
    public boolean isLastFired(long lastCycle) {
        return lastFiredCycle == lastCycle;
    }
}
