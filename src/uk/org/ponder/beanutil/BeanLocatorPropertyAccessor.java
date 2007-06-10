/*
 * Created on Oct 22, 2005
 */
package uk.org.ponder.beanutil;

import uk.org.ponder.util.AssertionException;
import uk.org.ponder.util.EnumerationConverter;

public class BeanLocatorPropertyAccessor implements PropertyAccessor {
  // This class is completely immutable, can universally use this instance.
  public static final BeanLocatorPropertyAccessor instance = new BeanLocatorPropertyAccessor();

  public boolean canSet(String name) {
    return true;
  }

  public void setProperty(Object parent, String name, Object value) {
    if (!(parent instanceof WriteableBeanLocator)) {
      throw new AssertionException("Attempt to set non-writeable property "
          + name + " of BeanLocator");
    }
    else
      ((WriteableBeanLocator) parent).set(name, value);
  }

  public void unlink(Object parent, String name) {
    if (!(parent instanceof WriteableBeanLocator)) {
      throw new AssertionException("Attempt to set non-writeable property "
          + name + " of BeanLocator");
    }
    else {
      ((WriteableBeanLocator) parent).remove(name);
    }
  }

  public boolean canGet(String name) {
    return true;
  }

  public Object getProperty(Object parent, String name) {
    return ((BeanLocator) parent).locateBean(name);
  }

  public Class getPropertyType(Object parent, String name) {
    // The fact that this currently causes a fetch of the parent bean
    // causes an anomaly with scoped beans, which are momentarily
    // blank-constructed before they are replaced.
    Object located = ((BeanLocator)parent).locateBean(name);
    return located == null? Object.class : located.getClass(); 
  }

  public boolean isMultiple(Object parent, String name) {
    return EnumerationConverter.isDenumerable(getPropertyType(parent, name));
  }

}
