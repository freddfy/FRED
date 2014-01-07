package fred.frp;

import com.google.common.base.Optional;
import fred.event.EventReact;
import fred.util.Optionals;

/**
 * Apply function when either input is fired, last fired value will be used when that side is not fired.
 *
 * Author: Fred Deng
 */
public class ReactFuncZipEither<F1, F2, T> extends ReactFuncIgnoreHost<T> {

    private final EventReact<F1> input1;
    private final EventReact<F2> input2;
    private final Function2<? super F1, ? super F2, T> function;

    public ReactFuncZipEither(EventReact<F1> input1, EventReact<F2> input2, Function2<? super F1, ? super F2, T> function) {
        this.input1 = input1;
        this.input2 = input2;
        this.function = function;
    }

    @Override
    public Optional<T> apply() {
        return Optionals.filterAbsent(input1.value(), input2.value(), function);
    }
}
