/*
 * Created on Sep 22, 2004
 */
package uk.org.ponder.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

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
    || Map.class.isAssignableFrom(c)
    || Iterator.class.isAssignableFrom(c)
    || isObjectArray(c);
  }

  // Maps are not currently denumerable! Pending some scheme for
  // getting at their keys.
  public static boolean isDenumerable(Class c) {
    return /*c != Collection.class && */Collection.class.isAssignableFrom(c) && 
    !isMappable(c) || c.isArray();
  }
  
  public static boolean isMappable(Class c) {
    // other things may be mappable too. In practice we will make
    // reduced map interface.
    return Map.class.isAssignableFrom(c);
  }
  
  public static Map getMap(Object o) {
    if (o instanceof Map) {
      return (Map)o;
    }
    throw new AssertionException("getMap called for unmappable type " +
        o.getClass());
  }
  
  public static Enumeration getEnumeration(final Object o) {
    if (o instanceof Enumeration) {
      return (Enumeration)o;
    }
    else if (o instanceof Iterator) {
      return new Enumeration() {
        public boolean hasMoreElements() {
          return ((Iterator)o).hasNext();
        }

        public Object nextElement() {
          return ((Iterator)o).next();
        }
      };
    }
    else if (o instanceof Collection) {
      return Collections.enumeration((Collection) o);
    }
    else if (o instanceof Map) {
      return Collections.enumeration(((Map)o).values());
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
        }
        public boolean remove(Object o) {
           return ((Collection)collo).remove(o);
        }};
    }
    // NB this array functionality will probably never be used - better to
    // make leaf parsers for Arrays storing exactly length up front.
    else if (collo instanceof Array) {
      return new Denumeration() {
        int index = 0;
        public void add(Object o) {
          Object[] coll = (Object[]) collo;
          coll[index++] = o;
        }
        public boolean remove(Object o) {
          throw new UniversalRuntimeException("Removal not supported from Array denumerable");
        }
        
      };
    }
    return null;
  }
  
}
