/*
 * Created on 7 Nov 2006
 */
package uk.org.ponder.util;

public class ExceptionRunnableAdapter {
  private String message;

  public void setExtraExceptionMessage(String message) {
    this.message = message;
  }
  public Class exceptionclass;
  
  public void invoke(ExceptionRunnable erunnable) {
    try {
      erunnable.run();
    }
    catch (Exception e) {
      throw message == null? 
          UniversalRuntimeException.accumulate(e):
          UniversalRuntimeException.accumulate(e, message);
    }
  }
}
