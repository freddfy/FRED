package fred.graph;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Vertex cache by the data. It assumes the data T is compatible key of a hash table.
 *
 * Author:  Fred Deng
 */
public class VerticesImpl<T> implements Vertices<T> {
    private final LoadingCache<T, Vertex<T>> vertices = CacheBuilder.newBuilder().build(new CacheLoader<T, Vertex<T>>() {
        @Override
        public Vertex<T> load(T key) throws Exception {
            return new Vertex<T>(key);
        }
    });

    @Override
    public Vertex<T> getOrCreate(T value) {
        return value == null ? null : vertices.getUnchecked(value);
    }

    @Override
    public Vertex<T> get(T value) {
        return vertices.asMap().get(value);
    }

    @Override
    public Vertex<T> remove(T value) {
        return vertices.asMap().remove(value);
    }
}
