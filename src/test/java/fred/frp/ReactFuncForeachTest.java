package fred.frp;

import com.google.common.base.Optional;
import fred.event.EventReact;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static com.google.common.base.Optional.of;
import static org.assertj.guava.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 *
 * Author:  Fred Deng
 */
public class ReactFuncForeachTest {
    private ReactFuncForeach<Integer> subject;
    private EventReact<Integer> source;
    private FunctionVoid<Integer> function;
    private InOrder inOrder;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        source = mock(EventReact.class);
        function = mock(FunctionVoid.class);
        inOrder = inOrder(function);
        subject = new ReactFuncForeach<Integer>(source, function);

    }

    @Test
    public void testApplyInOrder() throws Exception {
        when(source.value()).thenReturn(of(1)).thenReturn(of(2));

        assertThat(subject.apply()).isAbsent();
        assertThat(subject.apply()).isAbsent();

        inOrder.verify(function).apply(1);
        inOrder.verify(function).apply(2);
    }

    @Test
    public void testAbsentInputWillReturnsNull() throws Exception {
        when(source.value()).thenReturn(Optional.<Integer>absent());

        assertThat(subject.apply()).isAbsent();
    }
}
