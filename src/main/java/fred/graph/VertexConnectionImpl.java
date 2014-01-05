package fred.graph;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collections;
import java.util.Set;

/**
 * Author: Fred Deng
 */
public class VertexConnectionImpl<T> implements VertexConnection<T> {

    @Override
    public Set<Vertex<T>> dependants(Vertex<T> vertex) {
        if (vertex == null) {
            return Collections.emptySet();
        }

        return dependantIterable(vertex).toSet();
    }

    //it is not recursive call, so NO increasing stacks as number increased,
    // thanks to the lazy invocation nature in FluentIterable.
    private FluentIterable<Vertex<T>> dependantIterable(Vertex<T> vertex) {
        return FluentIterable.from(vertex.outgoingVertices()).transformAndConcat(
                new Function<Vertex<T>, Iterable<Vertex<T>>>() {
                    @Override
                    public Iterable<Vertex<T>> apply(Vertex<T> input) {
                        return Iterables.concat(ImmutableList.of(input), dependantIterable(input));
                    }
                });
    }


    @Override
    public void connect(Vertex<T> source) {
    }

    @Override
    public void connect(Vertex<T> source, Vertex<T> target) {
        if (dependantIterable(target).contains(source)) {
            throw new CycleException("Cycle found for adding " + source + " -> " + target);
        }

        if (!source.equals(target)) {
            source.connectDependant(target);
        }
    }

    @Override
    public void disconnect(Vertex<T> source, Vertex<T> target) {
        source.disconnectDependant(target);
    }

    @Override
    public void disconnect(Vertex<T> vertex) {
        vertex.disconnectAll();
    }
}
