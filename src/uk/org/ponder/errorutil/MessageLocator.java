/*
 * Created on Dec 3, 2004
 */
package uk.org.ponder.errorutil;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public abstract class MessageLocator {
  public abstract String getMessage(String code, Object[] args);
  public String getMessage(String code) {
    return getMessage(code, null);
  }
  public String getMessage(String code, Object param) {
    return getMessage(code, new Object[] {param});
  }
}