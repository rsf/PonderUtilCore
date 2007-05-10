/*
 * Created on 15-Jan-2006
 */
package uk.org.ponder.iocevent;

import java.util.List;

public interface ListenerGetter {
  /** Gets a nominated set of listeners from storage.
   * 
   * @param listenerkey A key identifying the type of listener to be
   * acquired. This would probably be an interface type implemented by the listener.
   * @param targetkey A key identifying the type of target requesting the listeners.
   * This would probably be a String representing the target's function.
   * @return
   */
  public List getListeners(Object listenerkey, Object targetkey);
}
