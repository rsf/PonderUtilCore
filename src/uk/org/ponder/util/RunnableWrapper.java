/*
 * Created on Jun 21, 2005
 */
package uk.org.ponder.util;

/**
 * RunnableWrapper is now deprecated - use {@link RunnableInvoker} instead.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface RunnableWrapper {
  public Runnable wrapRunnable(Runnable towrap);
}
