/*
 * Created on Apr 27, 2004
 */
package uk.org.ponder.swingutil;

import java.awt.Font;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.FontUIResource;

/**
 * The class 
 * 
 * @author Bosmon
 */
public class SwingUtil {
  static final Border empty10Border = BorderFactory.createEmptyBorder(3, 2, 6, 2);
  static final Border etchedBorder = BorderFactory.createEtchedBorder();

  public static Border getTitledBorder(String title) {
    return BorderFactory.createCompoundBorder(
      BorderFactory.createTitledBorder(etchedBorder, title),
      empty10Border);
  }
  
  public static void expandJTree(JTree toexpand) {
    // replace this with a sensible implementation if it fails
    for (int row = 0; row < toexpand.getRowCount(); ++row) {
      toexpand.expandRow(row);
    }
  }
  public static void setUIFont(Font f) {
     //
     // sets the default font for all Swing components.
     // ex. 
     //  setUIFont (new javax.swing.plaf.FontUIResource("Serif",Font.ITALIC,12));
     //
     FontUIResource fur = new FontUIResource(f);
     java.util.Enumeration keys = UIManager.getDefaults().keys();
     while (keys.hasMoreElements()) {
       Object key = keys.nextElement();
       Object value = UIManager.get(key);
       if (value instanceof javax.swing.plaf.FontUIResource)
         UIManager.put(key, fur);
     }
   }


  public static ArrayList listModelToList(ListModel listmodel) {
    ArrayList togo = new ArrayList();
    for (int i = 0; i < listmodel.getSize(); ++ i) {
      togo.add(listmodel.getElementAt(i));
    }
    return togo;
  }
}
