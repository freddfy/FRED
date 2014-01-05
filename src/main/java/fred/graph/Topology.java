package fred.graph;

import java.util.Iterator;
import java.util.Set;

/**
 *
 * Author:  Fred Deng
 */
public interface Topology<T>{
    /**
     *
     * @param source the source to start from
     * @param toSort the rest dependants to topology sort
     * @return An iterator allows to traversal all the vertex in an topology sorted order. Iterator.remove() indicates
     * the vertex is not triggered and therefore all its only dependants should not be iterated as well.
     */
    Iterator<T> sort(Vertex<T> source, Set<Vertex<T>> toSort);
}
