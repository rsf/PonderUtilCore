/*
 * Created on 7 Nov 2006
 */
package uk.org.ponder.util;

/** A version of java.lang.Runnable that is permitted to throw any checked
 * exception. Useful in adapting API methods to modern unchecked exception
 * semantics. 
 * @see ExceptionRunnableAdapter
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *
 */
public interface ExceptionRunnable {
  public void run() throws Exception;
}
