/*
 * Created on 29-Feb-2004
 */
package uk.org.ponder.swingutil;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.LayoutManager;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import uk.org.ponder.util.AssertionException;

/**
 * The class 
 * 
 * @author Bosmon
 */
public class LayoutSpec {
  int strutheight;
  int strutwidth = 5;
  LayoutManager exemplar;
  private Component strut;
  private Font defaultfont;
  public LayoutSpec(int strutheight, LayoutManager exemplar, Font defaultfont) {
    this.strutheight = strutheight;
    this.exemplar = exemplar;
    this.defaultfont = defaultfont;
  }
  
  public JLabel getLabel(String label) {
    JLabel togo = new JLabel(label);
    togo.setFont(defaultfont);
    return togo;
  }
  public JPanel getPanel() {
    JPanel togo = new JPanel(getLayout());
    togo.setFont(defaultfont);
    togo.add(getStrut()); 
    return togo;
  }
  public Component getStrut() {
    return Box.createVerticalStrut(strutheight);
  }
  public LayoutManager getLayout() {
    LayoutManager togo = null;
    if (exemplar instanceof FlowLayout) {
      FlowLayout exflow = (FlowLayout)exemplar;
      togo = new FlowLayout(exflow.getAlignment(), exflow.getHgap(), 
        exflow.getVgap());
    }
    else {
      throw new AssertionException("Unrecognised layout manager " + exemplar);
    }
    return togo;
  }
  public int getHeight() {
    return strutheight;
  }
  public Font getFont() {
    return defaultfont;
  }

  /**
   * @return
   */
  public Component getHStrut() {
    return Box.createHorizontalStrut(strutwidth);
  }
}
