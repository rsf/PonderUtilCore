/*
 * Created on Nov 5, 2004
 */
package uk.org.ponder.transaction;

import java.util.ArrayList;

import uk.org.ponder.util.UniversalRuntimeException;


/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class TransactionThreadMap {
  // largely for debugging purposes, this keeps a registry of all 
  // TransactionThreadMaps in the system for assertAllConcluded(). 
  private static ArrayList allmaps = new ArrayList();
  
  public TransactionThreadMap() {
    allmaps.add(this);
  }
  
  private ThreadLocal transmap = new ThreadLocal() {};
  // this is currently only called by NestedTransactionWrapper constructor
  public void enterTransaction(Transaction tran) {
    //Logger.log.info("*|*|*|*|*|  Thread " + Thread.currentThread() + " entered transaction");
    transmap.set(tran);
  }
  public Transaction getTransaction() {
    return (Transaction) transmap.get();
  }

  public void endTransaction() {
    //Logger.log.info("*|*|*|*|*|   Thread " + Thread.currentThread() + " left transaction");
    transmap.set(null);
  }

  public void assertTransactionsConcluded() {
    Transaction trans = getTransaction();
    if (trans != null) {
      try {
        trans.rollback();
      }
      catch (Exception e) {}
      throw new UniversalRuntimeException("Outstanding transaction " + trans
          + " discovered on thread " + Thread.currentThread());
    }
  }
  /** Throws an exception if this thread has an outstanding transaction on
   * any transaction map in the system.
   */
  public static void assertAllTransactionsConcluded() {
    for (int i = 0; i < allmaps.size(); ++ i) {
      ((TransactionThreadMap)allmaps.get(i)).assertTransactionsConcluded();
    }
  }
  
}