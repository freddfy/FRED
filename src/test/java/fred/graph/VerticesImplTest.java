package fred.graph;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


/**
 *
 * Author:  Fred Deng
 */
public class VerticesImplTest {
    private VerticesImpl<String> subject;

    @Before
    public void setUp() throws Exception {
        subject = new VerticesImpl<String>();

    }

    @Test
    public void testCreateNew() throws Exception {
        Vertex<String> one = subject.getOrCreate("1");
        Vertex<String> another = subject.getOrCreate("1");

        assertThat(one).isSameAs(another);
    }

    @Test
    public void testGetReturnCreated() throws Exception {
        Vertex<String> created = subject.getOrCreate("1");
        Vertex<String> got = subject.get("1");

        assertThat(created).isSameAs(got);
    }

    @Test
    public void testGetReturnNullIfNotCreated() throws Exception {
        Vertex<String> got = subject.get("1");

        assertThat(got).isNull();
    }

    @Test
    public void testRemoveCreated() throws Exception {
        Vertex<String> one = subject.getOrCreate("1");
        Vertex<String> two = subject.getOrCreate("2");

        assertThat(one).isSameAs(subject.remove("1"));
        assertThat(subject.get("1")).isNull();

        assertThat(two).isSameAs(subject.get("2"));
    }

    @Test
    public void testReturnNullWhenRemoveNonExists() throws Exception {
        assertThat(subject.remove("2")).isNull();
    }

    @Test
    public void testReturnNullWhenRemoveNull() throws Exception {
        assertThat(subject.remove(null)).isNull();
    }

    @Test
    public void testReturnNullWhenGetOrCreateNull() throws Exception {
        assertThat(subject.getOrCreate(null)).isNull();

    }
}
