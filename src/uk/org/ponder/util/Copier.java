/*
 * Created on Jan 22, 2004
 */
package uk.org.ponder.util;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * The class 
 * 
 * @author Bosmon
 */
public class Copier {
  private static Class[] noclass = new Class[] {};
  private static Object[] noobj = new Object[] {};
  private static HashMap copiers = new HashMap();
  public static Object copy(Object o) {
    try {
    Class c = o.getClass();
    Method m = (Method)copiers.get(c);
    if (m == null) {
      m = c.getMethod("copy", noclass);
      copiers.put(c, m);
    }
    return m.invoke(o, noobj);
    }
    catch (Throwable t) {
      throw new RuntimeException("Failed to copy " + o);
    }
  }
}
