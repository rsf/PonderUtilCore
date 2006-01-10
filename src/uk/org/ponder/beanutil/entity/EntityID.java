/*
 * Created on 09-Jan-2006
 */
package uk.org.ponder.beanutil.entity;

import java.io.Serializable;

/** Represents the locator for an "entity", probably provided by some ORM
 * mapping solution. We had to make this a proper bean and not a bean since
 * the "clazz" field needs to be Spring addressible. The "ID" field will be
 * in String form after being parsed - objects of this class will be automatically
 * passed through an "EntityIDProcessor" before significant events in their
 * lifetimes.
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public class EntityID {
  public Class clazz;
  public Serializable id;

  public void setClazz(Class entityclass) {
    this.clazz = entityclass;
  }
  public void setID(Serializable id) {
    this.id = id;
  }
  
  public Serializable getID() {
    return id;
  }
  public Class getClazz() {
    return clazz;
  }
}
