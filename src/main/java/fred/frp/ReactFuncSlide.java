package fred.frp;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fred.event.Event;
import fred.event.EventManager;
import fred.event.EventReact;
import fred.util.Optionals;

import java.util.LinkedList;
import java.util.List;

/**
 * Slide function
 * <p/>
 * Author:  Fred Deng
 */
public class ReactFuncSlide<T> extends ReactFuncIgnoreHost<List<T>> {
    private final EventReact<T> source;
    private final int size;
    private final LinkedList<Item> buffer;
    private final Event slideSignal;
    private final EventManager em;
    private long seq = 0;

    public ReactFuncSlide(EventReact<T> source, int size) {
        this(source, size, null, null);
    }

    public ReactFuncSlide(EventReact<T> source, int slideSize, EventManager em, Event slideSignal) {
        this.size = slideSize;
        this.source = source;
        this.buffer = Lists.newLinkedList();
        addNextEmptySlideSlot();
        this.em = em;
        this.slideSignal = slideSignal;
    }

    private void addNextEmptySlideSlot() {
        buffer.add(new Item(seq++));
    }

    @Override
    public Optional<List<T>> apply() {
        if(isSourceFired()){
            buffer.getLast().add(source.value());
        }

        if (isSlideSignaled()) {
            if (buffer.peek().seq < seq - size) {
                buffer.poll();
            }

            addNextEmptySlideSlot();

            List<T> result = ImmutableList.copyOf(result());
            return result.isEmpty() ? Optional.<List<T>>absent() : Optional.of(result);
        } else {
            return Optional.absent();
        }
    }

    private boolean isSourceFired() {
        return em == null || em.isFired(source);
    }

    private Iterable<T> result() {
        return FluentIterable.from(buffer).transformAndConcat(
                new Function<Item, Iterable<T>>() {
                    @Override
                    public Iterable<T> apply(Item input) {
                        return Optionals.filterAbsent(input.values);
                    }
                }
        );
    }

    private boolean isSlideSignaled() {
        return em == null //null means does not care slideSignal so always signaled
                || em.isFired(slideSignal);
    }

    public class Item{
        private final long seq;
        private final List<Optional<T>> values = Lists.newLinkedList();

        public Item(long seq) {
            this.seq = seq;
        }

        public void add(Optional<T> value) {
            this.values.add(value);
        }
    }

}
