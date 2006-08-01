/*
 * Created on Aug 4, 2005
 */
package uk.org.ponder.beanutil;

/**
 * A set of utility methods to operate on dot-separated bean paths.
 * TODO: implement the special ['path with.dots'] syntax for property
 * names that contain periods.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class PathUtil {
  
  public static String getHeadPath(String path) {
    int firstdot = path.indexOf('.');
    return firstdot == -1? path : path.substring(0, firstdot);
  }

  public static String getFromHeadPath(String path) {
    int firstdot = path.indexOf('.');
    return firstdot == -1? null : path.substring(firstdot + 1);
  }
  
  public static String getToTailPath(String path) {
    int lastdot = path.lastIndexOf('.');
    return lastdot == -1? null : path.substring(0, lastdot);
  }
  /** Returns the very last path component of a bean path */ 
  public static String getTailPath(String path) {
    int lastdot = path.lastIndexOf('.');
    return lastdot == -1? path : path.substring(lastdot + 1);
  }

  public static String composePath(String prefix, String suffix) {
    return prefix.equals("")? suffix : (prefix + '.' + suffix);
  }
  
}
