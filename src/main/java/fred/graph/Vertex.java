package fred.graph;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Vertex with incoming and outgoing vertex edge implemented using Set.
 *
 * Author:  Fred Deng
 */
public class Vertex<T> {

    private final T value;
    private Set<Vertex<T>> incoming = Sets.newHashSet();
    private Set<Vertex<T>> outgoing = Sets.newHashSet();

    public Vertex(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public Set<Vertex<T>> incomingVertices(){
        return incoming;
    }

    public Set<Vertex<T>> outgoingVertices(){
        return outgoing;
    }

    public boolean connectDependant(Vertex<T> dependant) {
        outgoing.add(dependant);
        dependant.incoming.add(this);
        return true;
    }

    public void disconnectDependant(Vertex<T> dependant) {
        outgoing.remove(dependant);
        dependant.incoming.remove(this);
    }

    public void disconnectAll() {
        for (Vertex<T> inc : incoming) {
            inc.outgoing.remove(this);
        }

        for (Vertex<T> out : outgoing) {
            out.incoming.remove(this);
        }

        incoming.clear();
        outgoing.clear();
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "value=" + value +
                '}';
    }
}
