package fred.util;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import fred.FunctionalReactives;
import fred.frp.FunctionVoid;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A tool for accumulating all results for later verification.
 *
 * Author:  Fred Deng
 */
public class Assert<T> implements FunctionVoid<T> {
    private final FunctionalReactives<T> verifiable;
    private final List<T> results = Lists.newCopyOnWriteArrayList();
    private long timeInMs = 0;

    public static <T> Assert<T> assertReactive(FunctionalReactives<T> verifiable){
        return new Assert<T>(verifiable);
    }

    public Assert(FunctionalReactives<T> verifiable) {
        this.verifiable = verifiable;
    }

    @Override
    public void apply(T input) {
        results.add(input);
    }

    public void hasFired(T... expResult) {
        verifiable.forEach(this).start();

        try {
            Thread.sleep(timeInMs);
        } catch (InterruptedException e) {
            Throwables.propagate(e);
        }

        verifiable.shutdown();

        assertThat(results).containsExactly(expResult);
    }

    public Assert waitFor(int ms) {
        timeInMs = ms;
        return this;
    }
}
