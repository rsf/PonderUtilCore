/*
 * Created on Oct 7, 2004
 */
package uk.org.ponder.transaction;


/**
 * <code>NullTransaction</code> objects are returned by resources that
 * do not provide any transaction support.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class NullTransaction implements Transaction {
  private NullTransaction() {}
  public void commit() {
  }

  public void rollback() {
  }
  public static NullTransaction instance = new NullTransaction();
}
