/*
 * Created on Mar 20, 2004
 */
package uk.org.ponder.swingutil;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

import uk.org.ponder.event.EventFirer;
import uk.org.ponder.event.Listener;

/**
 * Converts any JComponent into a button by being passed as the constructor argument. 
 * 
 * @author Bosmon
 */
public class ButtonyListener {
  Border raisedborder = BorderFactory.createRaisedBevelBorder();
  Border loweredborder = BorderFactory.createLoweredBevelBorder();
  Border noborder = BorderFactory.createEmptyBorder(2, 2, 2, 2); 
  JComponent tolisten;
  EventFirer eventfirer = new EventFirer(new Class[]{ButtonPressedEvent.class});
  public ButtonyListener(JComponent tolisten) {
    tolisten.addMouseListener(listener);
    this.tolisten = tolisten;
    tolisten.setBorder(noborder);
    }

  public void addListener(Listener l) {
    eventfirer.addListener(ButtonPressedEvent.class, l);
  }
  MouseListener listener = new MouseAdapter() {
    public void mouseEntered(MouseEvent me) {
      tolisten.setBorder(raisedborder);
    }
    public void mouseExited(MouseEvent me) {
      tolisten.setBorder(noborder);
    }
    public void mousePressed(MouseEvent me) {
      tolisten.setBorder(loweredborder);
    }
    public void mouseReleased(MouseEvent me) {
      if (tolisten.contains(me.getPoint())) {
        tolisten.setBorder(noborder);
        eventfirer.fireEvent(new ButtonPressedEvent(tolisten));
      }
    }
  };
}
