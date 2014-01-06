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
 * Author: Fred Deng
 */
public class ReactFuncZipEitherTest {
    private ReactFuncZipEither<Integer, Integer, String> subject;
    private EventReact<Integer> input1;
    private EventReact<Integer> input2;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        input1 = mock(EventReact.class);
        input2 = mock(EventReact.class);
        subject = new ReactFuncZipEither<Integer, Integer, String>(input1, input2,
                new Function2<Integer, Integer, String>() {
                    @Override
                    public Optional<String> apply(Integer input1, Integer input2) {
                        return of(input1.toString() + input2.toString());
                    }
                });
    }

    @Test
    public void testEveryFireWillCalculateBasedOnInputIgnoringAbsent() throws Exception {
        when(input1.value()).thenReturn(of(1)).thenReturn(of(1)).thenReturn(of(2));
        when(input2.value()).thenReturn(of(1)).thenReturn(of(2)).thenReturn(Optional.<Integer>absent());

        assertThat(subject.apply()).contains("11");
        assertThat(subject.apply()).contains("12");
        assertThat(subject.apply()).isAbsent();
    }



}
