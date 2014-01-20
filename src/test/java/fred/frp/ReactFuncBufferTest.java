package fred.frp;

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
public class ReactFuncBufferTest {
    private ReactFuncBuffer<String> subject;
    private EventManager em;
    private EventReact<String> source;
    private EventReact<Integer> size;
    private Event flush;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        em = mock(EventManager.class);
        source = mock(EventReact.class);
        size = mock(EventReact.class);
        flush = mock(Event.class);
        subject = new ReactFuncBuffer<String>(em, source, size, flush);
    }

    @Test
    public void testBufferExceedsLimitWillFlushEventsRegardlessOfFlush() throws Exception {
        when(em.isFired(flush)).thenReturn(false);
        when(size.value()).thenReturn(of(3));
        when(source.value()).thenReturn(of("1"));
        when(em.isFired(source)).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);

        assertThat(subject.apply()).isAbsent();
        assertThat(subject.apply()).isAbsent();
        assertThat(subject.apply()).contains(ImmutableList.of("1", "1", "1"));
        assertThat(subject.apply()).isAbsent();
    }

    @Test
    public void testFlushSourceReactedWillFlushRegardlessOfBufferedItems() throws Exception {
        when(em.isFired(flush)).thenReturn(true);
        when(size.value()).thenReturn(of(3));
        when(source.value()).thenReturn(of("1")).thenReturn(of("2"));
        when(em.isFired(source)).thenReturn(true).thenReturn(false).thenReturn(true);

        assertThat(subject.apply()).contains(ImmutableList.of("1"));
        assertThat(subject.apply()).contains(ImmutableList.of());
        assertThat(subject.apply()).contains(ImmutableList.of("2"));
    }

    @Test
    public void testWhenSizeReducedToExceedBufferWillFlush() throws Exception {
        when(em.isFired(flush)).thenReturn(false);
        when(size.value()).thenReturn(of(3)).thenReturn(of(1));
        when(source.value()).thenReturn(of("1")).thenReturn(of("2")).thenReturn(of("3"));
        when(em.isFired(source)).thenReturn(true);

        assertThat(subject.apply()).isAbsent();
        assertThat(subject.apply()).contains(ImmutableList.of("1", "2")); //2 > limit 1 so flushed 2
        assertThat(subject.apply()).contains(ImmutableList.of("3"));
    }
}
