package fred.sub;

import fred.event.Schedulable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Author:  Fred Deng
 */
public class SubscribableTimerTest {
    private static final long DELAY = 1l;
    private static final long PERIOD = 2l;

    private SubscribableTimer subject;
    private ScheduledExecutorService executor;
    private Schedulable<Boolean> source;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        executor = mock(ScheduledExecutorService.class);
        subject = new SubscribableTimer(DELAY, PERIOD, TimeUnit.SECONDS, executor);
        source = mock(Schedulable.class);
    }

    @Test
    public void testDoSubscribeWillStartSchedulingRepeatedTaskAtSpecifiedInterval() throws Exception {
        subject.doSubscribe(source);

        verify(source, never()).schedule(anyBoolean());//not schedule yet due to mocking

        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(executor).scheduleAtFixedRate(captor.capture(), eq(DELAY), eq(PERIOD), eq(TimeUnit.SECONDS));

        Runnable runnable = captor.getValue();
        runnable.run();
        verify(source, times(1)).schedule(true);

        runnable.run();
        verify(source, times(2)).schedule(true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnsubscribeWillCancelTheScheduledTimerTask() throws Exception {
        ScheduledFuture future = mock(ScheduledFuture.class);

        when(executor.scheduleAtFixedRate(any(Runnable.class), anyLong(), anyLong(), any(TimeUnit.class)))
                .thenReturn(future);

        subject.doSubscribe(source);

        subject.unsubscribe();

        verify(future).cancel(true);
    }
}
