package fred.graph;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 *
 * Author:  Fred Deng
 */
public class GraphTopoSortTest {
    private GraphTopoSort<String> subject;
    private Topology<String> topology;
    private Vertices<String> vertices;
    private VertexConnection<String> vertexConn;
    private Vertex<String> one;
    private Vertex<String> two;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        topology = mock(Topology.class);
        vertices = mock(Vertices.class);
        vertexConn = mock(VertexConnection.class);
        subject = new GraphTopoSort<String>(topology, vertexConn, vertices);

        one = new Vertex<String>("1");
        two = new Vertex<String>("2");
        when(vertices.getOrCreate("1")).thenReturn(one);
        when(vertices.getOrCreate("2")).thenReturn(two);
    }

    @Test
    public void testConnectSingle() throws Exception {
        subject.connect("1");

        verify(vertices).getOrCreate("1");
        verify(vertexConn).connect(one);
    }

    @Test
    public void testConnectTwo() throws Exception {
        subject.connect("1", "2");

        verify(vertices).getOrCreate("1");
        verify(vertices).getOrCreate("2");
        verify(vertexConn).connect(one, two);
    }

    @Test
    public void testDisconnectSingle() throws Exception {
        when(vertices.remove("1")).thenReturn(one);

        subject.disconnect("1");

        verify(vertices).remove("1");
        verify(vertexConn).disconnect(one);
    }

    @Test
    public void testDisconnectTwo() throws Exception {
        when(vertices.get("1")).thenReturn(one);
        when(vertices.get("2")).thenReturn(two);

        subject.disconnect("1", "2");

        verify(vertices).get("1");
        verify(vertices).get("2");
        verify(vertexConn).disconnect(one, two);
    }

    @Test
    public void testNothingHappensWhenDisconnectNonExistSingle() throws Exception {
        subject.disconnect("NotExist");
        verify(vertexConn, never()).disconnect(any(Vertex.class), any(Vertex.class));
    }

    @Test
    public void testNothingHappensWhenDisconnectNonExistSource() throws Exception {
        subject.disconnect("NotExist", "2");
        verify(vertexConn, never()).disconnect(any(Vertex.class), any(Vertex.class));
    }

    @Test
    public void testNothingHappensWhenDisconnectNonExistTarget() throws Exception {
        subject.disconnect("one", "NotExist2");
        verify(vertexConn, never()).disconnect(any(Vertex.class), any(Vertex.class));
    }
}
