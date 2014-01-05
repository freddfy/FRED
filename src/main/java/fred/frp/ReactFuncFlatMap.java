package fred.frp;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import fred.event.EventManager;
import fred.event.EventReact;
import fred.util.Optionals;

import java.util.Queue;

/**
 *
 * Author:  Fred Deng
 */
public class ReactFuncFlatMap<F, T> implements ReactFunc<T> {

    private final Queue<T> EMPTY_QUEUE = Lists.newLinkedList();
    private final Function<? super F, Optional<Queue<T>>> function;
    private final EventManager em;
    private final EventReact<F> source;
    private final ReactFuncSchedulable<T> schedulable;

    public ReactFuncFlatMap(EventManager em, EventReact<F> source, Function<? super F, Optional<Queue<T>>> function) {
        this.function = function;
        this.em = em;
        this.source = source;
        this.schedulable = new ReactFuncSchedulable<T>(em);
    }

    @Override
    public Optional<T> apply() {
        if (em.isFired(source)) {
            Optional<Queue<T>> pendingResults = Optionals.filterAbsent(source.value(), function);
            if (!pendingResults.or(EMPTY_QUEUE).isEmpty()) {
                return pollAndScheduleTheRest(pendingResults.get());
            }
        }

        return schedulable.apply();
    }

    private Optional<T> pollAndScheduleTheRest(Queue<? extends T> pendingResults) {
        T result = pendingResults.poll();
        while (!pendingResults.isEmpty()) {
            schedulable.schedule(pendingResults.poll());
        }
        return Optional.fromNullable(result);
    }

    @Override
    public void setHost(EventReact<T> host) {
        schedulable.setHost(host);
    }

}
