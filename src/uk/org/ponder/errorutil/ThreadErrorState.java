/*
 * Created on Nov 23, 2004
 */
package uk.org.ponder.errorutil;

import uk.org.ponder.hashutil.EighteenIDGenerator;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class ThreadErrorState {
  private static ThreadLocal errormap = new ThreadLocal();
  private static EighteenIDGenerator idgenerator = new EighteenIDGenerator();
  
  public static ErrorStateEntry getErrorState() {
    return (ErrorStateEntry) errormap.get();
  }
  
  public static void addError(TargettedMessage message) {
    ErrorStateEntry ese = getErrorState();
    if (ese == null) {
      ese = new ErrorStateEntry();
      ese.tokenid = idgenerator.generateID();
      errormap.set(ese);
    }
    getErrorState().addMessage(message);
  }
  
  public static void clear() {
    errormap.set(null);
  }

  public static void setErrorState(ErrorStateEntry cachedese) {
    errormap.set(cachedese);
  }
}
