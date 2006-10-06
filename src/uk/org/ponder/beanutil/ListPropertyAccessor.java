/*
 * Created on 6 Oct 2006
 */
package uk.org.ponder.beanutil;

import uk.org.ponder.saxalizer.mapping.ContainerTypeRegistry;

public class ListPropertyAccessor implements PropertyAccessor {

  private ContainerTypeRegistry ctr;
  
  public boolean canGet(String name) {
    return true;
  }

  public boolean canSet(String name) {
    return true;
  }

  public Object getProperty(Object parent, String name) {
    int index = Integer.parseInt(name);
    // TODO Auto-generated method stub
    return null;
  }

  public Class getPropertyType(Object parent, String name) {
    return ctr.getContaineeType(parent.getClass());
  }

  public boolean isMultiple(Object parent, String name) {
    // TODO Auto-generated method stub
    return false;
  }

  public void setProperty(Object parent, String name, Object value) {
    // TODO Auto-generated method stub
    
  }

  public void unlink(Object parent, String name) {
    // TODO Auto-generated method stub
    
  }

}
