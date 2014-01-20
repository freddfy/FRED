package fred.frp;

import com.google.common.base.Optional;
import fred.event.EventCycleAware;
import fred.event.EventReact;

/**
 * An EventReact that is constant.
 */
public class EventReactConstant<T> extends EventCycleAware implements EventReact<T> {

    private final Optional<T> constant;

    public EventReactConstant(T constant) {
        this.constant = Optional.fromNullable(constant);
    }

    @Override
    public Optional<T> value() {
        return constant;
    }

    @Override
    protected boolean fired() {
        return false;  //cause a constant never fire
    }
}
