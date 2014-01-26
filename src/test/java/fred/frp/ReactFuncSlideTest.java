package fred.frp;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import fred.event.Event;
import fred.event.EventManager;
import fred.event.EventReact;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.base.Optional.of;
import static org.assertj.guava.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Author:  Fred Deng
 */
public class ReactFuncSlideTest {
    private EventReact<Integer> source;
    private ReactFuncSlide<Integer> subject;
    private EventManager em;
    private Event signal;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        source = mock(EventReact.class);
        em = mock(EventManager.class);
        signal = mock(Event.class);
    }

    @Test
    public void testSlideInSizeOfThree() throws Exception {
        subject = new ReactFuncSlide<Integer>(source, 3);

        when(source.value()).thenReturn(of(1)).thenReturn(of(2)).thenReturn(of(3)).thenReturn(of(4));

        assertThat(subject.apply()).contains(ImmutableList.of(1));
        assertThat(subject.apply()).contains(ImmutableList.of(1, 2));
        assertThat(subject.apply()).contains(ImmutableList.of(1, 2, 3));
        assertThat(subject.apply()).contains(ImmutableList.of(2, 3, 4));
    }

    @Test
    public void testSlideBySlideSignal() throws Exception {
        subject = new ReactFuncSlide<Integer>(source, 2, em, signal);

        when(em.isFired(source)).thenReturn(true);
        when(source.value()).thenReturn(of(1)).thenReturn(of(2)).thenReturn(of(3)).thenReturn(of(4));
        when(em.isFired(signal)).thenReturn(false).thenReturn(true).thenReturn(true).thenReturn(true);

        assertThat(subject.apply()).isAbsent();
        assertThat(subject.apply()).contains(ImmutableList.of(1, 2));
        assertThat(subject.apply()).contains(ImmutableList.of(1, 2, 3));
        assertThat(subject.apply()).contains(ImmutableList.of(3, 4));

    }

    @Test
    public void testSlideBySignalWithAbsentSourceValuesAtTheBeginning() throws Exception {
        subject = new ReactFuncSlide<Integer>(source, 2, em, signal);

        when(em.isFired(source)).thenReturn(true);
        when(source.value()).thenReturn(Optional.<Integer>absent()).thenReturn(of(1));
        when(em.isFired(signal)).thenReturn(true);

        assertThat(subject.apply()).isAbsent();
        assertThat(subject.apply()).contains(ImmutableList.of(1));
    }

    @Test
    public void testSlideBySignalWithAbsentSourceValuesInTheMiddle() throws Exception {
        subject = new ReactFuncSlide<Integer>(source, 1, em, signal);

        when(em.isFired(source)).thenReturn(true);
        when(source.value()).thenReturn(of(1)).thenReturn(Optional.<Integer>absent()).thenReturn(of(3));
        when(em.isFired(signal)).thenReturn(true);

        assertThat(subject.apply()).contains(ImmutableList.of(1));
        assertThat(subject.apply()).isAbsent();
        assertThat(subject.apply()).contains(ImmutableList.of(3));

    }

    @Test
    public void testSlideBySignalWillFadeOutWhenNoMoreSourcesFired() throws Exception {
        subject = new ReactFuncSlide<Integer>(source, 2, em, signal);

        when(em.isFired(source)).thenReturn(true).thenReturn(true).thenReturn(false);
        when(source.value()).thenReturn(of(1)).thenReturn(of(2)).thenReturn(Optional.<Integer>absent());
        when(em.isFired(signal)).thenReturn(true);

        assertThat(subject.apply()).contains(ImmutableList.of(1));
        assertThat(subject.apply()).contains(ImmutableList.of(1, 2));
        assertThat(subject.apply()).contains(ImmutableList.of(2));
        assertThat(subject.apply()).isAbsent();
        assertThat(subject.apply()).isAbsent();

    }
}
