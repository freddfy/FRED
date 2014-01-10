package fred.example;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import fred.FunctionalReactives;
import fred.event.Schedulable;
import fred.frp.FunctionAcc;
import fred.sub.Subscribable;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static fred.frp.FunctionVoids.println;

/**
 * The Usage example implemented as a JUnit test cases for easy run.
 *
 * Author:  Fred Deng
 */
public class FunctionalReactiveExamples {

    /**
     * This example demonstrate the fluent API for chaining reactions.
     * <p/>
     * The higher order function namings are similar to those in Scala.
     */
    @Test
    public void testChainingReactions() throws Exception {
        FunctionalReactives.from(1, 2, 3, 4, 5)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean apply(Integer input) {
                        return input % 2 == 1; //filter out even integers
                    }
                })
                .map(new Function<Integer, Optional<String>>() {
                    @Override
                    public Optional<String> apply(Integer input) {
                        return Optional.of(input.toString()); //convert integer to String
                    }
                })
                .reduce(new FunctionAcc<String, String>() {
                    @Override
                    public Optional<String> apply(String acc, String next) {
                        return Optional.of(acc + "," + next); //join integers on ","
                    }
                })
                .forEach(println())  //print out reaction results each in one line
                .start(); //on start() it will iterate through the array to fire the reactives

        //Reaction walk through:
        // Original source:          1 -> 2 -> 3 -> 4 -> 5 -> |
        // Filter events:            1 ------> 3 ------> 5 -> |
        // Map to String:           "1" ----> "3" ----> "5"-> |
        // Join on "," by reduce:    -----> "1,3" --> "1,3,5" -> |
        // Print out on each:        ----> "1,3\n" -> "1,3,5\n" -> |
    }

    /**
     * This example demonstrate how FunctionalReactives could be used to source from another customised source even
     * it schedule events from a different thread as mostly sources do.
     *
     */
    @Test
    public void testEventSourcingFromAsynchronousSubscribable() throws Exception {
        FunctionalReactives.createAsync( //assume source happens in a different thread

                //For more complex sourcing, a Subscribable needs to be implemented
                //The example subscribable here is only a simple one that will
                //schedule 1 ~ 5 to the reactives
                new Subscribable<Integer>() {
                    private final ExecutorService executor = Executors.newSingleThreadExecutor();

                    @Override
                    public void doSubscribe(final Schedulable<? super Integer> source) {
                        executor.execute(new Runnable() {
                            @Override
                            public void run() { //produce integers in a different thread
                                for (int i = 0; i < 6; i++) {
                                    source.schedule(i);
                                }
                            }
                        });
                    }

                    @Override
                    public void unsubscribe() { //unsubscribe from the source
                        executor.shutdown();
                        try {
                            executor.awaitTermination(5, TimeUnit.SECONDS); //wait for all the scheduled values fired
                        } catch (InterruptedException e) {
                            Throwables.propagate(e); //just hide the checked exception
                        }
                    }
                })
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean apply(Integer input) {
                        return input % 2 == 0;  //filter out odd integers
                    }
                })
                .forEach(println()); //print out reaction results each in a line

        //Reaction walk through:
        // Original source:          1 -> 2 -> 3 -> 4 -> 5 -> |
        // Filter events:            ---> 2 ------> 4 ------> |
        // Print out results:        --> "2" ----> "4" -----> |

    }
}
