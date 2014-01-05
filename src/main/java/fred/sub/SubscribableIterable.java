package fred.sub;

import fred.event.Schedulable;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Author:  Fred Deng
 */
public class SubscribableIterable<T> implements Subscribable<T> {

    private final Iterable<T> iterable;
    private volatile boolean isSubscribed = false;

    public SubscribableIterable(T... tArray) {
        this(Arrays.asList(tArray));
    }

    public SubscribableIterable(Iterable<T> iterable) {
        this.iterable = iterable;
    }

    @Override
    public void doSubscribe(Schedulable<? super T> source) {
        isSubscribed = true;

        Iterator<T> iter = iterable.iterator();
        while (isSubscribed && iter.hasNext()) {
            source.schedule(iter.next());
        }
    }

    @Override
    public void unsubscribe() {
        isSubscribed = false;
    }
}
