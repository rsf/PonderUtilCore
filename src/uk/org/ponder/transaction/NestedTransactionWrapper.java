/*
 * Created on Nov 10, 2004
 */
package uk.org.ponder.transaction;

import java.util.logging.Level;

import uk.org.ponder.util.Logger;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
public class NestedTransactionWrapper implements Transaction {
  private Transaction target;
  private Transaction listener;

  
  /** Given factories for the main target and listener "flat" transactions
   * that will be wrapped by the required NestedTransactionFactory,
   * inspect the current threadmap and return any existing NestedTransaction
   * which is in progress with its use count updated, or create a new one
   * using the factories if requred and store it.
   * @param mainfactory A factory producing the main logic flat transaction
   * to be wrapped.
   * @param listenerfactory A factory producing a "listener" transaction
   * that requires (synchronous) notification AFTER successful conclusion 
   * of transation events. This parameter may be <code>null</code>
   */
  public static NestedTransactionWrapper beginNestedTransaction(
      TransactionFactory mainfactory, TransactionFactory listenerfactory) {
    NestedTransactionWrapper togo = (NestedTransactionWrapper) TransactionThreadMap
        .getTransaction();
    if (togo == null) {
      Transaction nested = mainfactory.beginTransaction();
      togo = new NestedTransactionWrapper(nested, listenerfactory == null? null : listenerfactory
          .beginTransaction());
    }
    else {
      togo.increment();
    }
    return togo;
  }

  /**
   * Accepts two Transactions objects, the first being the "important" concrete
   * transaction object returned by the application, the second being a
   * subsidiary listener. Both will be notified of transaction events, the
   * listener receiving notification second and with lower priority in the case
   * of exceptions.
   * 
   * @param target
   * @param listener
   */
  public NestedTransactionWrapper(Transaction target, Transaction listener) {
    this.target = target;
    this.listener = listener;
    TransactionThreadMap.enterTransaction(this);
  }

  static final int ROLLED_BACK = -1;

  int nestingdepth = 1;

  public void increment() {
    ++nestingdepth;
  }

  public void commit() {
    // A commit has no effect except at the very highest stack level attempting.
    // Two-phase commit is not supported, to the extent that any exception
    // occuring within a commit will inevitably cause all correct client
    // code to perform a rollback within a finally block. No point writing
    // any extra code here to deal with it.
    if (nestingdepth <= 0) {
      throw new UniversalRuntimeException(
          "Error: attempting to inactive transaction with count "
              + nestingdepth);
    }
    --nestingdepth;
    if (nestingdepth == 0) {
      try {
        target.commit();
        TransactionThreadMap.endTransaction();
        if (listener != null) {
          listener.commit();
        }
      }
      catch (Throwable t) {
        throw UniversalRuntimeException.accumulate(t,
            "Error committing transaction");
      }
    }
  }

  public void rollback() {
    // Having rolled back the transaction once, it is removed from the map
    // so that a fresh transaction may be started. However, further people
    // up the stack may attempt independently to roll the transaction back
    // again from their local copies, this will have no further effect.
    if (nestingdepth != ROLLED_BACK) {
      try {
        target.rollback();
      }
      catch (Throwable t) {
        // THIS METHOD SHOULD NEVER THROW!!!
        Logger.log.log(Level.SEVERE, "Error rolling back transaction");
      }
      finally {
        nestingdepth = ROLLED_BACK;
        TransactionThreadMap.endTransaction();
        if (listener != null) {
          listener.rollback();
        }
      }
    }
  }

  public String toString() {
    return target.toString();
  }

  public int hashCode() {
    return target.hashCode();
  }

  // we can compare equal either to another nested wrapper mapping
  // the same target, or the same target itself.
  public boolean equals(Object other) {
    if (other instanceof NestedTransactionWrapper) {
      return target.equals(((NestedTransactionWrapper)other).target);
    }
    else return target.equals(other);
  }
}