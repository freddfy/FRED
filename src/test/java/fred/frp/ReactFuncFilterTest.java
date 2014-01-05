package fred.frp;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
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
public class ReactFuncFilterTest {
    private ReactFuncFilter<Integer> subject;
    private EventReact<Integer> evtVal;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        evtVal = mock(EventReact.class);
        subject = new ReactFuncFilter<Integer>(evtVal, new Predicate<Integer>() {
            @Override
            public boolean apply(Integer input) {
                return input % 2 == 0;
            }
        });
    }

    @Test
    public void testOnlyEvenNumberReturns() throws Exception {
        when(evtVal.value()).thenReturn(of(1)).thenReturn(of(10));

        assertThat(subject.apply()).isAbsent();
        assertThat(subject.apply()).contains(10);

    }

    @Test
    public void testAbsentInputReturnsAbsent() throws Exception {
        when(evtVal.value()).thenReturn(Optional.<Integer>absent());

        assertThat(subject.apply()).isAbsent();
    }
}
