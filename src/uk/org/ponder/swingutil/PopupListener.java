/*
 * Created on 20-Feb-2006
 */
package uk.org.ponder.swingutil;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

public class PopupListener extends MouseAdapter {
  public JComponent parent;
  public JPopupMenu target;

  public PopupListener(JComponent parent, JPopupMenu target) {
    this.parent = parent;
    this.target = target;
  }

  public static void add(JComponent parent, JPopupMenu target) {
    PopupListener toadd = new PopupListener(parent, target);
    parent.addMouseListener(toadd);
  }
  
  public void mouseClicked(MouseEvent me) {
    if (SwingUtilities.isRightMouseButton(me)) {
      target.show((Component) me.getSource(), me.getX(), me.getY());
    }
  }

}
