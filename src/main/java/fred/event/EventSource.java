package fred.event;

/**
 * Source which could schedule a new value and fire a new propagation cycle.
 *
 * Author:  Fred Deng
 */
public interface EventSource<T> extends EventReact<T>, LifeCycle {

}
