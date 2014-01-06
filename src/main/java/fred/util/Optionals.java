package fred.util;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import fred.frp.Function2;
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

    public static <F1, F2, T> Optional<T> filterAbsent(Optional<F1> input1,
                                                       Optional<F2> input2,
                                                       Function2<? super F1, ? super F2, T> function){
        return input1.isPresent() && input2.isPresent() ? function.apply(input1.get(), input2.get()) : Optional.<T>absent();
    }

}
