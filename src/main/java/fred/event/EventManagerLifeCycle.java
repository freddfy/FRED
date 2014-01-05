package fred.event;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Managing life cycles of events by start/shutdown if they are of LifeCycle.
 * <p/>
 * Author: Fred Deng
 */
public class EventManagerLifeCycle extends EventManagerDecorator {

    private final EventManager delegate;
    private final Set<LifeCycle> started = Sets.newHashSet();
    private final Set<LifeCycle> toStart = Sets.newLinkedHashSet(); //start sequence might matter

    public EventManagerLifeCycle() {
        this(new EventManagerCore());
    }

    protected EventManagerLifeCycle(EventManager delegate) {
        this.delegate = delegate;
    }

    @Override
    protected EventManager delegate() {
        return delegate;
    }

    @Override
    public void connect(Event source) {
        super.connect(source);
        addIfLifeCycle(source);
    }

    private void addIfLifeCycle(Event source) {
        if (source instanceof LifeCycle) {
            LifeCycle candidate = (LifeCycle) source;
            if (!started.contains(candidate)) {
                toStart.add(candidate);
            }
        }
    }

    @Override
    public void connect(Event source, Event target) {
        super.connect(source, target);
        addIfLifeCycle(source);
        addIfLifeCycle(target);
    }

    //Not overriding disconnect(source, target) because you cannot assume source need to be shut down
    // even it no longer has dependants. (it might have side effect alone)

    @Override
    public void disconnect(Event target) {
        super.disconnect(target);
        removeIfLifeCycle(target);
    }

    private void removeIfLifeCycle(Event target) {
        if (target instanceof LifeCycle) {
            LifeCycle candidate = (LifeCycle) target;
            if (started.remove(candidate)) {
                candidate.shutdown();
            } else {
                toStart.remove(candidate);
            }
        }
    }

    @Override
    public void start() {
        delegate.start();

        for (LifeCycle candidate : toStart) {
            try {
                candidate.start();
                started.add(candidate);
            } catch (Exception ex) { //so that failure in one candidate.start() won't affect other toStart
                ex.printStackTrace(); //TODO: add logging
            }
        }

        toStart.clear();
    }

    @Override
    public void shutdown() {
        for (LifeCycle candidate : started) {
            try {
                candidate.shutdown();
            } catch (Exception ex) { //so that failure in any one candidate.shutdown() won't affect others
                ex.printStackTrace(); //TODO: add logging
            }
        }

        started.clear();
        toStart.clear();

        delegate.shutdown();
    }
}
