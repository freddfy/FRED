package fred.sub;

import fred.FunctionalReactives;
import fred.event.Schedulable;
import fred.frp.FunctionVoid;

/**
 * A subscribable for bridging between different managers, i.e. one EM subscribe from events from another EM.
 *
 * Author: Fred Deng
 */
public class SubscribableEventManagerBridge<T> implements Subscribable<T>{

    private final FunctionalReactives<T> origSource;
    private FunctionalReactives<Void> subscription;

    public SubscribableEventManagerBridge(FunctionalReactives<T> origSource) {
        this.origSource = origSource;
    }

    @Override
    public void doSubscribe(final Schedulable<? super T> source) {
        if (subscription != null) {
            throw new IllegalStateException("Already subscribed");
        }

        subscription = origSource.forEach(new FunctionVoid<T>() {
            @Override
            public void apply(T next) {
                source.schedule(next);
            }
        });

        this.origSource.start(); //TODO: ideally we could decouple lifecycle with subscription
    }

    @Override
    public void unsubscribe() {
        if (subscription != null) {
            subscription.detach();
        }
    }
}
