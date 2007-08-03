/*
 * Created on Aug 4, 2005
 */
package uk.org.ponder.beanutil;

import uk.org.ponder.stringutil.CharWrap;
import uk.org.ponder.stringutil.StringList;

/**
 * A set of utility methods to operate on dot-separated bean paths.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class PathUtil {

  public static String getHeadPath(String path) {
    return getPathSegment(path, 0);
  }
  /** Returns the first path segment, without performing unescaping **/
  public static String getHeadPathEncoded(String path) {
    int firstdot = getPathSegment(null, path, 0);
    return path.substring(0, firstdot);
  }

  public static String getFromHeadPath(String path) {
    int firstdot = getPathSegment(null, path, 0);
    return firstdot == path.length() ? null
        : path.substring(firstdot + 1);
  }

  public static String getToTailPath(String path) {
    int lastdot = lastDotIndex(path);
    return lastdot == -1 ? null
        : path.substring(0, lastdot);
  }

  /** Returns the very last path component of a bean path */
  public static String getTailPath(String path) {
    int lastdot = lastDotIndex(path);
    return getPathSegment(path, lastdot + 1);
  }
  
  /** Parses a path into an array of decoded EL segments **/
  public static String[] splitPath(String path) {
    StringList togo = new StringList();
    CharWrap build = new CharWrap();
    int index = 0;
    while (index < path.length()) {
      index = PathUtil.getPathSegment(build, path, index) + 1;
      togo.add(build.toString());
      build.clear();
    }
    return togo.toStringArray();
  }
  
  /** Builds an EL path of variable length. Particulary good when using
   *  strings of BeanLocators, Maps, and friends. Assumes none of the segments
   *  have been escaped yet.
   */
  public static String composePath(String[] segments) {
    CharWrap toappend = new CharWrap();
    for (int i = 0; i < segments.length; i++) {
      if (toappend.size != 0) {
        toappend.append('.');
      }
      composeSegment(toappend, segments[i]);
    }
    return toappend.toString();
  }

  /**
   * Compose a prefix and suffix EL path, where the prefix is already escaped.
   * Prefix may be empty, but not null.
   */
  public static String composePath(String prefix, String suffix) {
    CharWrap toappend = new CharWrap(prefix);
    if (toappend.size != 0) {
      toappend.append('.');
    }
    composeSegment(toappend, suffix);
    return toappend.toString();
  }

  /** Compose head and tail paths, where escaping is unnecessary * */
  public static String composePathEncoded(String head, String tail) {
    return head + '.' + tail;
  }
  
  /**
   * Compose a prefix and suffix EL path, where the prefix has not been escaped, 
   * and is not null.
   * @param prefix A single path segment (bean name) starting the path, may not
   * be null or empty. This will be escaped.
   * @param suffix A single path segment (property name) continuing the path,
   * may not be null or empty. This will become escaped.
   * @return A properly escaped full path, representing "prefix.suffix".
   */
  public static String buildPath(String prefix, String suffix) {
    CharWrap toappend = new CharWrap();
    composeSegment(toappend, prefix);
    toappend.append('.');
    composeSegment(toappend, suffix);
    return toappend.toString();
  }

  static int lastDotIndex(String path) {
    for (int i = path.length() - 1; i >= 0; --i) {
      if (path.charAt(i) == '.' && (i == 0 || path.charAt(i) != '\\'))
        return i;
    }
    return -1;
  }

  static void composeSegment(CharWrap toaccept, String toappend) {
    for (int i = 0; i < toappend.length(); ++i) {
      char c = toappend.charAt(i);
      if (c == '.' || c == '\\' || c == '}') {
        toaccept.append('\\').append(c);
      }
      else
        toaccept.append(c);
    }
  }

  static String getPathSegment(String path, int i) {
    CharWrap accept = new CharWrap();
    getPathSegment(accept, path, i);
    return accept.toString();
  }

  static int getPathSegment(CharWrap accept, String path, int i) {
    boolean escaped = false;
    for (; i < path.length(); ++i) {
      char c = path.charAt(i);
      if (!escaped) {
        if (c == '.') {
          return i;
        }
        else if (c == '\\') {
          escaped = true;
        }
        else if (accept != null)
          accept.append(c);
      }
      else {
        escaped = false;
        if (accept != null)
          accept.append(c);
      }
    }
    return i;
  }

}
