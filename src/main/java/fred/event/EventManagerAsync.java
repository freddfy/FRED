package fred.event;

import com.google.common.base.Throwables;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Event graph and propagation happens in a single thread pool.
 *
 * Author:  Fred Deng
 */
public class EventManagerAsync extends EventManagerDecorator {

    private final ExecutorService executor;
    private final EventManager delegate = new EventManagerLifeCycle();

    public EventManagerAsync() {
        this(Executors.newSingleThreadExecutor());
    }

    protected EventManagerAsync(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    protected EventManager delegate() {
        return delegate;
    }

    @Override
    public void scheduleFire(final Event source) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                delegate.scheduleFire(source);
            }
        });
    }

    @Override
    public void schedule(final Runnable runnable) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                delegate.schedule(runnable);
            }
        });
    }

    @Override
    public void connect(final Event source) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                delegate.connect(source);
            }
        });
    }

    @Override
    public void connect(final Event source, final Event target) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                delegate.connect(source, target);
            }
        });
    }

    @Override
    public void disconnect(final Event source, final Event target) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                delegate.disconnect(source, target);
            }
        });
    }

    @Override
    public void disconnect(final Event target) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                delegate.disconnect(target);
            }
        });
    }

    @Override
    public void start() {
        try {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    delegate.start();
                }
            }).get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            Throwables.propagate(e); //TODO: properly logged
        }
    }

    @Override
    public void shutdown() {
        try {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    delegate.shutdown();
                }
            }).get(30, TimeUnit.SECONDS);

            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);

        } catch (Exception e) {
            Throwables.propagate(e);  //TODO: properly logged
        }
    }
}
