package fred.event;

/**
 *
 * Author:  Fred Deng
 */
public interface Schedulable<T> {
    void schedule(T nextValue);
}
