/*
 * Created on Dec 9, 2004
 */
package uk.org.ponder.servletutil;

import uk.org.ponder.stringutil.StringList;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
public class ErrorObject {
  public String request;
  public String handlerID;
  public StringList stacktrace = new StringList();

  public ErrorObject(String request, String handlerID, Throwable t) {
    this.request = request;
    this.handlerID = handlerID;
    if (t != null) {
      StackTraceElement[] trace = t.getStackTrace();
      for (int i = 0; i < trace.length; ++i) {
        stacktrace.add(trace[i].toString());
      }
    }
  }
}