/*
 * Created on 10-Jan-2006
 */
package uk.org.ponder.beanutil.entity;

/** A null implementation of EntityIDProcessor to represent an ORM 
 * solution with unchanging object IDs of String type.
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */
public class NullEntityIDProcessor implements EntityIDRewriter {

  public void postParse(EntityID toadjust) {
  }

  public void preRender(EntityID toadjust) {
  }

  public void postCommit(EntityID toadjust) {
  }

}
