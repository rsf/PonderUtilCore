/*
 * Created on 21-Sep-2003
 */
package uk.org.ponder.swingutil;

import java.awt.RenderingHints;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Graphics2D;

import javax.swing.BoxLayout; // QQQQQ this can probably be done without Swing
import javax.swing.JPanel;

/**
 * A collection of small utility methods useful in dealing with the AWT GUI libraries.
 * @author Bosmon
 */

public class AWTUtil {
   public static void labelledOutLayer(
    Container parent,
    Component[] labels,
    Component[] labelled,
    LayoutSpec layoutspec) {
    GBLWrap gblw = new GBLWrap(parent);
    for (int i = 0; i < labels.length; ++i) {
      if (i == labels.length - 1) {
        gblw.endcol();
      }
      gblw.apply(labels[i], "l");
      if (i == labels.length - 1) {
        gblw.endcol();
      }
      Component righty = labelled[i];
      if (righty instanceof JPanel) {
        ((JPanel) righty).add(layoutspec.getStrut());
      }
      else {
        JPanel newpanel = layoutspec.getPanel();
        newpanel.add(righty);
        righty = newpanel;
      }
      gblw.endrow().apply(righty, "l");
    }

  }

  public static void flowOutLayer(
    Container parent,
    Component[] labels,
    Component[] labelled,
    LayoutSpec layoutspec) {
    parent.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));
    for (int i = 0; i < labels.length; ++i) {
      JPanel rowpanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      rowpanel.add(labels[i]);
      rowpanel.add(labelled[i]);
      rowpanel.add(layoutspec.getStrut());
      parent.add(rowpanel);
    }
  }

  public static void fillRect(Graphics g, int x1, int y1, int width, int height) {
    if (width < 0) {
      x1 = x1 + width;
      width = -width;
    }
    if (height < 0) {
      y1 = y1 + height;
      height = -height;
    }
    g.fillRect(x1, y1, width, height);
  }

  public static void drawRect(Graphics g, int x1, int y1, int width, int height) {
    if (width < 0) {
      x1 = x1 + width;
      width = -width;
    }
    if (height < 0) {
      y1 = y1 + height;
      height = -height;
    }
    g.drawRect(x1, y1, width, height);
  }

  public static double colorToLuminance(Color c) {
    //      System.out.println(c.toString());
    double luminance =
      (0.212671 * c.getRed() + 0.715160 * c.getGreen() + 0.072169 * c.getBlue()) / 255;
    //      System.out.println("" + luminance);
    return luminance;
  }
  public static void setClipBounds(Graphics g, Rectangle toset) {
    g.setClip(toset.x, toset.y, toset.width, toset.height);
  }
  /** Sets the clip bounds of the specified graphics context to the intersection of the
   * two supplied clip bounds.
   * @param g The graphics object for which the clip bounds are to be adjusted.
   * @param origbounds The first rectangle to be intersected (the original OS clip
   * bounds)
   * @param toset The second rectangle to be intersected.
   */
  public static void setClipBounds(Graphics g, Rectangle origbounds, Rectangle toset) {
    Rectangle intersect = origbounds.intersection(toset);
    setClipBounds(g, intersect);
  }
  public static void expandRectangle(Rectangle toexpand, int amount) {
    toexpand.x -= amount;
    toexpand.y -= amount;
    toexpand.width += 2 * amount;
    toexpand.height += 2 * amount;
  }
  public static void antiAlias(Graphics2D graphics, boolean state) {
    graphics.setRenderingHint(
      RenderingHints.KEY_ANTIALIASING,
      state ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

  }
}
