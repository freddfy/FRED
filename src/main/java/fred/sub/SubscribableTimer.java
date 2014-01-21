package fred.sub;

import fred.event.Schedulable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Timer event source.
 *
 * The subscribable schedule value will always be true, even though it is not necessary. Ideally it could be of type
 * Void but unfortunately EventReact contract will always regard Void value as not fired, so make it boolean to bypass
 * the limitation.
 */
public class SubscribableTimer implements Subscribable<Boolean> {
    public static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();

    private final long delay;
    private final long period;
    private final TimeUnit timeUnit;
    private final ScheduledExecutorService executor;
    private volatile ScheduledFuture<?> future;

    /**
     * Timer schedule fires per period of timeUnit with the initial delay
     */
    public SubscribableTimer(long delay, long period, TimeUnit timeUnit) {
        this(delay, period, timeUnit, EXECUTOR);
    }

    //for unit test
    protected SubscribableTimer(long delay, long period, TimeUnit timeUnit, ScheduledExecutorService executor) {
        this.delay = delay;
        this.period = period;
        this.timeUnit = timeUnit;
        this.executor = executor;
    }

    public void doSubscribe(final Schedulable<? super Boolean> source) {
        unsubscribe(); //in case it is subscribed before, though invoker should ensure they do not invoke more than once

        future = executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                source.schedule(true);
            }
        }, delay, period, timeUnit);
    }

    @Override
    public void unsubscribe() {
        if (future != null) {
            future.cancel(true);
        }
    }
}
