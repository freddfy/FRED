package fred.frp;

/**
 * Commonly used FunctionVoids.
 *
 * Author:  Fred Deng
 */
public class FunctionVoids {
    public static <T> FunctionVoid<T> println(){
        return new FunctionVoid<T>() {
            @Override
            public void apply(T next) {
                System.out.println(next);
            }
        };
    }
}
