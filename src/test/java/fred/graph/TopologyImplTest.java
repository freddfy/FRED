package fred.graph;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;


/**
 *
 * Author:  Fred Deng
 */
public class TopologyImplTest {
    private TopologyImpl<Integer> subject = new TopologyImpl<Integer>();
    private Vertex<Integer> one;
    private Vertex<Integer> two;
    private Vertex<Integer> three;
    private Vertex<Integer> four;
    private Vertex<Integer> five;

    @Before
    public void setUp() throws Exception {
        one = new Vertex<Integer>(1);
        two = new Vertex<Integer>(2);
        three = new Vertex<Integer>(3);
        four = new Vertex<Integer>(4);
        five = new Vertex<Integer>(5);
    }

    @Test
    public void testChainDependants() throws Exception {
        one.connectDependant(two);
        two.connectDependant(three);
        three.connectDependant(four);

        assertThat(subject.sort(one, Sets.newHashSet(four, three, two))).containsOnly(1, 2, 3, 4);
    }

    @Test
    public void testTriangleDependants() throws Exception {
        one.connectDependant(two);
        two.connectDependant(three);
        one.connectDependant(three);

        assertThat(subject.sort(one, Sets.newHashSet(three, two))).containsOnly(1, 2, 3);
    }

    @Test
    public void testDiamondDependants() throws Exception {
        one.connectDependant(two);
        two.connectDependant(three);
        three.connectDependant(four);
        one.connectDependant(three);
        two.connectDependant(four);

        assertThat(subject.sort(one, Sets.newHashSet(three, four, two))).containsOnly(1, 2, 3, 4);
    }

    @Test
    public void testMoreTangledThanDiamond() throws Exception {
        one.connectDependant(two);
        two.connectDependant(three);
        three.connectDependant(four);
        one.connectDependant(three);
        two.connectDependant(four);
        three.connectDependant(five);
        four.connectDependant(five);
        two.connectDependant(five);

        assertThat(subject.sort(one, Sets.newHashSet(five, two, three, four))).containsOnly(1, 2, 3, 4, 5);
    }

    @Test
    public void testIteratingFromVertexInMiddle() throws Exception {
        one.connectDependant(two);
        two.connectDependant(three);
        three.connectDependant(four);
        one.connectDependant(three);
        two.connectDependant(four);
        three.connectDependant(five);
        four.connectDependant(five);
        two.connectDependant(five);

        assertThat(subject.sort(two, Sets.newHashSet(four, three, five))).containsOnly(2, 3, 4, 5);
    }

    @Test
    public void testInvokingIteratorRemove() throws Exception {
        one.connectDependant(two);
        two.connectDependant(three);
        two.connectDependant(four);
        one.connectDependant(three);

        //4 is removed as the only dependant of 2
        Iterator<Integer> result = subject.sort(one, Sets.newHashSet(four, three, two));
        assertThat(removeWhen(result, 2)).containsOnly(1, 2, 3);
    }

    @Test
    public void testRemoveSourceResultInNoDependantsFollowing() throws Exception {
        one.connectDependant(two);
        two.connectDependant(three);
        two.connectDependant(four);
        one.connectDependant(three);

        //4 is removed as the only dependant of 2
        Iterator<Integer> result = subject.sort(one, Sets.newHashSet(four, three, two));
        assertThat(removeWhen(result, 1)).containsOnly(1);

    }

    private Iterator<Integer> removeWhen(final Iterator<Integer> delegate, final int toRemove) {
        return new Iterator<Integer>(){

            @Override
            public boolean hasNext() {
                return delegate.hasNext();
            }

            @Override
            public Integer next() {
                Integer next = delegate.next();
                if (toRemove == next) {
                   delegate.remove();
                }
                return next;
            }

            @Override
            public void remove() {
                delegate.remove();
            }

            @Override
            public String toString(){
                return delegate.toString();
            }
        };
    }
}
