package fred;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import fred.frp.FunctionAcc;
import fred.frp.FunctionVoid;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Author: Fred Deng
 */
public class FunctionalReactivesTest {

    private FunctionalReactives<Integer> subject;

    @Before
    public void setUp() throws Exception {
        subject = FunctionalReactives.from(1, 2, null, 3, 4, 5);
    }

    @Test
    public void testFilterEvenAndNull() throws Exception {
        Verify<Integer> verify = new Verify<Integer>();

        subject.filter(new Predicate<Integer>() {
            @Override
            public boolean apply(Integer input) {
                return input % 2 == 1;
            }
        })
                .forEach(verify)
                .start();

        verify.assertResult(1, 3, 5);
    }

    @Test
    public void testMapToStringExcludingNull() throws Exception {
        Verify<String> verify = new Verify<String>();

        subject.map(new Function<Integer, Optional<String>>() {
            @Override
            public Optional<String> apply(Integer input) {
                return Optional.of(input.toString());
            }
        })
                .forEach(verify)
                .start();

        verify.assertResult("1", "2", "3", "4", "5");
    }

    @Test
    public void testReduceBySummingUpExcludingNull() throws Exception {
        Verify<Integer> verify = new Verify<Integer>();

        subject.reduce(new FunctionAcc<Integer, Integer>() {
            @Override
            public Optional<Integer> apply(Integer last, Integer next) {
                return Optional.of(last + next);
            }
        })
                .forEach(verify)
                .start();

        verify.assertResult(3, 6, 10, 15);
    }

    @Test
    public void testFoldToStringConcatenationExcludingNull() throws Exception {
        Verify<String> verify = new Verify<String>();

        subject.fold(new FunctionAcc<Integer, String>() {
            @Override
            public Optional<String> apply(String last, Integer next) {
                return Optional.of(last + next);
            }
        }, "")
                .forEach(verify)
                .start();

        verify.assertResult("1", "12", "123", "1234", "12345");

    }

    @Test
    public void testFlatMapTo1ToInputExclusive() throws Exception {
        Verify<Integer> verify = new Verify<Integer>();

        subject.flatMap(new Function<Integer, Optional<Queue<Integer>>>() {
            @Override
            public Optional<Queue<Integer>> apply(Integer input) {
                Queue<Integer> result = Lists.newLinkedList();
                for (int i = 1; i < input; i++) {
                    result.offer(i);
                }
                return Optional.of(result);
            }
        })
                .forEach(verify)
                .start();

        verify.assertResult(1, 1, 2, 1, 2, 3, 1, 2, 3, 4);

    }

    @Test
    public void testAsyncEventManager() throws Exception {
        Verify<Integer> verify = new Verify<Integer>();

        subject = FunctionalReactives.fromAsync(1, 2, 3, 4, 5)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean apply(Integer input) {
                        return input % 2 == 1;
                    }
                })
                .map(new Function<Integer, Optional<Integer>>() {
                    @Override
                    public Optional<Integer> apply(Integer input) {
                        return Optional.of(input + 1);
                    }
                })
                .reduce(new FunctionAcc<Integer, Integer>() {
                    @Override
                    public Optional<Integer> apply(Integer acc, Integer next) {
                        return Optional.of(acc + next);
                    }
                });

        subject.forEach(verify).start();

        subject.shutdown();

        verify.assertResult(6, 12);
    }

    private static class Verify<T> implements FunctionVoid<T> {
        private List<T> results = Lists.newArrayList();

        @Override
        public void apply(T input) {
            results.add(input);
        }

        public void assertResult(T... expResult) {
            assertThat(results).containsExactly(expResult);
        }
    }
}
