package fred.frp;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fred.event.Event;
import fred.event.EventManager;
import fred.event.EventReact;

import java.util.List;

/**
 * Buffer until buffered items reaches the size or its connected flush source if there is.
 * <p/>
 * Note that the flush source needs to include in constructor if it belongs to the same source with source.
 */
public class ReactFuncBuffer<T> extends ReactFuncIgnoreHost<List<T>> {

    private final EventManager em;
    private final EventReact<T> source;
    private final EventReact<Integer> size;
    private final List<T> buffer = Lists.newArrayList();
    private final Event flushSource;

    //NOTE: Misc constructors here aims to allow for handling all different scenarios in a consistent way
    /**
     * Only react to source so flush only when buffer exceeds
     */
    public ReactFuncBuffer(EventManager em, EventReact<T> source, int fixedSize) {
        this(em, source, new EventReactConstant<Integer>(fixedSize), Events.alwaysNotFire());
    }

    /**
     * Flush either buffer exceed or flush source fired.
     */
    public ReactFuncBuffer(EventManager em, EventReact<T> source, EventReact<Integer> bufferSize, Event flushSource) {
        this.em = em;
        this.source = source;
        this.size = bufferSize;
        this.flushSource = flushSource;
    }

    @Override
    public Optional<List<T>> apply() {
        if ((bufferAdded() && buffer.size() >= size.value().or(0))  //buffer limit exceeds
                || em.isFired(flushSource)) { //flush source has fired
            return flushBuffer();
        }

        return Optional.absent();
    }

    private boolean bufferAdded() {
        if (em.isFired(source)) {
            Optional<T> val = source.value();
            if (val.isPresent()) {
                buffer.add(val.get());
            }
            return true;
        }
        return false;
    }

    private Optional<List<T>> flushBuffer() {
        try {
            if (!buffer.isEmpty()) {
                return Optional.<List<T>>of(ImmutableList.copyOf(buffer));
            }
            return Optional.absent();
        } finally{
            buffer.clear();
        }
    }
}
