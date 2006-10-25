/*
 * Created on 25 Oct 2006
 */
package uk.org.ponder.util;

/** 
 * Invokes a Runnable's "run" method, with the possible addition of some
 * logic either before or after (or both) the invokation. In terms of AOP
 * programming this is a pure-JDK equivalent of an "Interceptor", which may
 * be used to implement an "execute around Advice".
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *
 */

public interface RunnableInvoker {
  public void invokeRunnable(Runnable torun);
}
