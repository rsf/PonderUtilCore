/*
 * Created on Dec 20, 2004
 */
package uk.org.ponder.doubleutil;

import java.text.DecimalFormat;

/**
 * @author Antranig Basman (amb26@ponder.org.uk)
 * 
 */
public class DoubleFormat {
  private static ThreadLocal formatlocal = new ThreadLocal() {
    public Object initialValue() {
      return new DecimalFormat("0.0000");
    }
  };
  
  public static String format4(double val) {
    return getFormat4().format(val);
  }
  public static DecimalFormat getFormat4() {
    return (DecimalFormat) formatlocal.get();
  }
}
