/*
 * Created on Dec 22, 2004
 */
package uk.org.ponder.transaction;

/**
 * Intented to wrap the delivery point of a flat transaction.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface TransactionFactory {
  public Transaction beginTransaction();
}
