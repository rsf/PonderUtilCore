/*
 * Created on Nov 5, 2004
 */
package uk.org.ponder.transaction;

import java.util.logging.Level;

import uk.org.ponder.util.Logger;
import uk.org.ponder.util.UniversalRuntimeException;


/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class TransactionThreadMap {
  private static ThreadLocal transmap = new ThreadLocal() {};
  // this is currently only called by NestedTransactionWrapper constructor
  public static void enterTransaction(Transaction tran) {
    Logger.log.log(Level.INFO, "*|*|*|*|*|  Thread " + Thread.currentThread() + " entered transaction");
    transmap.set(tran);
  }
  public static Transaction getTransaction() {
    return (Transaction) transmap.get();
  }

  public static void endTransaction() {
    Logger.log.log(Level.INFO, "*|*|*|*|*|   Thread " + Thread.currentThread() + " left transaction");
    transmap.set(null);
  }

  public static void assertTransactionsConcluded() {
    Transaction trans = getTransaction();
    if (trans != null) {
      throw new UniversalRuntimeException("Outstanding transaction " + trans
          + " discovered on thread " + Thread.currentThread());
    }
  }
}