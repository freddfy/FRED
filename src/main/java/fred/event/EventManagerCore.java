package fred.event;

import com.google.common.collect.Lists;
import fred.graph.*;

import java.util.Iterator;
import java.util.Queue;

/**
 * Core implementation managing event in a graph and topology sort to fire event propagation.
 *
 * Author:  Fred Deng
 */
public class EventManagerCore implements EventManager {

    private final Graph<Event> graph;
    private final Queue<Runnable> inCycleQueue = Lists.newLinkedList();
    private boolean inCycle = false;
    private long cycle = 0;

    public EventManagerCore() {
        this(new GraphTopoSort<Event>(new TopologyImpl<Event>(), new VertexConnectionImpl<Event>(), new VerticesImpl<Event>()));
    }

    protected EventManagerCore(Graph<Event> graph) {
        this.graph = graph;
    }

    @Override
    public void fireCycle(Event source) {
        try{
            inCycle = true;
            cycle++;

            Iterator<Event> iter = graph.iterator(source);

            while (iter.hasNext()) {
                Event evt = iter.next();
                if (!evt.tryFire(cycle)) {
                    iter.remove();
                }
            }

            while(!inCycleQueue.isEmpty()){
                inCycleQueue.poll().run();
            }
        } finally {
            inCycle = false;
        }

    }

    @Override
    public boolean isFired(Event event) {
        return event.isLastFired(cycle);
    }

    @Override
    public void scheduleFire(final Event source) {
        if (inCycle) {
            inCycleQueue.offer(new Runnable() {
                @Override
                public void run() {
                    fireCycle(source);
                }
            });
        } else {
            fireCycle(source);
        }
    }

    @Override
    public void schedule(Runnable runnable) {
        if (inCycle) {
            inCycleQueue.offer(runnable);
        } else {
            runnable.run();
        }
    }

    @Override
    public void connect(Event source) {
        graph.connect(source);
    }

    @Override
    public void connect(Event source, Event target) {
        graph.connect(source, target);
    }

    @Override
    public void disconnect(Event source, Event target) {
        graph.disconnect(source, target);
    }

    @Override
    public void disconnect(Event target) {
        graph.disconnect(target);
    }

    @Override
    public void start() {}

    @Override
    public void shutdown() {}
}
