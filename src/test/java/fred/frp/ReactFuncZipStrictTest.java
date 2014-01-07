package fred.frp;

import com.google.common.base.Optional;
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
public class ReactFuncZipStrictTest {
    private ReactFuncZipStrict<Integer, Integer, String> subject;
    private EventReact<Integer> input1;
    private EventReact<Integer> input2;
    private EventManager em;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        input1 = mock(EventReact.class);
        input2 = mock(EventReact.class);
        em = mock(EventManager.class);
        subject = new ReactFuncZipStrict<Integer, Integer, String>(input1, input2,
                new Function2<Integer, Integer, String>() {
                    @Override
                    public Optional<String> apply(Integer input1, Integer input2) {
                        return of(input1.toString() + input2.toString());
                    }
                }, em);
    }

    @Test
    public void testStrictlyMatchInputPairToApplyFunction() throws Exception {
        when(input1.value()).thenReturn(of(1)).thenReturn(of(2));
        when(input2.value()).thenReturn(of(2)).thenReturn(of(3));
        when(em.isFired(input1)).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false);
        when(em.isFired(input2)).thenReturn(false).thenReturn(true).thenReturn(true).thenReturn(false);

        assertThat(subject.apply()).isAbsent();
        assertThat(subject.apply()).contains("12");
        assertThat(subject.apply()).contains("23");
        assertThat(subject.apply()).isAbsent();
    }

    @Test
    public void testFilterAbsentWhenApplyingFunction() throws Exception {
        when(input1.value()).thenReturn(Optional.<Integer>absent()).thenReturn(of(2));
        when(input2.value()).thenReturn(of(1)).thenReturn(of(3));

        when(em.isFired(input1)).thenReturn(true).thenReturn(true).thenReturn(false);
        when(em.isFired(input2)).thenReturn(true).thenReturn(true).thenReturn(false);

        assertThat(subject.apply()).isAbsent();
        assertThat(subject.apply()).contains("21");
        assertThat(subject.apply()).isAbsent();


    }
}
