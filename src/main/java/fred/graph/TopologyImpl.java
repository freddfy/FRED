package fred.graph;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 *
 * Author:  Fred Deng
 */
public class TopologyImpl<T> implements Topology<T> {

    @Override
    public Iterator<T> sort(Vertex<T> source, Set<Vertex<T>> toSort) {

        return new TopologyIterator<T>(source, toSort);
    }

    private class TopologyIterator<T> implements Iterator<T>{
        private final Vertex<T> NOT_FOUND = new Vertex<T>(null);

        private final Set<Vertex<T>> rest;
        private final Set<Vertex<T>> consumed = Sets.newHashSet();
        private Vertex<T> lastConsumed = null;
        private Vertex<T> nextReady;

        private TopologyIterator(Vertex<T> source, Set<Vertex<T>> dependants) {
            this.nextReady = source;
            this.rest = Sets.newHashSet(dependants);
        }

        @Override
        public boolean hasNext() {
            return nextVertex() != null && nextReady != NOT_FOUND;
        }

        @Override
        public T next() {
            if (nextReady == null) {
                nextVertex();
            }

            rest.remove(nextReady);
            consumed.add(nextReady);

            lastConsumed = nextReady;
            nextReady = null;
            return lastConsumed.getValue();
        }

        private Vertex<T> nextVertex(){
            if (nextReady != null) {
                return nextReady;
            }

            try {
                nextReady = Iterables.find(rest, new Predicate<Vertex<T>>() {
                    @Override
                    public boolean apply(Vertex<T> candidate) {
                        return hasIncomingInConsumed(candidate) && hasNoIncomingInRest(candidate);
                    }
                });
            } catch (NoSuchElementException ex) {
                nextReady = NOT_FOUND;
            }

            return nextReady;

        }

        private boolean hasIncomingInConsumed(Vertex<T> candidate) {
            return Iterables.any(candidate.incomingVertices(), new Predicate<Vertex<T>>() {
                @Override
                public boolean apply(Vertex<T> incoming) {
                    return consumed.contains(incoming);
                }
            });
        }

        private boolean hasNoIncomingInRest(Vertex<T> candidate) {
            return Iterables.all(candidate.incomingVertices(), new Predicate<Vertex<T>>() {
                @Override
                public boolean apply(Vertex<T> incoming) {
                    return !rest.contains(incoming);
                }
            });
        }

        @Override
        public void remove() {
            consumed.remove(lastConsumed);
        }

        @Override
        public String toString() {
            return "TopologyIterator{" +
                    "nextReady=" + nextReady +
                    ", lastConsumed=" + lastConsumed +
                    ", rest=" + rest +
                    ", consumed=" + consumed +
                    '}';
        }
    }
}
