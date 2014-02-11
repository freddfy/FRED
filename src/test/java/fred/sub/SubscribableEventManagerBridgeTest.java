package fred.sub;

import fred.FunctionalReactives;
import fred.event.Schedulable;
import fred.frp.FunctionVoid;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Author:  Fred Deng
 */
public class SubscribableEventManagerBridgeTest {
    private SubscribableEventManagerBridge<Integer> subject;
    private FunctionalReactives<Integer> origSource;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        origSource = mock(FunctionalReactives.class);
        subject = new SubscribableEventManagerBridge<Integer>(origSource);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testOrigSourceWillTriggerSchedulable() throws Exception {
        Schedulable<Integer> schedulable = mock(Schedulable.class);

        subject.doSubscribe(schedulable);

        ArgumentCaptor<FunctionVoid> captor = ArgumentCaptor.forClass(FunctionVoid.class);

        verify(origSource).forEach(captor.capture());

        FunctionVoid<Integer> function = captor.getValue();

        verify(schedulable, never()).schedule(anyInt());

        function.apply(1);
        verify(schedulable).schedule(1);
        function.apply(2);
        verify(schedulable).schedule(2);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnsubscribeWillDetachOrigSource() throws Exception {
        Schedulable<Integer> schedulable = mock(Schedulable.class);

        FunctionalReactives<Void> subscription = mock(FunctionalReactives.class);
        when(origSource.forEach(any(FunctionVoid.class))).thenReturn(subscription);

        subject.doSubscribe(schedulable);

        subject.unsubscribe();

        verify(subscription).detach();

    }
}
