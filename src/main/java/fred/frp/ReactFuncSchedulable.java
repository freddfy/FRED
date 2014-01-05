package fred.frp;

import com.google.common.base.Optional;
import fred.event.EventManager;
import fred.event.EventReact;
import fred.event.Schedulable;

/**
 * Helper function for event source makes it more aligned with event react.
 *
* Author:  Fred Deng
*/
public class ReactFuncSchedulable<T> implements ReactFunc<T>, Schedulable<T> {

    private final EventManager em;
    private Optional<T> nextValue = Optional.absent();
    private EventReact<T> host;

    public ReactFuncSchedulable(EventManager em) {
        this.em = em;
    }

    @Override
    public Optional<T> apply() {
        Optional<T> value = nextValue;
        nextValue = Optional.absent();
        return value;
    }

    @Override
    public void setHost(EventReact<T> host) {
        this.host = host;
        em.connect(host);
    }

    @Override
    public void schedule(T nextValue) {
        final Optional<T> nextOptional = Optional.fromNullable(nextValue);
        em.schedule(new Runnable() {
            @Override
            public void run() {
                ReactFuncSchedulable.this.nextValue = nextOptional;
                em.fireCycle(host);
            }
        });
    }
}
