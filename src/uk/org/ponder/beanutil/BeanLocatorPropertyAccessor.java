/*
 * Created on Oct 22, 2005
 */
package uk.org.ponder.beanutil;

import uk.org.ponder.util.AssertionException;

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

  public Class getPropertyType(String name) {
    return Object.class; // no idea!
  }

  public boolean isMultiple(String name) {
    // no idea!
    // TODO: the only conceivable way to implement this is to
    // HAVE AN OBJECT HERE ALREADY, and then defer to MethodAnalyser again.
    // Type system still needs a bit of a kicking, better await some use cases.
    return false;
  }

}
