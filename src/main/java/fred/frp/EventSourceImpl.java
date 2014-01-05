package fred.frp;

import com.google.common.base.Optional;
import fred.event.EventManager;
import fred.event.EventSource;
import fred.event.Schedulable;
import fred.sub.Subscribable;

/**
 * Default source implementation which allow scheduling new value and fire a event propagation cycle.
 *
 * Author:  Fred Deng
 */
public class EventSourceImpl<T> extends EventReactImpl<T> implements EventSource<T> {

    private final Schedulable<T> schedulable;
    private final Subscribable<? extends T> subscribable;

    public EventSourceImpl(EventManager em, Subscribable<? extends T> subscribable) {
        this(em, null, subscribable);
    }

    public EventSourceImpl(EventManager em, T initValue, Subscribable<? extends T> subscribable) {
        this(new ReactFuncSchedulable<T>(em), Optional.fromNullable(initValue), subscribable);
    }

    protected EventSourceImpl(ReactFuncSchedulable<T> reactFunc, Optional<T> initValue, Subscribable<? extends T> subscribable) {
        super(reactFunc, initValue);
        this.schedulable = reactFunc;
        this.subscribable = subscribable;
    }

    @Override
    public void start() {
        subscribable.doSubscribe(schedulable);
    }

    @Override
    public void shutdown() {
        subscribable.unsubscribe();
    }
}
