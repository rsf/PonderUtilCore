/*
 * Created on 16-Jun-2004
 */
package uk.org.ponder.streamutil;

import java.io.PrintWriter;
import java.io.StringWriter;

import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Bosmon
 *
 * The class 
 */
public class ErrorPrintWriter extends PrintWriter {
  
  private static ThreadLocal threadwriter = new ThreadLocal();
  
  public static void setWriter(PrintWriter pw) {
    threadwriter.set(pw);
  }
  
  public static PrintWriter getWriter() {
    return (PrintWriter) threadwriter.get();
  }
  
  StringWriter sw = new StringWriter();
  public ErrorPrintWriter() {
    super(new StringWriter());
    sw = (StringWriter) out; // recover our argument! Since this is mysteriously
    setWriter(this);
    // forbidden by super rules.
  }
  public void dispose(String err) {
    setWriter(null);
    close();
    String errorstring = sw.toString();
    if (errorstring.length() > 0) {
      throw new UniversalRuntimeException(err + ": " + errorstring);
    }
  }
}
