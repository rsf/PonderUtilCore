/*
 * Created on Oct 6, 2004
 */
package uk.org.ponder.transaction;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface Transaction {
  public void commit();
  public void rollback();
}
