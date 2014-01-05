package fred.event;

import com.google.common.base.Optional;

/**
 * Reacts to the event source and evaluation during event propagation.
 *
 * Author:  Fred Deng
 */
public interface EventReact<T> extends Event {
    /**
     * @return may or may not contains a value depending on even fire/reacts.
     */
    Optional<T> value();
}
