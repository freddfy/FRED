package fred.event;

/**
 * Simple implementation allow event graph and propagation to run the the client running thread.
 *
 * Synchronous invocation also makes it easier for unit-test event propagation.
 *
 * Author:  Fred Deng
 */
public class EventManagerSync extends EventManagerDecorator {

    private final EventManager delegate;
    private boolean started = true;

    public EventManagerSync() {
        this(new EventManagerLifeCycle());
    }

    protected EventManagerSync(EventManager delegate) {
        this.delegate = delegate;
    }

    @Override
    protected EventManager delegate() {
        return delegate;
    }

    @Override
    public void scheduleFire(Event source) {
        if (started) {
            delegate.scheduleFire(source);
        } else {
            throw new IllegalStateException("EventManager shut down");
        }
    }

    @Override
    public void schedule(Runnable runnable) {
        if (started) {
            delegate.schedule(runnable);
        }else {
            throw new IllegalStateException("EventManager shut down");
        }
    }

    @Override
    public void shutdown() {
        delegate.shutdown();
        started = false;
    }
}
