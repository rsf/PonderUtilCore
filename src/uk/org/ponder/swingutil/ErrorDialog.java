/*
 * Created on Feb 28, 2004
 */
package uk.org.ponder.swingutil;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * The class 
 * 
 * @author Bosmon
 */
public class ErrorDialog {
  public static void show(JFrame frame, Throwable t) {
    JOptionPane.showMessageDialog(frame,
        t.getMessage(),
        "Error",
        JOptionPane.ERROR_MESSAGE);

  }
}
