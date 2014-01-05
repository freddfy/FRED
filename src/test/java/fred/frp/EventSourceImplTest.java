package fred.frp;

import fred.event.EventManager;
import fred.event.EventSource;
import fred.event.Schedulable;
import fred.sub.Subscribable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.guava.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 * Author:  Fred Deng
 */
public class EventSourceImplTest {
    private EventSourceImpl<String> subject;
    private EventManager em;
    private Subscribable<String> subscribable;
    private Schedulable<String> schedulable;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        em = mock(EventManager.class);
        subscribable = mock(Subscribable.class);
    }

    @SuppressWarnings("unchecked")
    private void setup(EventSourceImpl<String> source) {
        subject = source;

        ArgumentCaptor<Schedulable> captor = ArgumentCaptor.forClass(Schedulable.class);
        subject.start();

        verify(subscribable).doSubscribe(captor.capture());
        schedulable = captor.getValue();
    }

    @Test
    public void testEMConnected() throws Exception {
        setup(new EventSourceImpl<String>(em, subscribable));

        verify(em).connect(subject);
    }

    @Test
    public void testInitValue() throws Exception {
        setup(new EventSourceImpl<String>(em, "initString", subscribable));

        assertThat(subject.value()).contains("initString");
    }

    @Test
    public void testInitValueIsAbsentIfNotSpecified() throws Exception {
        setup(new EventSourceImpl<String>(em, subscribable));

        assertThat(subject.value()).isAbsent();
    }

    @Test
    public void testScheduleValue() throws Exception {
        setup(new EventSourceImpl<String>(em, subscribable));

        schedulable.schedule("newString");

        ArgumentCaptor<Runnable> arg =ArgumentCaptor.forClass(Runnable.class);
        verify(em).schedule(arg.capture());

        arg.getValue().run();
        verify(em).fireCycle(subject);

        //What em.fireCycle should invoke
        assertThat(subject.tryFire(1)).isTrue();

        //schedule once should only fire once regardless of cycles
        assertThat(subject.tryFire(1)).isFalse();
        assertThat(subject.tryFire(2)).isFalse();

        assertThat(subject.value()).contains("newString");
    }


}
