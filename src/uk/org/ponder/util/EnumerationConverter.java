/*
 * Created on Sep 22, 2004
 */
package uk.org.ponder.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

import uk.org.ponder.arrayutil.ArrayEnumeration;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class EnumerationConverter {
  public static boolean isEnumerable(Class c) {
    return c.isAssignableFrom(Enumeration.class) 
    || c.isAssignableFrom(Collection.class)
    || c.isArray();
  }
  public static Enumeration getEnumeration(Object o) {
    if (o instanceof Enumeration) {
      return (Enumeration)o;
    }
    if (o instanceof Collection) {
      return Collections.enumeration((Collection) o);
    }
    else if (o.getClass().isArray()) {
      return new ArrayEnumeration((Object[])o);
    }
    else throw new AssertionException("getEnumeration called for unenumerable type "+
        o.getClass());
  }
}
