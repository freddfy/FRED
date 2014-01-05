package fred.frp;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.primitives.Chars;
import fred.event.Event;
import fred.event.EventManager;
import fred.event.EventReact;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Queue;

import static com.google.common.base.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.guava.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 *
 * Author:  Fred Deng
 */
public class ReactFuncFlatMapTest {
    private ReactFuncFlatMap<String, Character> subject;
    private EventManager em;
    private EventReact<String> source;
    private EventReact host;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        em = mock(EventManager.class);
        source = mock(EventReact.class);
        subject = new ReactFuncFlatMap<String, Character>(em, source, new Function<String, Optional<Queue<Character>>>() {
            @Override
            public Optional<Queue<Character>> apply(String input) {
                return Optional.<Queue<Character>>of(Lists.newLinkedList(Chars.asList(input.toCharArray())));
            }
        });

        host = mock(EventReact.class);
        subject.setHost(host);

    }

    @Test
    public void testStringFlatMapToScheduleCharacters() throws Exception {
        when(source.value()).thenReturn(of("abcd"));
        when(em.isFired(source)).thenReturn(true, false, false, false, false);

        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        assertThat(subject.apply()).contains('a');

        verify(em, times(3)).schedule(captor.capture());
        List<Runnable> scheduledRunnables = captor.getAllValues();
        assertThat(scheduledRunnables).hasSize(3);

        scheduledRunnables.get(0).run();
        assertThat(subject.apply()).contains('b');
        scheduledRunnables.get(1).run();
        assertThat(subject.apply()).contains('c');
        scheduledRunnables.get(2).run();
        assertThat(subject.apply()).contains('d');

        assertThat(subject.apply()).isAbsent();

        verify(em, times(3)).fireCycle(host);
    }

    @Test
    public void testEmptyStringFlatMapToNothing() throws Exception {
        when(source.value()).thenReturn(of(""));
        when(em.isFired(source)).thenReturn(true, false);

        assertThat(subject.apply()).isAbsent();
        verify(em, never()).schedule(any(Runnable.class));
        verify(em, never()).scheduleFire(any(Event.class));
        verify(em, never()).fireCycle(any(Event.class));
    }

    @Test
    public void testAbsentFlatMapToNothing() throws Exception {
        when(source.value()).thenReturn(Optional.<String>absent());
        when(em.isFired(source)).thenReturn(false);

        assertThat(subject.apply()).isAbsent();
        verify(em, never()).schedule(any(Runnable.class));
        verify(em, never()).scheduleFire(any(Event.class));
        verify(em, never()).fireCycle(any(Event.class));
    }
}
