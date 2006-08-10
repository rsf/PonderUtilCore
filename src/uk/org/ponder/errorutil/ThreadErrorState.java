/*
 * Created on Nov 23, 2004
 */
package uk.org.ponder.errorutil;

import uk.org.ponder.hashutil.EighteenIDGenerator;
import uk.org.ponder.hashutil.IDGenerator;
import uk.org.ponder.util.Logger;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class ThreadErrorState {
  /** An ID generator used to assign ids to error messages, for easy
   * location in logs.
   */
  public static IDGenerator idgenerator = new EighteenIDGenerator(); 
  
  private static ThreadLocal errormap = new ThreadLocal() {
    public Object initialValue() {
      Logger.log.warn("ThreadErrorState allocated for thread " + Thread.currentThread());
      return new ErrorStateEntry();
    }
  };
   
  public static ErrorStateEntry getErrorState() {
    return (ErrorStateEntry) errormap.get();
  }
  
  /** Determines whether any errors have occured during the processing of 
   * the current request.
   */
  public static boolean isError() {
    return getErrorState().errors.isError();
  }
  
  // Idea is that during POST processing, this will be full of messages
  // keyed by field path, but during GET processing, keyed by component ID. 
  public static void addError(TargettedMessage message) {
    ErrorStateEntry ese = getErrorState();
    if (ese.errorid == null) {
      ese.errorid = idgenerator.generateID();
    }
    getErrorState().errors.addMessage(message);
  }
  
  public static void beginRequest() {
    errormap.set(new ErrorStateEntry());
  }
  
  public static void endRequest() {
    errormap.set(null);
  }
}
