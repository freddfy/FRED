package fred.graph;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * Author:  Fred Deng
 */
public class VertexConnectionImplTest {
    private Vertex<Integer> one;
    private Vertex<Integer> two;
    private Vertex<Integer> three;
    private Vertex<Integer> four;
    private VertexConnectionImpl<Integer> subject;


    @Before
    public void setUp() throws Exception {
        one = new Vertex<Integer>(1);
        two = new Vertex<Integer>(2);
        three = new Vertex<Integer>(3);
        four = new Vertex<Integer>(4);
        subject = new VertexConnectionImpl<Integer>();
    }

    @Test
    public void testSelfDependantIfConnectSingle() throws Exception {
        subject.connect(one);

        assertThat(subject.dependants(one)).isEmpty();
        assertThat(one.incomingVertices()).isEmpty();
    }

    @Test
    public void testDependantsWhenConnectTwo() throws Exception {
        subject.connect(one, two);

        assertThat(subject.dependants(one)).containsOnly(two);
        assertThat(one.outgoingVertices()).containsOnly(two);
        assertThat(subject.dependants(two)).isEmpty();
        assertThat(two.incomingVertices()).containsOnly(one);
    }

    @Test
    public void testChainDependants() throws Exception {
        subject.connect(one, two);
        subject.connect(two, three);
        subject.connect(three, four);

        assertThat(subject.dependants(one)).containsOnly(two, three, four);
        assertThat(subject.dependants(two)).containsOnly(three, four);
        assertThat(subject.dependants(three)).containsOnly(four);
        assertThat(subject.dependants(four)).isEmpty();

        assertThat(one.outgoingVertices()).containsOnly(two);
        assertThat(one.incomingVertices()).isEmpty();
        assertThat(two.outgoingVertices()).containsOnly(three);
        assertThat(two.incomingVertices()).containsOnly(one);
        assertThat(three.incomingVertices()).containsOnly(two);
        assertThat(three.outgoingVertices()).containsOnly(four);
        assertThat(four.incomingVertices()).containsOnly(three);
        assertThat(four.outgoingVertices()).isEmpty();
    }

    @Test
    public void testTriangleDependants() throws Exception {
        subject.connect(one, two);
        subject.connect(two, three);
        subject.connect(one, three);

        assertThat(subject.dependants(one)).contains(two, three);
        assertThat(subject.dependants(two)).containsOnly(three);
        assertThat(subject.dependants(three)).isEmpty();

        assertThat(one.outgoingVertices()).containsOnly(two, three);
        assertThat(one.incomingVertices()).isEmpty();
        assertThat(two.outgoingVertices()).containsOnly(three);
        assertThat(two.incomingVertices()).containsOnly(one);
        assertThat(three.incomingVertices()).containsOnly(one, two);
        assertThat(three.outgoingVertices()).isEmpty();
    }

    @Test
    public void testDiamondDependencies() throws Exception {
        subject.connect(one, two);
        subject.connect(two, four);
        subject.connect(one, three);
        subject.connect(two, three);
        subject.connect(three, four);

        assertThat(subject.dependants(one)).containsOnly(two, three, four);
        assertThat(subject.dependants(two)).containsOnly(three, four);
        assertThat(subject.dependants(three)).containsOnly(four);
        assertThat(subject.dependants(four)).isEmpty();

        assertThat(one.outgoingVertices()).containsOnly(two, three);
        assertThat(one.incomingVertices()).isEmpty();
        assertThat(two.outgoingVertices()).containsOnly(three, four);
        assertThat(two.incomingVertices()).containsOnly(one);
        assertThat(three.incomingVertices()).containsOnly(one, two);
        assertThat(three.outgoingVertices()).containsOnly(four);
        assertThat(four.incomingVertices()).containsOnly(two, three);
        assertThat(four.outgoingVertices()).isEmpty();

    }

    @Test
    public void testChainDependantsDisconnectSingle() throws Exception {
        subject.connect(one, two);
        subject.connect(two, three);
        subject.disconnect(two);

        assertThat(subject.dependants(one)).isEmpty();
        assertThat(subject.dependants(two)).isEmpty();
        assertThat(subject.dependants(three)).isEmpty();

        assertThat(one.incomingVertices()).isEmpty();
        assertThat(one.outgoingVertices()).isEmpty();
        assertThat(two.incomingVertices()).isEmpty();
        assertThat(two.outgoingVertices()).isEmpty();
        assertThat(three.incomingVertices()).isEmpty();
        assertThat(three.outgoingVertices()).isEmpty();
    }

    @Test
    public void testTriangleDependantsDisconnectSingle() throws Exception {
        subject.connect(one, two);
        subject.connect(two, three);
        subject.connect(one, three);

        subject.disconnect(two);

        assertThat(subject.dependants(one)).containsOnly(three);
        assertThat(subject.dependants(two)).isEmpty();
        assertThat(subject.dependants(three)).isEmpty();

        assertThat(one.incomingVertices()).isEmpty();
        assertThat(one.outgoingVertices()).containsOnly(three);
        assertThat(two.incomingVertices()).isEmpty();
        assertThat(two.outgoingVertices()).isEmpty();
        assertThat(three.incomingVertices()).containsOnly(one);
        assertThat(three.outgoingVertices()).isEmpty();

    }

    @Test
    public void testChainDependantsDisconnectTwo() throws Exception {
        subject.connect(one, two);
        subject.connect(two, three);
        subject.connect(three, four);

        subject.disconnect(two, three);

        assertThat(subject.dependants(one)).containsOnly(two);
        assertThat(subject.dependants(two)).isEmpty();
        assertThat(subject.dependants(three)).containsOnly(four);
        assertThat(subject.dependants(four)).isEmpty();

        assertThat(one.incomingVertices()).isEmpty();
        assertThat(one.outgoingVertices()).containsOnly(two);
        assertThat(two.incomingVertices()).containsOnly(one);
        assertThat(two.outgoingVertices()).isEmpty();
        assertThat(three.incomingVertices()).isEmpty();
        assertThat(three.outgoingVertices()).containsOnly(four);
        assertThat(four.incomingVertices()).containsOnly(three);
        assertThat(four.outgoingVertices()).isEmpty();

    }

    @Test(expected = CycleException.class)
    public void testExceptionWhenConnectCycle() throws Exception {
        subject.connect(one, two);
        subject.connect(two, three);
        subject.connect(three, one);

    }

    @Test
    public void testNothingHappensWhenConnectSelf() throws Exception {
        subject.connect(one, one);

        assertThat(subject.dependants(one)).isEmpty();
    }

    @Test
    public void testNothingHappensWhenDisconnectSelf() throws Exception {
        subject.connect(one, two);
        subject.disconnect(one, one);

        assertThat(subject.dependants(one)).containsOnly(two);
        assertThat(one.outgoingVertices()).containsOnly(two);
        assertThat(subject.dependants(two)).isEmpty();
        assertThat(two.incomingVertices()).containsOnly(one);
    }
}
