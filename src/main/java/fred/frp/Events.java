package fred.frp;

import fred.event.Event;

/**
 * Some straight forward event class, could be helpful in implementing more consistent event handling.
 *
 * e.g. ReactFuncBuffer
 */
public class Events {

    /**
     * The event appears to always fire when:
     * 1. It is in the graph and event propagation hits it, so it will further trigger downstream events
     * 2. It is invoked in EventManager.isFired(event)
     */
    public static Event alwaysFireIfTriggered(){
         return new Event() {
             @Override
             public boolean tryFire(long cycle) {
                 return true;
             }

             @Override
             public boolean isLastFired(long lastCycle) {
                 return true;
             }
         };
    }

    /**
     * The event appears to always not fires, regardless of it being in graph or not, or EventManager.isFired(event).
     */
    public static Event alwaysNotFire(){
         return new Event(){

             @Override
             public boolean tryFire(long cycle) {
                 return false;
             }

             @Override
             public boolean isLastFired(long lastCycle) {
                 return false;
             }
         };
    }
}
