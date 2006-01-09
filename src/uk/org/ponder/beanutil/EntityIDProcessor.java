/*
 * Created on 09-Jan-2006
 */
package uk.org.ponder.beanutil;

/** Interface implemented by an ORM adaptation layer to keep bean values
 * respresenting entity IDs consistent.
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public interface EntityIDProcessor {
  /** Called after the supplied EntityID is parsed from serializable 
   * representation. The ID field is parsed from String form to the 
   * appropriate type for the referenced entity.
   * @param toadjust
   */
  public void postParse(EntityID toadjust);
  /** Called before the supplied EntityID is rendered into serializable
   * form (e.g. a URL). The ID field is rendered into a String in such a
   * way as to be reversible via postParse().
   * @param toadjust
   */
  public void preRender(EntityID toadjust);
  /** Called after changes to the object model have been commited to 
   * persistent state - the ID field will need to be adjusted if it referred
   * to a previously unsaved entity.
   * @param toadjust
   */
  public void postCommit(EntityID toadjust);
}
