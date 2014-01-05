package fred.graph;

/**
 * Indicates there is a cycle in graph which is not allowed in event propagation.
 *
 * Author:  Fred Deng
 */
public class CycleException extends RuntimeException {
    public CycleException(String message) {
        super(message);
    }
}
