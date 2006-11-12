/*
 * Created on 7 Nov 2006
 */
package uk.org.ponder.util;

/** A simple RunnableInvoker that converts any incoming exceptions by 
 * replacing their message with a supplied String. By standard RSF semantics,
 * if this is applied to a bean write operation, the String value will be used
 * as a key to locate a message from the current message bundle, 
 * when it results in a TargettedMessage entry.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *
 */

public class StaticExceptionConvertingRI implements RunnableInvoker {
  private String message;
  private Class clazz = IllegalArgumentException.class;

  public void setMessage(String message) {
    this.message = message;
  }
  
  public void setExceptionClass(Class clazz) {
    this.clazz = clazz;
  }

  public void invokeRunnable(Runnable torun) {
    try {
      torun.run();
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulateMsg(e, clazz, message);
    }
  }
}
