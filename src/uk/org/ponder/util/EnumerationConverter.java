/*
 * Created on Sep 22, 2004
 */
package uk.org.ponder.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

import uk.org.ponder.arrayutil.ArrayEnumeration;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class EnumerationConverter {
  public static boolean isObjectArray(Class c) {
    if (!c.isArray()) return false;
    return !c.getComponentType().isPrimitive(); 
  }
  public static boolean isEnumerable(Class c) {
    return Enumeration.class.isAssignableFrom(c) 
    || Collection.class.isAssignableFrom(c)
    || isObjectArray(c);
  }
  
  public static boolean isDenumerable(Class c) {
    return Collection.class.isAssignableFrom(c) || c.isArray();
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
  
  public static Denumeration getDenumeration(final Object collo) {
    if (collo instanceof Collection) {
      return new Denumeration() {

        public void add(Object o) {
	         ((Collection)collo).add(o);
        }};
    }
    else if (collo instanceof Array) {
      return new Denumeration() {
        int index = 0;
        public void add(Object o) {
          Object[] coll = (Object[]) collo;
          coll[index++] = o;
        }
        
      };
    }
    return null;
  }
  
}
