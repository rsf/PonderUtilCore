/*
 * Created on 15-Jan-2006
 */
package uk.org.ponder.iocevent;

/** ListenerReporter is part of a general solution to the "listener adder" problem
 * raised by IOC containers. Given they expect beans to load in an order induced
 * by a DAG, AND in addition would prefer not to be aware specifically of the
 * number and nature of their targets, it is generally hazardous to specify
 * these as direct bean dependencies.
 * <p>Instead, each bean specifies a single, central "wiring" bean as their
 * dependency, the "beetlecrusher", which implements ListenerGetter on one
 * side, and ListenerReporter on the other. 
 * <p>It is key for the operation of this solution that no events are fired
 * before the bean container is fully constructed! 
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public interface ListenerReporter {
  /** Report the supplied listener object as listening to a certain class
   * of event.
   * @param listener The listener to be reported.
   * @param listenerkey A key identifying this class of listener - the target
   * ListenerGetter will supply a matching key on requesting the listener list.
   * @param targetkey A key identifying the required type of target - the
   * target ListenerGetter will supply a matching key on requesting the listener
   * list.
   */
  public void reportListener(Object listener, Object listenerkey, Object targetkey);
}
