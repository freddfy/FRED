package fred.graph;

/**
 * Vertex cache by the data.
 *
 * Author:  Fred Deng
 */
public interface Vertices<T> {
    Vertex<T> getOrCreate(T value);

    Vertex<T> get(T value);

    Vertex<T> remove(T value);
}
