package fred.util;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import fred.frp.FunctionAcc;
import fred.frp.FunctionVoid;

/**
 * Some monad like helper for filtering out absent values, like the way in Scala.
 *
 * Author:  Fred Deng
 */
public class Optionals {

    public static <F, T> Optional<T> filterAbsent(Optional<F> source, Function<? super F, Optional<T>> function) {
        return source.isPresent() ? function.apply(source.get()) : Optional.<T>absent();
    }

    public static <T> boolean filterAbsent(Optional<T> source, Predicate<? super T> predicate) {
        return source.isPresent() && predicate.apply(source.get());
    }

    public static <T> Optional<Void> filterAbsentVoid(Optional<T> source, FunctionVoid<? super T> functionVoid) {
        if(source.isPresent()){
            functionVoid.apply(source.get());
        }

        return Optional.absent();
    }

    public static <F, T> Optional<T> filterAbsent(Optional<T> acc, Optional<F> next, FunctionAcc<? super F, T> function ){
        return acc.isPresent() && next.isPresent() ? function.apply(acc.get(), next.get()) : Optional.<T>absent();
    }

}
