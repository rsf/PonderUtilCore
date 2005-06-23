/*
 * Created on Mar 3, 2004
 */
package uk.org.ponder.statutil;

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
    min = Double.POSITIVE_INFINITY;
    max = Double.NEGATIVE_INFINITY;
    return this;
  }
  public void collect(double x) {
    sx += x;
    sx2 += x*x;
    if (x < min) min = x;
    if (x > max) max = x;
    ++n;
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
  public double variance() {
    return (sx2 - sx*sx/n)/n;
  }
  public String toString() {
    return n + " data: mean " + mean() + " SD " + Math.sqrt(variance()) 
    + " min " + min + " max " + max;
  }
  public int count() {
    return n;
  }
}
