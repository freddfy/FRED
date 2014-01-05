package fred.frp;

import com.google.common.base.Optional;
import fred.event.EventCycleAware;
import fred.event.EventReact;

/**
 * Default react implementation that takes a function for evaluation during event propagation cycle.
 *
 * Author:  Fred Deng
 */
public class EventReactImpl<T> extends EventCycleAware implements EventReact<T> {

    private final ReactFunc<T> reactFunc;
    private Optional<T> value;

    public EventReactImpl(ReactFunc<T> reactFunc) {
        this(reactFunc, Optional.<T>absent());
    }

    public EventReactImpl(ReactFunc<T> reactFunc, T value) {
        this(reactFunc, Optional.fromNullable(value));
    }

    protected EventReactImpl(ReactFunc<T> reactFunc, Optional<T> value) {
        this.reactFunc = reactFunc;
        this.value = value;
        this.reactFunc.setHost(this);
    }

    @Override
    protected boolean fired() {
        Optional<T> funcVal = reactFunc.apply();
        this.value = funcVal.or(value);
        return funcVal.isPresent();
    }

    @Override
    public Optional<T> value(){
        return value;
    }

}
