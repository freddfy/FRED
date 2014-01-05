package fred.event;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import fred.graph.Graph;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

/**
 *
 * Author:  Fred Deng
 */
public class EventManagerCoreTest {
    private EventManagerCore subject;
    private Graph<Event> graph;
    private Event alwaysFired1;
    private Event alwaysFired2;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        graph = mock(Graph.class);
        subject = new EventManagerCore(graph);

        alwaysFired1 = alwaysFired();
        alwaysFired2 = alwaysFired();
    }

    private Event alwaysFired() {
        Event evt = mock(Event.class);
        when(evt.tryFire(anyLong())).thenReturn(true);
        return evt;
    }

    private Event alwaysNotFired() {
        Event evt = mock(Event.class);
        when(evt.tryFire(anyLong())).thenReturn(false);
        return evt;
    }

    @Test
    public void testConnectDelegation() throws Exception {
        subject.connect(alwaysFired1, alwaysFired2);
        verify(graph).connect(alwaysFired1, alwaysFired2);
    }

    @Test
    public void testDisconnect2Delegation() throws Exception {
        subject.disconnect(alwaysFired1, alwaysFired2);
        verify(graph).disconnect(alwaysFired1, alwaysFired2);
    }

    @Test
    public void testDisconnect1Delegation() throws Exception {
        subject.disconnect(alwaysFired2);
        verify(graph).disconnect(alwaysFired2);
    }

    @Test
    public void testFireCycle() throws Exception {
        when(graph.iterator(alwaysFired1)).thenReturn(Iterators.forArray(alwaysFired1, alwaysFired2));

        subject.fireCycle(alwaysFired1);

        InOrder order = inOrder(alwaysFired1, alwaysFired2);

        order.verify(alwaysFired1).tryFire(anyLong());
        order.verify(alwaysFired2).tryFire(anyLong());
    }

    @Test
    public void testConnect1Delegation() throws Exception {
        subject.connect(alwaysFired1);

        verify(graph).connect(alwaysFired1);
    }

    @Test
    public void testScheduleRunnable() throws Exception {
        Runnable runnable = mock(Runnable.class);

        subject.schedule(runnable);

        verify(runnable).run();
    }

    @Test
    public void testRemoveNonFiredEventAndDependentFromTraverse() throws Exception {
        Event notFired = alwaysNotFired();

        List<Event> containsAlwaysNotFired = Lists.newArrayList(notFired);

        when(graph.iterator(notFired)).thenReturn(containsAlwaysNotFired.iterator());

        subject.fireCycle(notFired);

        assertThat(containsAlwaysNotFired).overridingErrorMessage("alwaysNotFired should have been removed").isEmpty();
    }


    @Test
    public void testSelfScheduleEventShouldFireAfterTheFirstCycleFinishes() throws Exception {
        final Event selfSchedule = mock(Event.class);
        when(selfSchedule.tryFire(1l)).thenAnswer(
                new Answer<Boolean>() {
                    private boolean firedAtLeastOnce = false;

                    @Override
                    public Boolean answer(InvocationOnMock invocation) throws Throwable {
                        subject.scheduleFire(selfSchedule);
                        return true;
                    }
                }
        );
        when(selfSchedule.tryFire(2l)).thenReturn(true);

        InOrder inOrder = inOrder(selfSchedule, alwaysFired1);

        List<Event> events = Lists.newArrayList(selfSchedule, alwaysFired1);
        when(graph.iterator(selfSchedule)).thenReturn(events.iterator()).thenReturn(events.iterator());

        subject.scheduleFire(selfSchedule);

        inOrder.verify(selfSchedule).tryFire(1);
        inOrder.verify(alwaysFired1).tryFire(1);
        inOrder.verify(selfSchedule).tryFire(2);
        inOrder.verify(alwaysFired1).tryFire(2);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testIsEventFired() throws Exception {
        final Event notFired = new EventCycleAware() {
            @Override
            protected boolean fired() {
                return false;
            }
        };
        final Event alwaysFired = new EventCycleAware() {
            @Override
            protected boolean fired() {
                return true;
            }
        };

        when(graph.iterator(notFired)).thenReturn(Lists.newArrayList(alwaysFired, notFired).iterator());

        subject.scheduleFire(notFired);

        assertThat(subject.isFired(alwaysFired)).isTrue();
        assertThat(subject.isFired(notFired)).isFalse();

    }
}
