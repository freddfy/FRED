package fred.graph;

import java.util.Set;

/**
 *
 * Author:  Fred Deng
 */
public interface VertexConnection<T> extends Connection<Vertex<T>> {

    /**
     * All the dependants of a vertex.
     */
    Set<Vertex<T>> dependants(Vertex<T> vertex);
}
