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
  StringWriter sw = new StringWriter();
  public ErrorPrintWriter() {
    super(new StringWriter());
    sw = (StringWriter) out; // recover our argument! Since this is mysteriously
    // forbidden by super rules.
  }
  public void dispose(String err) {
    close();
    String errorstring = sw.toString();
    if (errorstring.length() > 0) {
      throw new UniversalRuntimeException(err + ": " + errorstring);
    }
  }
}
