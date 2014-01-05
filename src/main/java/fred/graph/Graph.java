package fred.graph;

import java.util.Iterator;

/**
 * A Directed Acyclic Graph interface that allow for traversal all the vertexes starting from source.
 *
 * Author:  Fred Deng
 */
public interface Graph<T> extends Connection<T> {

    /**
     * Iterating all the vertexes starting from source. Invocation of remove implies a particular vertex is
     * not triggered and therefore all its sole dependants should be removed from the iteration.
     */
    Iterator<T> iterator(T source);
}
