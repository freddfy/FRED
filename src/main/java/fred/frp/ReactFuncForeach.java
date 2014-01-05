package fred.frp;

import com.google.common.base.Optional;
import fred.event.EventReact;
import fred.util.Optionals;

/**
 * The forEach function will always assume not fired successfully should its event host should not have dependants becos
 * they will never be fired.
 *
 * Author:  Fred Deng
 */
public class ReactFuncForeach<T> extends ReactFuncIgnoreHost<Void> {

    private final EventReact<T> source;
    private final FunctionVoid<? super T> function;

    public ReactFuncForeach(EventReact<T> source, FunctionVoid<? super T> function) {
        this.source = source;
        this.function = function;
    }


    @Override
    public Optional<Void> apply() {
        return Optionals.filterAbsentVoid(source.value(), function);
    }
}
