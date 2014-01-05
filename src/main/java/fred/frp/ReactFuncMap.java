package fred.frp;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import fred.event.EventReact;
import fred.util.Optionals;

/**
 *
 * Author:  Fred Deng
 */
public class ReactFuncMap<F, T> extends ReactFuncIgnoreHost<T> {
    private final EventReact<F> source;
    private final Function<? super F, Optional<T>> function;

    public ReactFuncMap(EventReact<F> source, Function<? super F, Optional<T>> function) {
        this.source = source;
        this.function = function;
    }

    @Override
    public Optional<T> apply() {
        return Optionals.filterAbsent(source.value(), function);
    }
}
