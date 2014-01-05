package fred.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;

/**
 * Author: Fred Deng
 */
public class EventManagerLifeCycleTest {
    private EventManagerLifeCycle subject;
    private EventManager em;
    private EventLifeCycle elc1;
    private EventLifeCycle elc2;
    private EventLifeCycle elc3;
    private Event e1;
    private Event e2;
    private Event e3;

    @Before
    public void setUp() throws Exception {
        em = mock(EventManager.class);
        subject = new EventManagerLifeCycle(em);

        elc1 = mock(EventLifeCycle.class, "elc1");
        elc2 = mock(EventLifeCycle.class, "elc2");
        elc3 = mock(EventLifeCycle.class, "elc3");

        e1 = mock(Event.class);
        e2 = mock(Event.class);
        e3 = mock(Event.class);
    }

    @Test
    public void testStartAndShutDownConnectedLifeCycleEventsInOrder() throws Exception {
        InOrder order = inOrder(elc1, elc2, elc3);

        subject.connect(elc1);
        subject.connect(elc2, e2);
        subject.connect(elc3, elc1);
        subject.connect(e1, e2);
        subject.connect(e2);
        subject.connect(e3, e2);

        verify(elc1, never()).start();
        verify(elc2, never()).start();
        verify(elc3, never()).start();

        subject.start();

        order.verify(elc1, times(1)).start();
        order.verify(elc2, times(1)).start();
        order.verify(elc3, times(1)).start();

        subject.shutdown();

        verify(elc1, times(1)).shutdown();
        verify(elc2, times(1)).shutdown();
        verify(elc3, times(1)).shutdown();
    }

    @Test
    public void testMultipleStartAndShutdownWillOnlyInvokeEventStartShutdownOnce() throws Exception {
        //multiple start invocations
        subject.connect(elc1);
        subject.start();
        verify(elc1, times(1)).start();
        verify(elc2, never()).start();

        subject.connect(elc2, e2);
        subject.start();
        verify(elc1, times(1)).start();
        verify(elc2, times(1)).start();

        subject.start();
        verify(elc1, times(1)).start();
        verify(elc2, times(1)).start();

        //multiple shutdown invocations
        verify(elc1, times(0)).shutdown();
        verify(elc2, times(0)).shutdown();

        subject.shutdown();
        verify(elc1, times(1)).shutdown();
        verify(elc2, times(1)).shutdown();

        subject.shutdown();
        verify(elc1, times(1)).shutdown();
        verify(elc2, times(1)).shutdown();
    }

    @Test
    public void testAnyLifeCycleEventsStartAndShutdownExceptionWontAffectOthers() throws Exception {
        doThrow(new RuntimeException("ecl2 test")).when(elc2).start();

        subject.connect(elc1);
        subject.connect(elc2);
        subject.connect(elc3);

        subject.start();

        verify(elc1).start();
        verify(elc2).start();
        verify(elc3).start();

        doThrow(new RuntimeException("elc3 test")).when(elc3).shutdown();

        subject.shutdown();

        verify(elc1).shutdown();
        verify(elc2, never()).shutdown(); //becos it failed to start
        verify(elc3).shutdown();

    }

    private interface EventLifeCycle extends Event, LifeCycle {
    }
}
