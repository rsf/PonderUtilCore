/*
 * Created on Apr 27, 2004
 */
package uk.org.ponder.swingutil;

import javax.swing.SwingUtilities;

import uk.org.ponder.event.EventTag;
import uk.org.ponder.event.Listener;

/**
 * The class 
 * 
 * @author Bosmon
 */
public class SwingListener implements Listener {
  private Listener target;
  private EventTag event;
  
  public SwingListener(Listener target) {
    this.target = target;
  }
  private Runnable laterer = new Runnable() {
    public void run() {
      target.receiveEvent(event);
    }
  };
  public void receiveEvent(EventTag event) {
    this.event = event;
    SwingUtilities.invokeLater(laterer);
  } 
}
