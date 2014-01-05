package fred.frp;

import com.google.common.base.Function;
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
public class ReactFuncMapTest {
    private ReactFuncMap<Integer, String> subject;
    private EventReact<Integer> source;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        source = mock(EventReact.class);
        subject = new ReactFuncMap<Integer, String>(source, new Function<Integer, Optional<String>>() {
            @Override
            public Optional<String> apply(Integer input) {
                return Optional.of(input.toString());
            }
        });
    }

    @Test
    public void testMappingFromIntegerToString() throws Exception {
        when(source.value()).thenReturn(of(1)).thenReturn(of(2));

        assertThat(subject.apply()).contains("1");
        assertThat(subject.apply()).contains("2");

    }

    @Test
    public void testMappingFromAbsentReturnsAbsent() throws Exception {
        when(source.value()).thenReturn(Optional.<Integer>absent());

        assertThat(subject.apply()).isAbsent();

    }
}
