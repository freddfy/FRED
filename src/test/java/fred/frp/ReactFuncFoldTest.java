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
public class ReactFuncFoldTest {
    private ReactFuncFold<Integer, String> subject;
    private EventReact<Integer> source;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        source = mock(EventReact.class);
        subject = new ReactFuncFold<Integer, String>(source, new FunctionAcc<Integer, String>() {
            @Override
            public Optional<String> apply(String last, Integer next) {
                return Optional.of(last + next);
            }
        }, "");
    }

    @Test
    public void testMapToStringAndConcatenation() throws Exception {
        when(source.value()).thenReturn(of(1)).thenReturn(of(2)).thenReturn(of(3));

        assertThat(subject.apply()).contains("1");
        assertThat(subject.apply()).contains("12");
        assertThat(subject.apply()).contains("123");
    }

    @Test
    public void testAbsentIsIgnored() throws Exception {
        when(source.value()).thenReturn(Optional.<Integer>absent()).thenReturn(of(1)).thenReturn(of(2)).thenReturn(Optional.<Integer>absent());

        assertThat(subject.apply()).isAbsent();
        assertThat(subject.apply()).contains("1");
        assertThat(subject.apply()).contains("12");
        assertThat(subject.apply()).isAbsent();
    }

    @Test(expected=NullPointerException.class)
    @SuppressWarnings("unchecked")
    public void testRuntimeExceptionIfInitValueSetToNull() throws Exception {
        subject = new ReactFuncFold<Integer, String>(source, mock(FunctionAcc.class), null);
    }
}
