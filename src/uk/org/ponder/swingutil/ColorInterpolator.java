/*
 * Created on Mar 16, 2004
 */
package uk.org.ponder.swingutil;

import java.awt.Color;

/**
 * The class 
 * 
 * @author Bosmon
 */
public class ColorInterpolator {
  private Color start;
  private Color end;
  private double minval;
  private double maxval;
  public ColorInterpolator(Color start, Color end, double minval, double maxval) {
    this.start = start;
    this.end = end;
    this.minval = minval;
    this.maxval = maxval;
  }
  public Color interpolate(double interp) {
    return new Color(
    (int)(((maxval - interp)*start.getRed() + (interp - minval)*end.getRed())/ (maxval - minval)),
    (int)(((maxval - interp)*start.getGreen() + (interp - minval)*end.getGreen())/ (maxval - minval)),
    (int)(((maxval - interp)*start.getBlue() + (interp - minval)*end.getBlue())/ (maxval - minval))
    );
  }

}
