/*
 * Created on Nov 5, 2004
 */
package uk.org.ponder.transaction;


/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class TransactionThreadMap {
  private static ThreadLocal transmap = new ThreadLocal() {};
  // this is currently only called by NestedTransactionWrapper constructor
  public static void enterTransaction(Transaction tran) {
    transmap.set(tran);
  }
  public static Transaction getTransaction() {
    return (Transaction) transmap.get();
  }
  
  public static void endTransaction() {
    transmap.set(null);
  }
}
