package fred.frp;

import com.google.common.base.Optional;

/**
 * Accumulation function which can be used by fold / reduce.
 *
 * Author:  Fred Deng
 */
public interface FunctionAcc<F, T> {
    /**
     *
     * @param acc  accumulated value from last apply
     * @param next the incoming event value
     * @return the next value calculated by the last accumulated value and the next value. Optional.absent() should be
     * returned if the calculated value is not valid and therefore the host event should not be fired.
     */
    Optional<T> apply(T acc, F next);
}
