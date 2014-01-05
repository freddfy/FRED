package fred.frp;

import com.google.common.base.Optional;
import fred.event.EventReact;
import fred.util.Optionals;

/**
 *
 * Author:  Fred Deng
 */
public class ReactFuncFold<F, T> extends ReactFuncIgnoreHost<T> {
    private final EventReact<F> source;
    private final FunctionAcc<? super F, T> function;
    private Optional<T> acc;

    public ReactFuncFold(EventReact<F> source, FunctionAcc<? super F, T> function, T initValue) {
        this.source = source;
        this.function = function;
        this.acc = Optional.of(initValue);
    }

    @Override
    public Optional<T> apply() {
        Optional<T> result = Optionals.filterAbsent(acc, source.value(), function);
        acc = result.or(acc);
        return result;
    }
}
