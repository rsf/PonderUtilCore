/*
 * Created on 21-Feb-2006
 */
package uk.org.ponder.util;

public interface CompletableDenumeration extends Denumeration {
  /** Called to deliver a "composite" object (currently an array) holding
   * all the elements thus far denumerated.
   * @return A composite of all denumerated objects.
   */
  public Object complete();
}
