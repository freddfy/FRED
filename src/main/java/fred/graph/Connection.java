package fred.graph;

/**
 * Interface for managing event graph connections.
 *
 * Author:  Fred Deng
 */
public interface Connection<T> {

    void connect(T source);

    void connect(T source, T target);

    void disconnect(T source, T target);

    void disconnect(T target);

}
