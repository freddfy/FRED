package fred.graph;

import java.util.Iterator;

/**
 * Graph traversal based on topology sort.
 *
 * Author:  Fred Deng
 */
public class GraphTopoSort<T> implements Graph<T> {

    private final Topology<T> topology;
    private final Vertices<T> vertices;
    private final VertexConnection<T> verticesConn;

    public GraphTopoSort(Topology<T> topology, VertexConnection<T> verticesConn, Vertices<T> vertices) {
        this.topology = topology;
        this.verticesConn = verticesConn;
        this.vertices = vertices;
    }

    @Override
    public Iterator<T> iterator(T source) {
        Vertex<T> srcVertex = vertices.get(source);
        return topology.sort(srcVertex, verticesConn.dependants(srcVertex));
    }

    @Override
    public void connect(T source) {
        verticesConn.connect(vertices.getOrCreate(source));
    }

    @Override
    public void connect(final T source, final T target) {
        Vertex<T> srcVertex = vertices.getOrCreate(source);
        Vertex<T> tarVertex = vertices.getOrCreate(target);
        verticesConn.connect(srcVertex, tarVertex);
    }

    @Override
    public void disconnect(T source, T target) {
        Vertex<T> srcVertex = vertices.get(source);
        if (srcVertex != null) {
            Vertex<T> tarVertex = vertices.get(target);
            if (tarVertex != null) {
                verticesConn.disconnect(srcVertex, tarVertex);
            }
        }
    }

    @Override
    public void disconnect(T target) {
        Vertex<T> removed = vertices.remove(target);
        if (removed != null) {
            verticesConn.disconnect(removed);
        }
    }
}
