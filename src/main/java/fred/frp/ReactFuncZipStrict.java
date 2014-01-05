package fred.frp;

import com.google.common.base.Optional;
import fred.event.EventReact;

/**
 *
 * Author: Fred Deng
 */
public class ReactFuncZipStrict<F1, F2, T> implements ReactFunc<T>{
    @Override
    public Optional<T> apply() {
        return null;
    }

    @Override
    public void setHost(EventReact<T> host) {

    }
}
