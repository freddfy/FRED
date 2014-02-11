package fred;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fred.frp.Function2;
import fred.frp.FunctionAcc;
import fred.sub.SubscribableIterable;
import org.junit.Before;
import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

import static fred.util.Assert.assertReactive;

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

        assertReactive(
                subject.filter(new Predicate<Integer>() {
                    @Override
                    public boolean apply(Integer input) {
                        return input % 2 == 1;
                    }
                })
        ).hasFired(1, 3, 5);
    }

    @Test
    public void testMapToStringExcludingNull() throws Exception {

        assertReactive(
                subject.map(new Function<Integer, Optional<String>>() {
                    @Override
                    public Optional<String> apply(Integer input) {
                        return Optional.of(input.toString());
                    }
                })
        ).hasFired("1", "2", "3", "4", "5");
    }

    @Test
    public void testReduceBySummingUpExcludingNull() throws Exception {

        assertReactive(
                subject.reduce(new FunctionAcc<Integer, Integer>() {
                    @Override
                    public Optional<Integer> apply(Integer last, Integer next) {
                        return Optional.of(last + next);
                    }
                })
        ).hasFired(3, 6, 10, 15);
    }

    @Test
    public void testFoldToStringConcatenationExcludingNull() throws Exception {

        assertReactive(
                subject.fold(new FunctionAcc<Integer, String>() {
                    @Override
                    public Optional<String> apply(String last, Integer next) {
                        return Optional.of(last + next);
                    }
                }, "")
        ).hasFired("1", "12", "123", "1234", "12345");
    }

    @Test
    public void testFlatMapTo1ToInputExclusive() throws Exception {

        assertReactive(
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
        ).hasFired(1, 1, 2, 1, 2, 3, 1, 2, 3, 4);

    }

    @Test
    public void testAsyncEventManager() throws Exception {

        assertReactive(
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
                        })
        ).hasFired(6, 12);

    }

    @Test
    public void testZipEitherWithTheSameEventManagerFromDifferentSources() throws Exception {

        FunctionalReactives<Integer> fr1 = FunctionalReactives.from(4, 5);  //4 is ignored since it is sync event manager
        FunctionalReactives<Integer> fr2 = fr1.fromAnother(new SubscribableIterable<Integer>(5, 3, 2, 1));

        assertReactive(
                fr1.zipEither(fr2, new Function2<Integer, Integer, String>() {
                    @Override
                    public Optional<String> apply(Integer input1, Integer input2) {
                        return Optional.of(input1.toString() + input2.toString());
                    }
                })
        ).hasFired("55", "53", "52", "51");
    }

    @Test
    public void testZipEitherWithTheSameEventManagerFromTheSameSource() throws Exception {

        FunctionalReactives<Integer> fr1 = FunctionalReactives.from(5, 4, 3, 2, 1);
        FunctionalReactives<Integer> fr2 = fr1.filter(new Predicate<Integer>() {
            @Override
            public boolean apply(Integer input) {
                return input % 2 == 1;
            }
        });

        assertReactive(
                fr1.zipEither(fr2, new Function2<Integer, Integer, String>() {
                    @Override
                    public Optional<String> apply(Integer input1, Integer input2) {
                        return Optional.of(input1.toString() + input2.toString());
                    }
                })
        ).hasFired("55", "45", "33", "23", "11");

    }

    @Test
    public void testZipEitherWithDifferentEventManagersFromDifferentSources() throws Exception {
        FunctionalReactives<Integer> fr1 = FunctionalReactives.from(4, 5);  //4 is ignored since it is sync event manager
        FunctionalReactives<Integer> fr2 = FunctionalReactives.from(5, 3, 2, 1);

        assertReactive(
                fr1.zipEither(fr2, new Function2<Integer, Integer, String>() {
                    @Override
                    public Optional<String> apply(Integer input1, Integer input2) {
                        return Optional.of(input1.toString() + input2.toString());
                    }
                })
        ).hasFired("55", "53", "52", "51");

    }

    @Test
    public void testZipStrictWithTheSameEventManagerFromDifferentSources() throws Exception {

        FunctionalReactives<Integer> fr1 = FunctionalReactives.from(4, 5);
        FunctionalReactives<Integer> fr2 = fr1.fromAnother(new SubscribableIterable<Integer>(5, 3, 2, 1));

        assertReactive(
                fr1.zipStrict(fr2, new Function2<Integer, Integer, String>() {
                    @Override
                    public Optional<String> apply(Integer input1, Integer input2) {
                        return Optional.of(input1.toString() + input2.toString());
                    }
                })
        ).hasFired("45", "53");
    }

    @Test
    public void testZipStrictWithTheSameEventManagerFromTheSameSource() throws Exception {

        FunctionalReactives<Integer> fr1 = FunctionalReactives.from(5, 4, 3, 2, 1);
        FunctionalReactives<Integer> fr2 = fr1.filter(new Predicate<Integer>() {
            @Override
            public boolean apply(Integer input) {
                return input % 2 == 1;
            }
        });

        assertReactive(
                fr1.zipStrict(fr2, new Function2<Integer, Integer, String>() {
                    @Override
                    public Optional<String> apply(Integer input1, Integer input2) {
                        return Optional.of(input1.toString() + input2.toString());
                    }
                })
        ).hasFired("55", "43", "31");

    }

    @Test
    public void testZipStrictWithDifferentEventManagerFromDifferentSources() throws Exception {

        FunctionalReactives<Integer> fr1 = FunctionalReactives.from(4, 5);
        FunctionalReactives<Integer> fr2 = FunctionalReactives.from(5, 3, 2, 1);

        assertReactive(
                fr1.zipStrict(fr2, new Function2<Integer, Integer, String>() {
                    @Override
                    public Optional<String> apply(Integer input1, Integer input2) {
                        return Optional.of(input1.toString() + input2.toString());
                    }
                })
        ).hasFired("45", "53");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBufferBySize() throws Exception {
        assertReactive(subject.bufferBySize(2))
                .hasFired(ImmutableList.of(1, 2),
                        ImmutableList.of(3, 4)
                );

    }

    @Test(expected = UnsupportedOperationException.class)
    public void testBufferByTimeWithTheSyncEventManagerWillThrowException() throws Exception {
        subject.bufferByTime(0, 1, TimeUnit.SECONDS);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBufferByTimeWithDelayLongerThanTestShouldFireNothing() throws Exception {
        assertReactive(
                FunctionalReactives.fromAsync(1, 2, 3, 4, 5)
                        .bufferByTime(1000, 1, TimeUnit.MILLISECONDS)
        ).hasFired();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBufferByTimeWithZeroInitialDelayShouldBeFastEnoughToFireSomething() throws Exception {
        assertReactive(
                FunctionalReactives.fromAsync(1, 2, 3, 4, 5)
                        .bufferByTime(0, 1000, TimeUnit.MILLISECONDS)
        ).hasFired(ImmutableList.of(1, 2, 3, 4, 5));

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBufferByFlushSource() throws Exception {
        FunctionalReactives<Integer> fireOnlyOddNumber =
                subject.filter(new Predicate<Integer>() {
                    @Override
                    public boolean apply(Integer input) {
                        return input % 2 == 1;
                    }
                });

        assertReactive(
                subject.bufferByFlush(fireOnlyOddNumber)
        ).hasFired(ImmutableList.of(1), ImmutableList.of(2, 3), ImmutableList.of(4, 5));

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSlideBySize() throws Exception {

        assertReactive(
                subject.slideBySize(3)
        ).hasFired(
                ImmutableList.of(1),
                ImmutableList.of(1, 2),
                ImmutableList.of(1, 2, 3),
                ImmutableList.of(2, 3, 4),
                ImmutableList.of(3, 4, 5)
        );
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAsyncSourceSlideByTime() throws Exception {
        assertReactive(
                FunctionalReactives.fromAsync(1, 2, 3)
                        .slideByTime(2, TimeUnit.MILLISECONDS)  //1st ms: [1,2,3]; 2nd ms: []
        ).waitFor(10)
        .hasFired(
                ImmutableList.of(1, 2, 3), //first ms buffer
                ImmutableList.of(1, 2, 3)  //first ms buffer + empty 2nd second buffer
        );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testSlideByTimeInTheSameThreadWillThrowUnsupportedException() throws Exception {
        subject.slideByTime(1, TimeUnit.SECONDS);
    }
}
