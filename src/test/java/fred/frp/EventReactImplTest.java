package fred.frp;

import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.base.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.guava.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * Author:  Fred Deng
 */
public class EventReactImplTest {
    private EventReactImpl subject;
    private ReactFunc<String> reactFunc;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        reactFunc = mock(ReactFunc.class);
        subject = new EventReactImpl(reactFunc);

    }

    @Test
    public void testFiredOnlyWhenFunctionNotReturningNull() throws Exception {
        when(reactFunc.apply()).thenReturn(of("Result1")).thenReturn(of("Result2")).thenReturn(Optional.<String>absent());

        assertThat(subject.tryFire(1)).isTrue();
        assertThat(subject.value()).contains("Result1");

        assertThat(subject.tryFire(2)).isTrue();
        assertThat(subject.value()).contains("Result2");

        assertThat(subject.tryFire(3)).isFalse();
        assertThat(subject.value()).contains("Result2"); //remains unchanged

    }

}
