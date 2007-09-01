/*
 * Created on 6 Oct 2006
 */
package uk.org.ponder.beanutil.support;

import java.lang.reflect.Array;
import java.util.List;

import uk.org.ponder.beanutil.PropertyAccessor;
import uk.org.ponder.saxalizer.mapping.ContainerTypeRegistry;
import uk.org.ponder.util.EnumerationConverter;

/** Accessor for numeric indexed properties (held in Arrays or Lists) * */

public class IndexedPropertyAccessor implements PropertyAccessor {

  public void setContainerTypeRegistry(
      ContainerTypeRegistry containertyperegistry) {
    this.ctr = containertyperegistry;
  }

  public static boolean isIndexed(Class c) {
    return (c.isArray() || List.class.isAssignableFrom(c));
  }

  private ContainerTypeRegistry ctr;

  public boolean canGet(String name) {
    return true;
  }

  public boolean canSet(String name) {
    return true;
  }

  public Object getProperty(Object parent, String name) {
    int index = Integer.parseInt(name);
    if (parent.getClass().isArray()) {
      return Array.get(parent, index);
    }
    else {
      return ((List) parent).get(index);
    }
  }

  public Class getPropertyType(Object parent, String name) {
    Class ctype = ctr.getContaineeType(parent);
    if (ctype == null) {
      return String.class;
    }
    else
      return ctype;
  }

  public boolean isMultiple(Object parent, String name) {
    return EnumerationConverter.isDenumerable(getPropertyType(parent, name));
  }

  public void setProperty(Object parent, String name, Object value) {
    int index = Integer.parseInt(name);
    if (parent.getClass().isArray()) {
      Array.set(parent, index, value);
    }
    else {
      ((List) parent).set(index, value);
    }

  }

  public void unlink(Object parent, String name) {
    int index = Integer.parseInt(name);
    if (parent instanceof List) {
      ((List) parent).remove(index);
    }
    else {
      throw new UnsupportedOperationException(
          "Cannot remove element from array " + parent.getClass());
    }
  }

}
