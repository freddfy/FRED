package fred.frp;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.guava.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

/**
 * Author:  Fred Deng
 */
public class EventReactConstantTest {
    private static final String CONSTANT = "CONSTANT";
    private EventReactConstant<String> subject;

    @Before
    public void setUp() throws Exception {
        subject = new EventReactConstant<String>(CONSTANT);
    }

    @Test
    public void testConstantNeverFires() throws Exception {
        assertFalse(subject.fired());
        assertFalse(subject.fired());
        assertFalse(subject.fired());
    }

    @Test
    public void testConstantNeverChanged() throws Exception {
        assertThat(subject.value()).contains(CONSTANT);

        assertFalse(subject.fired());

        assertThat(subject.value()).contains(CONSTANT);
    }
}
