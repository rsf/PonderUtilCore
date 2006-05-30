/*
 * Created on May 28, 2006
 */
package uk.org.ponder.saxalizer.mapping;

import java.util.HashMap;

public class ContainerTypeRegistry {

  private HashMap collectionmap = new HashMap();

  public Class getContaineeType(Class collectiontype) {
    if (collectiontype.isArray()) {
      Class component = collectiontype.getComponentType();
      if (!component.isPrimitive()) return component;
    }
    return (Class)collectionmap.get(collectiontype);
  }
  
  
  public void addCollectionType(Class collectiontype, Class containeetype) {
    collectionmap.put(collectiontype, containeetype);
  }
}
