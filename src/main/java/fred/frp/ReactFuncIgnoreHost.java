package fred.frp;

import fred.event.EventReact;

/**
 * For functions that neglect their hosts.
 *
 * Author:  Fred Deng
 */
public abstract class ReactFuncIgnoreHost<T> implements ReactFunc<T> {
    @Override
    public void setHost(EventReact<T> host) {
    }
}
