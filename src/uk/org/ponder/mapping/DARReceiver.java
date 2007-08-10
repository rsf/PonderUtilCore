/*
 * Created on Nov 22, 2004
 */
package uk.org.ponder.mapping;

import uk.org.ponder.beanutil.BeanModelAlterer;

/**
 * Implemented by a bean which wishes to take full manual control of application
 * of {@link DataAlterationRequest} values, rather than having these applied
 * immediately and reflectively. These may be pent up, and then applied later
 * using a {@link BeanModelAlterer}, or filtered and introspected in some other
 * manner.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface DARReceiver {
  /** Adds the supplied AlterationRequest into the target's collection
   * (probably for later replay).
   * @return <code>false</code> if the DAR was NOT added, but should be
   * applied directly to the target.
   */
  public boolean addDataAlterationRequest(DataAlterationRequest toadd);
}
