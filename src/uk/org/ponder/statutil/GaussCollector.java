/*
 * Created on Mar 3, 2004
 */
package uk.org.ponder.statutil;

import java.text.DecimalFormat;

import uk.org.ponder.doubleutil.DoubleFormat;

/**
 * The class 
 * 
 * @author Bosmon
 */
public class GaussCollector {
  public double min;
  public double max;
  public double sx;
  public double sx2;
  public int n;
  public GaussCollector init() {
    sx = sx2 = n = 0;
    min = Double.MAX_VALUE;
    max = -Double.MAX_VALUE;
    return this;
  }
  public GaussCollector collect(double x) {
    sx += x;
    sx2 += x*x;
    if (x < min) min = x;
    if (x > max) max = x;
    ++n;
    return this;
  }
  public GaussCollector collect(GaussCollector other) {
    sx += other.sx;
    sx2 += other.sx2;
    n += other.n;
    if (other.min < min) min = other.min;
    if (other.max > max) max = other.max;
    return this;
  }
  public double mean() {
    return n == 0? 0 : sx / n;
  }
  public double sd() {
    return Math.sqrt(variance());
  }
  public double variance() {
    return n == 0? 0 : Math.max((sx2 - sx*sx/n)/n, 0);
  }
  public String toString() {
    DecimalFormat df = DoubleFormat.getFormat4();
    return n + " data: mean " + df.format(mean()) + " SD " + df.format(Math.sqrt(variance())) + " min " + df.format(min) + " max " + df.format(max);
  }
  public int count() {
    return n;
  }
}
