package uk.org.ponder.event;

import uk.org.ponder.arrayutil.ArrayUtil;
import uk.org.ponder.util.AssertionException;
import uk.org.ponder.util.Logger;

import java.util.ArrayList;
///  Event handling

/**
 * @package <numode.event The package event contains the tag interface EventTag,
 * which is implemented
 * by all event classes. The interface Listener has to be implemented by classes
 * wishing to receive events. An object of the class EventFirer is responsible
 * for managing Listeners and firing events.
 */


/** An object of the class EventFirer is responsible for managing Listeners registered
 * to receive events and to fire events on demand. Upon construction, the object
 * receives information about which classes of events will be fired. For each class,
 * it manages an ArrayList of Listeners. 
 */

public class EventFirer {
 /// An array of ArrayLists containing the Listeners registered to receive events.
  private ArrayList[] listenervectors;
  
  /// The array of event classes handled by this firer.
  private Class[] registeredclasses; 
  
  /// Constructs an EventFirer with the specified classes of events to be fired.
  public EventFirer(Class[] classvector) {
    registeredclasses = classvector;
    listenervectors = new ArrayList[classvector.length];
    for (int i = 0; i < classvector.length; ++ i) {
	    listenervectors[i] = new ArrayList();
      }
    }
    
  public EventFirer(Class clazz) {
    this(new Class[] {clazz});
  }

  public void addListener(Class[] eventclasses, Listener listener) {
    for (int i = 0; i < eventclasses.length; ++ i) {
      addListener(eventclasses[i], listener);
      }
    }

  /// Register a Listener to receive events of the specified class
  public void addListener(Class eventclass, Listener listener) {
    if (listener == null) {
      throw new AssertionException("Null listener added to EventFirer for class "+eventclass);
      }
    int classindex = ArrayUtil.indexOf(registeredclasses, eventclass);
    if (classindex != -1) {
	    ArrayList listeners = listenervectors[classindex];
	    listeners.add(listener);
      }
      else {
        throw new AssertionException("Unknown event class " + eventclass + " added to EventFirer");
      }
    }
  /// Unregister a Listener from receiving events of the specified class
  public void removeListener(Class eventclass, Listener listener) {
    int classindex = ArrayUtil.indexOf(registeredclasses, eventclass);
    if (classindex != -1) {
      ArrayList listeners = listenervectors[classindex];
      listeners.remove(listener);
      }
    }
  /// Fire the supplied event object to any registered listeners. The listeners are chosen based on the class of the supplied object.
  
  public void fireEvent(EventTag tofire) {
    for (int classindex = 0; classindex < registeredclasses.length; ++ classindex) {
	    if (registeredclasses[classindex].isInstance(tofire)) {
        ArrayList listeners = listenervectors[classindex];
        for (int i = listeners.size() - 1; i >= 0; -- i) {
          try {
            ((Listener)listeners.get(i)).receiveEvent(tofire);
            }
          catch (Throwable t) {
            Logger.log.warn("Error propagating event", t);
            }
          }
        }
      }
    }
  }
