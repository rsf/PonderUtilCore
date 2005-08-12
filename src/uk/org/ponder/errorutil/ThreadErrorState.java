/*
 * Created on Nov 23, 2004
 */
package uk.org.ponder.errorutil;

import uk.org.ponder.hashutil.EighteenIDGenerator;
import uk.org.ponder.webapputil.ViewParameters;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class ThreadErrorState {
  private static ThreadLocal errormap = new ThreadLocal() {
    public Object initialValue() {
      return new RequestStateEntry();
    }
  };
   
  public static RequestStateEntry getErrorState() {
    return (RequestStateEntry) errormap.get();
  }
  
  private static EighteenIDGenerator idgenerator = new EighteenIDGenerator();
  

  public static void initRequest(ViewParameters viewparams) {
    RequestStateEntry rse = ThreadErrorState.getErrorState();
    rse.incomingtokenID = viewparams == null? null : viewparams.viewtoken;
    rse.outgoingtokenID = idgenerator.generateID();
  }
  
  // Idea is that during POST processing, this will be full of messages
  // keyed by field path, but during GET processing, keyed by component ID. 
  public static void addError(TargettedMessage message) {
    getErrorState().errors.addMessage(message);
  }
  
  public static void endRequest() {
    RequestStateEntry rse = ThreadErrorState.getErrorState();
    rse.errors.clear();
  }
}
