/*
 * Created on Oct 22, 2005
 */
package uk.org.ponder.beanutil.support;

import java.util.Map;

import uk.org.ponder.beanutil.PropertyAccessor;
import uk.org.ponder.util.EnumerationConverter;

public class MapPropertyAccessor implements PropertyAccessor {
  // This class is completely immutable, can universally use this instance.
  public static final MapPropertyAccessor instance = new MapPropertyAccessor();
  
  public boolean canSet(String name) {
    // well, we can't know any better at this point! If the JDK were better
    // designed it might be possible.
    return true;
  }

  public void setProperty(Object parent, String name, Object value) {
    ((Map)parent).put(name, value);
  }

  public void unlink(Object parent, String name) {
    ((Map)parent).remove(name);
  }

  public boolean canGet(String name) {
    return true;
  }

  public Object getProperty(Object parent, String name) {
   return ((Map)parent).get(name);
  }

  public Class getPropertyType(Object parent, String name) {
    Object got = ((Map)parent).get(name);
    return got == null? Object.class : got.getClass();
  }

  public boolean isMultiple(Object parent, String name) {
    return EnumerationConverter.isDenumerable(getPropertyType(parent, name));
  }
}
