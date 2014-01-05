package fred.event;

import fred.graph.Connection;

/**
 * Managing event graph, propagation and life cycle.
 *
 * Author:  Fred Deng
 */
public interface EventManager extends Connection<Event>, LifeCycle{

    /**
     * Fire the next event propagation cycle from a event source, for Event internal usage.
     */
    void fireCycle(Event source);

    /**
     * Whether the event was fired in the last cycle, for Event internal usage.
     */
    boolean isFired(Event event);

    /**
     * Schedule to fire event propagation cycle.
     *
     * The async implemention is thread-safe to use in different threads.
     */
    void scheduleFire(Event source);

    /**
     * Schedule to fire a runnable.
     */
    void schedule(Runnable runnable);
}
