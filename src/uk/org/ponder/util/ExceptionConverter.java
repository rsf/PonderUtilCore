/*
 * Created on 7 Nov 2006
 */
package uk.org.ponder.util;

public interface ExceptionConverter {
  public RuntimeException convertException(Exception e);
}
