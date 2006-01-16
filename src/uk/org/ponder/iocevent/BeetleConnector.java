/*
 * Created on 15-Jan-2006
 */
package uk.org.ponder.iocevent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** The beetlecrusher of all listener dependencies.
 * 
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public class BeetleConnector implements ListenerReporter, ListenerGetter {
  private Map listeners = new HashMap();
  
  public void reportListener(Object listener, Object listenerclass, Object targetkey) {
    List classlisteners = getListeners(listenerclass, targetkey);
    classlisteners.add(listener);
  }

  public List getListeners(Object listenerclass, Object targetkey) {
    List classlisteners = (List) listeners.get(listenerclass);
    if (classlisteners == null) {
      classlisteners = new ArrayList();
      listeners.put(listenerclass, classlisteners);
    }
    return classlisteners;
  }
  

}
