/*
 * Created on May 28, 2006
 */
package uk.org.ponder.stringutil;

public class StringUtil {
  /** Compares two Strings for equality, where either may be null **/
  public static final boolean equals(String a, String b) {
    if (a == null) {
      return b == null;
    }
    else return a.equals(b);
  }
  
  /** Returns a hashCode for a String, which may be null **/
  public static final int hashCode(String a) {
    return a == null? 0 : a.hashCode();
  }
}
