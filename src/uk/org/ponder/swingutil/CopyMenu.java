/*
 * Created on 20-Feb-2006
 */
package uk.org.ponder.swingutil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.JTextComponent;

public class CopyMenu {
  public static JPopupMenu get(final JTextComponent target) {
    JPopupMenu togo = new JPopupMenu();
    JMenuItem copyitem = new JMenuItem("Copy");
    copyitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        target.copy();
      }
    });
    togo.add(copyitem);
    PopupListener.add(target, togo);
    return togo;
  }
}
