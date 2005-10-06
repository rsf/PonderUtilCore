/*
 * Created on Aug 4, 2005
 */
package uk.org.ponder.beanutil;

/**
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
  
  public static String getTailPath(String path) {
    int lastdot = path.lastIndexOf('.');
    return lastdot == -1? path : path.substring(lastdot + 1);
  }

}
