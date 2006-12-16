/*
 * Created on Jun 2, 2006
 */
package uk.org.ponder.saxalizer;

import uk.org.ponder.beanutil.BeanLocator;
import uk.org.ponder.beanutil.WriteableBeanLocator;

public class WBLAccessMethod implements AccessMethod {
  private String propname;
  private Class clazz;

  public WBLAccessMethod(Class targetClass, String propname) {
	this.propname = propname;
	this.clazz = targetClass;
  }

  public String getPropertyName() {
    return propname;
  }

  public Object getChildObject(Object parent) {
    return ((BeanLocator)parent).locateBean(propname);
  }

  public void setChildObject(Object parent, Object newchild) {
    ((WriteableBeanLocator)parent).set(propname, newchild);
  }

  public boolean canGet() {
    return true;
  }

  public boolean canSet() {
    return true;
  }

  public boolean isEnumeration() {
    return false;
  }

  public boolean isDenumerable() {
    return false;
  }

  public Class getAccessedType() {
    return Object.class;
  }

  public Class getDeclaredType() {
    return Object.class;
  }

  public Class getDeclaringClass() {
	return clazz;
  }

}
