package fred.sub;

import fred.event.Schedulable;

/**
 * An interface for adapting functional reactives to the external sources based on the visitor pattern.
 *
 * Author:  Fred Deng
 */
public interface Subscribable<T> {
    void doSubscribe(Schedulable<? super T> source);
    void unsubscribe();
}
