/*
 * Created on Aug 5, 2005
 */
package uk.org.ponder.errorutil;

import uk.org.ponder.streamutil.PrintOutputStream;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class ErrorUtil {
  public static void dumpStackTrace(Throwable t, PrintOutputStream pos) {
    pos.println(t.getMessage());
    StackTraceElement[] elements = t.getStackTrace();
    for (int i = 0; i < elements.length; ++ i) {
      pos.println(elements[i]);
    }
  }
}
