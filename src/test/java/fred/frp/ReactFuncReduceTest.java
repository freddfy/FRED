package fred.frp;

import com.google.common.base.Optional;
import fred.event.EventReact;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.base.Optional.of;
import static org.assertj.guava.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * Author:  Fred Deng
 */
public class ReactFuncReduceTest {
    private ReactFuncReduce<Integer> subject;
    private EventReact<Integer> source;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        source = mock(EventReact.class);
        subject = new ReactFuncReduce<Integer>(source, new FunctionAcc<Integer, Integer>() {
            @Override
            public Optional<Integer> apply(Integer last, Integer next) {
                return Optional.of(last + next);
            }
        });

    }

    @Test
    public void testSum() throws Exception {
        when(source.value()).thenReturn(of(1)).thenReturn(of(2)).thenReturn(of(3));

        assertThat(subject.apply()).isAbsent();
        assertThat(subject.apply()).contains(3);
        assertThat(subject.apply()).contains(6);
    }

    @Test
    public void testSumWithAbsent() throws Exception {
        when(source.value()).thenReturn(Optional.<Integer>absent()).thenReturn(of(1)).thenReturn(of(2)).thenReturn(Optional.<Integer>absent());

        assertThat(subject.apply()).isAbsent();
        assertThat(subject.apply()).isAbsent();
        assertThat(subject.apply()).contains(3);
        assertThat(subject.apply()).isAbsent();
    }
}
