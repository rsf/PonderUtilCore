/*
 * Created on May 28, 2006
 */
package uk.org.ponder.saxalizer.mapping;

import java.util.HashMap;

import uk.org.ponder.arrayutil.TypedListWrapper;

public class ContainerTypeRegistry {

  private HashMap collectionmap = new HashMap();

  public Class getContaineeType(Object container) {
    Class collectiontype = container instanceof Class ? (Class) container
        : container.getClass();
    if (collectiontype.isArray()) {
      Class component = collectiontype.getComponentType();
      if (!component.isPrimitive())
        return component;
    }
    else if (container instanceof TypedListWrapper) {
      return ((TypedListWrapper) container).getWrappedType();
    }
    return (Class) collectionmap.get(collectiontype);
  }

  public void addCollectionType(Class collectiontype, Class containeetype) {
    collectionmap.put(collectiontype, containeetype);
  }
}
