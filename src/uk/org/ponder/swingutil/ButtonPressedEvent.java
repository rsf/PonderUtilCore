/*
 * Created on Mar 20, 2004
 */
package uk.org.ponder.swingutil;

import javax.swing.JComponent;

import uk.org.ponder.event.EventTag;

/**
 * The class 
 * 
 * @author Bosmon
 */
public class ButtonPressedEvent implements EventTag {
  JComponent source;
  public ButtonPressedEvent(JComponent source) {
    this.source = source;
  }
}
