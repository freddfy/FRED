package fred.frp;

import com.google.common.base.Optional;

/**
 * Function with 2 arguments returning one result, typically used by Zip.
 *
 * Author: Fred Deng
 */
public interface Function2<F1, F2, T> {

    Optional<T> apply(F1 input1, F2 input2);

}
