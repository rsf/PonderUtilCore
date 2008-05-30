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
  
  /** JDK String.split is EXTREMELY slow and also has somewhat unclear
   * semantics.
   */
  public static String[] split (String tosplit, char delim, boolean trim) {
    StringList togo = new StringList();
    CharWrap buffer = new CharWrap();
    for (int i = 0; i < tosplit.length(); ++ i) {
      char c = tosplit.charAt(i);
      if (c == delim) {
        togo.add(trim? buffer.toString().trim() : buffer.toString());
        buffer.clear();
      }
      else buffer.append(c);
    }
    if (trim) {
      String trimmed = buffer.toString().trim();
      if (trimmed.length() > 0) {
        togo.add(trimmed);
      }
    }
    else {
      togo.add(buffer.toString());
    }
    return togo.toStringArray();
  }
  
  public static String[] parseArray(String tosplit) {
    return split(tosplit, ',', true);
  }
}
