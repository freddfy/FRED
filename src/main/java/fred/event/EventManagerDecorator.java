package fred.event;

/**
 * Decorator helper class for decorating the delegate. Subclass should override the methods they want to decorate.
 *
 * Author:  Fred Deng
 */
public abstract class EventManagerDecorator implements EventManager {

    protected abstract EventManager delegate();

    @Override
    public void fireCycle(Event source) {
       delegate().fireCycle(source);
    }

    @Override
    public boolean isFired(Event event) {
        return delegate().isFired(event);
    }

    @Override
    public void scheduleFire(Event source) {
        delegate().scheduleFire(source);
    }

    @Override
    public void schedule(Runnable runnable) {
        delegate().schedule(runnable);
    }

    @Override
    public void connect(Event source) {
        delegate().connect(source);
    }

    @Override
    public void connect(Event source, Event target) {
        delegate().connect(source, target);
    }

    @Override
    public void disconnect(Event source, Event target) {
        delegate().disconnect(source, target);
    }

    @Override
    public void disconnect(Event target) {
        delegate().disconnect(target);
    }

    @Override
    public void start() {
        delegate().start();
    }

    @Override
    public void shutdown() {
        delegate().shutdown();
    }
}
