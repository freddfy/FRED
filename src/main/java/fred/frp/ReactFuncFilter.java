package fred.frp;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import fred.event.EventReact;
import fred.util.Optionals;

/**
 *
 * Author:  Fred Deng
 */
public class ReactFuncFilter<T> extends ReactFuncIgnoreHost<T> {

    private final EventReact<T> source;
    private final Predicate<? super T> predicate;

    public ReactFuncFilter(EventReact<T> source, Predicate<? super T> predicate) {
        this.source = source;
        this.predicate = predicate;
    }

    @Override
    public Optional<T> apply() {
        Optional<T> value = source.value();
        return Optionals.filterAbsent(value, predicate) ? value : Optional.<T>absent();
    }
}
