package uk.org.ponder.util;

/** Owing to a peculiar bug/inconsistency in certain JVMs, this class is necessary
 * to swallow the ClassNotFoundException that otherwise cannot be handled if 
 * Class.forName is tried in certain contexts (static/inner)
 */

public class ClassGetter {
  /** Returns the Class object corresponding to the supplied fully-qualified classname,
   * as if looked up by Class.forName().
   * @param classname The classname to be looked up.
   * @return The Class object corresponding to the classname, or <code>null</code> if the
   * name cannot be looked up.
   */
  public static Class forName(String classname) {
    try {
      return Class.forName(classname);
      }
    catch (ClassNotFoundException cnfe) {
      return null;
      }
    }
  }
