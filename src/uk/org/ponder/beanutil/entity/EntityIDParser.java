/*
 * Created on 10-Jan-2006
 */
package uk.org.ponder.beanutil.entity;

public interface EntityIDParser {
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
}
