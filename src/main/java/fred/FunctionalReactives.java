package fred;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import fred.event.*;
import fred.frp.*;
import fred.sub.Subscribable;
import fred.sub.SubscribableIterable;

import java.util.List;
import java.util.Queue;

/**
 * Main class for composing functional reactives.
 * <pre>
 * Usage Example:
 * {@code
 * FunctionalReactives.from(1, 2, 3, 4, 5)
 *      .filter(new Predicate<Integer>() {
 *
 *          @Override public boolean apply(Integer input) {
 *              return input % 2 == 1;
 *          }
 *      })
 *      .reduce(new FunctionAcc<Integer, Integer>() {
 *          @Override public Optional<Integer> apply(Integer acc, Integer next) {
 *              return Optional.of(acc + next);
 *          }
 *      })
 *      .map(new Function<Integer, Optional<String>>() {
 *          @Override public Optional<String> apply(Integer input) {
 *              return Optional.of(input.toString());
 *          }
 *      })
 *      .forEach(new FunctionVoid<Object>() {
 *          @Override public void apply(Object input) {
 *              System.out.println(input);
 *          }
 *      })
 *      .start();
 *      //Output: (result of 1+3 and 1+3+9 in String)
 *      //4
 *      //9
 *
 * }
 * </pre>
 * Author:  Fred Deng
 */
public class FunctionalReactives<T> implements LifeCycle {

    public static <T> FunctionalReactives<T> createSync(Subscribable<T> subscribable) {
        return create(new EventManagerSync(), subscribable);
    }

    public static <T> FunctionalReactives<T> createAsync(Subscribable<T> subscribable) {
        return create(new EventManagerAsync(), subscribable);
    }

    public static <T> FunctionalReactives<T> from(Iterable<T> iterable) {
        return createSync(new SubscribableIterable<T>(iterable));
    }

    public static <T> FunctionalReactives<T> from(T... array) {
        return createSync(new SubscribableIterable<T>(array));
    }

    public static <T> FunctionalReactives<T> fromAsync(Iterable<T> iterable) {
        return createAsync(new SubscribableIterable<T>(iterable));
    }

    public static <T> FunctionalReactives<T> fromAsync(T... array) {
        return createAsync(new SubscribableIterable<T>(array));
    }

    private static <T> FunctionalReactives<T> create(EventManager em, Subscribable<T> subscribable) {
        return new FunctionalReactives<T>(em, subscribable);
    }

    private final EventManager em;
    private final EventReact<T> currReact;

    private FunctionalReactives(EventManager em, Subscribable<? extends T> subscribable) {
        this(em, new EventSourceImpl<T>(em, subscribable));
    }

    private FunctionalReactives(EventManager em, EventReact<T> currReact) {
        this.em = em;
        this.currReact = currReact;
    }

    public <T1> FunctionalReactives<T1> from(Subscribable<? extends T1> subscribable) {
        return new FunctionalReactives<T1>(em, subscribable);
    }

    public FunctionalReactives<T> filter(Predicate<? super T> predicate) {
        return chain(new ReactFuncFilter<T>(currReact, predicate));
    }

    private <T1> FunctionalReactives<T1> chain(ReactFunc<T1> reactFunc) {
        EventReactImpl<T1> next = new EventReactImpl<T1>(reactFunc);
        em.connect(currReact, next);
        return new FunctionalReactives<T1>(em, next);
    }

    private <T0, T1> FunctionalReactives<T1> chain(FunctionalReactives<T0> another, ReactFunc<T1> reactFunc) {
        FunctionalReactives<T1> next = chain(reactFunc);
        em.connect(another.currReact, next.currReact);
        return next;
    }

    public <T1> FunctionalReactives<T1> map(Function<? super T, Optional<T1>> function) {
        return chain(new ReactFuncMap<T, T1>(currReact, function));
    }

    public FunctionalReactives<Void> forEach(FunctionVoid<? super T> function) {
        return chain(new ReactFuncForeach<T>(currReact, function));
    }

    public FunctionalReactives<T> reduce(FunctionAcc<? super T, T> function) {
        return chain(new ReactFuncReduce<T>(currReact, function));
    }

    public <T1> FunctionalReactives<T1> fold(FunctionAcc<? super T, T1> function, T1 initValue) {
        return chain(new ReactFuncFold<T, T1>(currReact, function, initValue));
    }

    public <T1> FunctionalReactives<T1> flatMap(Function<? super T, Optional<Queue<T1>>> function) {
        return chain(new ReactFuncFlatMap<T, T1>(em, currReact, function));
    }

    /**
     * Apply the function when either input has fired, will use the last fired value from the other side if only one side
     * was fired.
     */
    public <T0, T1> FunctionalReactives<T1> zipEither(FunctionalReactives<T0> another, Function2<? super T, ? super T0, T1> function) {
        if (another.em != this.em) { //TODO: to support different event manager
            throw new UnsupportedOperationException("Not yet support zipping from different event manager");
        }
        return chain(another, new ReactFuncZipEither<T, T0, T1>(currReact, another.currReact, function));
    }

    /**
     * Apply the function when both input has fired, might queue up the fired input if only one side is fired,
     * which is like "zipping strictly" two stream of inputs.
     */
    public <T0, T1> FunctionalReactives<T1> zipStrict(FunctionalReactives<T0> another, Function2<? super T, ? super T0, T1> function) {
        if (another.em != this.em) { //TODO: to support different event manager
            throw new UnsupportedOperationException("Not yet support zipping from different event manager");
        }
        return chain(another, new ReactFuncZipStrict<T, T0, T1>(currReact, another.currReact, function, em));
    }

    public FunctionalReactives<List<T>> bufferBySize(int size) {
        return chain(new ReactFuncBuffer<T>(em, currReact, size));
    }

    @Override
    public void start() {
        em.start();
    }

    @Override
    public void shutdown() {
        em.shutdown();
    }

}
