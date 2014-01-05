package fred.event;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 *
 * Author:  Fred Deng
 */
public class EventManagerSyncTest {
    private EventManagerSync subject;
    private EventManager delegate;
    private Runnable runnable;
    private Event event;

    @Before
    public void setUp() throws Exception {
        delegate = mock(EventManager.class);
        subject = new EventManagerSync(delegate);

        runnable = mock(Runnable.class);
        event = mock(Event.class);
    }

    @Test
    public void testSchedule() throws Exception {
        subject.schedule(runnable);

        verify(delegate).schedule(runnable);
    }

    @Test
    public void testScheduleFire() throws Exception {
        subject.scheduleFire(event);

        verify(delegate).scheduleFire(event);
    }

    @Test(expected=IllegalStateException.class)
    public void testExceptionWhenScheduleAfterShutDown() throws Exception {
        subject.shutdown();

        subject.schedule(runnable);
    }

    @Test(expected=IllegalStateException.class)
    public void testExceptionWhenScheduleFireAfterShutDown() throws Exception {
        subject.shutdown();

        subject.scheduleFire(event);
    }
}
