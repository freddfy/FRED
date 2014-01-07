package fred.frp;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import fred.event.EventManager;
import fred.event.EventReact;

import java.util.Queue;

/**
 * Zip on two streams of input events by applying the specified function on the input value.
 *
 * Caution: if only one side fires a lot whilst the other side fires little, memory will be consumed for the accumulated
 * cached value on the frequent fire side.
 *
 * Author: Fred Deng
 */
public class ReactFuncZipStrict<F1, F2, T> extends ReactFuncIgnoreHost<T>{
    private final EventReact<F1> input1;
    private final EventReact<F2> input2;
    private final Function2<? super F1, ? super F2, T> function;
    private final EventManager em;
    private final Queue<F1> queue1 = Lists.newLinkedList();
    private final Queue<F2> queue2 = Lists.newLinkedList();

    public ReactFuncZipStrict(EventReact<F1> input1, EventReact<F2> input2,
                              Function2<? super F1, ? super F2, T> function, EventManager em) {
        this.input1 = input1;
        this.input2 = input2;
        this.function = function;
        this.em = em;
    }

    @Override
    public Optional<T> apply() {
        addInputIfValid(input1, queue1);
        addInputIfValid(input2, queue2);

        if (!queue1.isEmpty() && !queue2.isEmpty()) {
            return function.apply(queue1.poll(), queue2.poll());
        }

        return Optional.absent();
    }

    private <T> void addInputIfValid(EventReact<T> input, Queue<T> queue) {
        if(em.isFired(input)) {
            Optional<T> value = input.value();
            if (value.isPresent()) {
                queue.add(value.get());
            }
        }
    }
}
