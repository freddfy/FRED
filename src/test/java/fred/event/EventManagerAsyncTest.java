package fred.event;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.RejectedExecutionException;

import static org.mockito.Mockito.mock;

/**
 * Author:  Fred Deng
 */
public class EventManagerAsyncTest {
    private EventManagerAsync subject;

    @Before
    public void setUp() throws Exception {
        subject = new EventManagerAsync();

    }

    @Test(expected= RejectedExecutionException.class)
    public void testExceptionWhenScheduleAfterShutDown() throws Exception {
        subject.shutdown();

        subject.schedule(mock(Runnable.class));
    }

    @Test(expected= RejectedExecutionException.class)
    public void testExceptionWhenScheduleFireAfterShutDown() throws Exception {
        subject.shutdown();

        subject.scheduleFire(mock(Event.class));
    }
}
