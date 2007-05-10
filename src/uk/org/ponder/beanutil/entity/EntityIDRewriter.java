/*
 * Created on 09-Jan-2006
 */
package uk.org.ponder.beanutil.entity;

/** Interface implemented by an ORM adaptation layer to keep bean values
 * respresenting entity IDs consistent.
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public interface EntityIDRewriter {
  /** Called after changes to the object model have been commited to 
   * persistent state - the ID field will need to be adjusted if it referred
   * to a previously unsaved entity.
   * @param toadjust
   */
  public void postCommit(EntityID toadjust);
}
