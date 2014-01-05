package fred.frp;

import com.google.common.base.Optional;
import fred.event.EventReact;
import fred.util.Optionals;

/**
 *
 * Author:  Fred Deng
 */
public class ReactFuncReduce<T> extends ReactFuncIgnoreHost<T> {

    private final FunctionAcc<? super T, T> function;
    private final EventReact<T> source;
    private Optional<T> acc = Optional.absent();

    public ReactFuncReduce(EventReact<T> source, FunctionAcc<? super T, T> function) {
        this.function = function;
        this.source = source;
    }

    @Override
    public Optional<T> apply() {
        Optional<T> next = source.value();
        Optional<T> result = Optionals.filterAbsent(acc, next, function);
        acc = result.or(next);
        return result;
    }
}
