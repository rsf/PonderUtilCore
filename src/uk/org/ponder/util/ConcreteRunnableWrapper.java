/*
 * Created on 24 Oct 2006
 */
package uk.org.ponder.util;

/**
 * A concrete implementation of RunnableWrapper that breaks out the "proceed"
 * operation into a separate method, to adapt the RunnableWrapper paradigm to
 * the AOP interception paradigm. You will typically apply any Interceptors you
 * want embodied by this wrapper onto the "proceed" method.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */

public class ConcreteRunnableWrapper implements RunnableWrapper {

  public Runnable wrapRunnable(final Runnable towrap) {
    return new Runnable() {
      public void run() {
        proceed(towrap);
      }
    };
  }

  public void proceed(Runnable towrap) {
    towrap.run();

  }
}
