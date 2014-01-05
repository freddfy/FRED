package fred.frp;

import com.google.common.base.Optional;
import fred.event.EventReact;

/**
 * Generic function interface for functional reactive programming.
 *
 * Author:  Fred Deng
 */
public interface ReactFunc<T> {
    /**
     * Must only apply once in one cycle, or undetermined behavior.
     *
     * @return function result in current cycle. Optional.absent() should be returned if the function calculation
     * is invalid and therefore its event host should not be fired and so as the host's dependants (if any).
     */
    //Optional.absent() obviously more elegant then returning null, and can be lifted in a generic way (see class Optionals)
    Optional<T> apply();

    /**
     * In case the function need to be aware of its event host.
     */
    void setHost(EventReact<T> host);
}
