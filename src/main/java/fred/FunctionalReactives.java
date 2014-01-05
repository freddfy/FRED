package fred;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import fred.event.*;
import fred.frp.*;
import fred.sub.Subscribable;
import fred.sub.SubscribableIterable;

import java.util.Queue;

/**
 * Main class for composing functional reactives.
 *
 * Usage Example:
 * <code>
 *  FunctionalReactives.from(1, 2, 3, 4, 5)
 *       .filter(new Predicate<Integer>() {
 *          @Override
 *          public boolean apply(Integer input) {
 *              return input % 2 == 1;
 *          }
 *      })
 *       .map(new Function<Integer, Optional<String>>() {
 *          @Override
 *          public Optional<String> apply(Integer input) {
 *              return Optional.of(input.toString());
 *          }
 *      })
 *       .reduce(new FunctionAcc<String, String>() {
 *          @Override
 *          public Optional<String> apply(String acc, String next) {
 *              return Optional.of(acc + next);
 *          }
 *      })
 *       .forEach(new FunctionVoid<Object>() {
 *          @Override
 *          public void apply(Object input) {
 *              System.out.println(input);
 *          }
 *      })
 *       .start();
 * </code>
 *
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

    @Override
    public void start() {
        em.start();
    }

    @Override
    public void shutdown() {
        em.shutdown();
    }

}
