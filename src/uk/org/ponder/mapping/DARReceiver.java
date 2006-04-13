/*
 * Created on Nov 22, 2004
 */
package uk.org.ponder.mapping;

/**
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
