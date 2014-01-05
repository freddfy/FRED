package fred.frp;

import com.google.common.base.Function;

/**
 * Function used by forEach event.
 *
 * Author:  Fred Deng
 */
public interface FunctionVoid<T> {
    /**
     * This function will return nothing and therefore the host event will not be fired no mater what even incoming.
     *
     * @param next the next incoming event value
     */
    void apply(T next);
}
