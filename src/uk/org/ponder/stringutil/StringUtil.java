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
}
