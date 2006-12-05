/*
 * Created on 09-Jan-2006
 */
package uk.org.ponder.beanutil.entity;

/** Represents the locator for an "entity", probably provided by some ORM
 * mapping solution. The ID field represents the ID of a particular INSTANCE
 * of this Entity class, and entityname represents the nickname (entity name,
 * bean path name) of this CLASS of entity. There is special support in RSF
 * for rewriting elements of this type at top-level of a ViewParameters 
 * returned as ARIResult.resultingview.
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public class EntityID {
  public EntityID(String entityname, String ID) {
    this.entityname = entityname;
    this.ID = ID;
  }
  public EntityID() {}
  
  public String entityname;
  // Will be parsed into correct class by BeanLocator
  public String ID;
}
