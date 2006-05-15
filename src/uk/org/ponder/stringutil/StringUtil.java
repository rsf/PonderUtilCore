/*
 * Created on 14-May-2006
 */
package uk.org.ponder.stringutil;

public class StringUtil {
  /** Returns the character of first mismatch of s1 and s2 **/
  public static int commonPrefix(String s1, String s2) {
    int i = 0;
    while (true) {
      if (i >= s1.length() || i >= s2.length()) break;
      if (s1.charAt(i) != s2.charAt(i)) break;
    }
    return i;
  }
}
