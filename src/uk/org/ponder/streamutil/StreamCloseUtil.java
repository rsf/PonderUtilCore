/*
 * Created on Sep 17, 2004
 */
package uk.org.ponder.streamutil;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import uk.org.ponder.util.Logger;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
public class StreamCloseUtil {
  /**
   * Exceptions should NEVER be thrown from "cleanup" methods. Compensate for
   * this incorrect JDK design with this method. Any exception thrown from the
   * close operation will be swallowed and printed to a log. If this logging
   * operation causes a FURTHER exception, this will itself be swallowed with no
   * further logging attempted...
   * 
   * @param w A Writer to be closed, which may be <code>null</code>.
   */
  public static void closeWriter(Writer w) {
    if (w != null) {
      try {
        w.close();
      }
      catch (Throwable t) {
        try {
          Logger.log.info("Unhandled exception while closing Writer", t);
        }
        catch (Throwable t2) {
        }
      }
    }
  }

  /**
   * See comments for closeWriter.
   * 
   * @param os
   */
  public static void closeOutputStream(OutputStream os) {
    if (os != null) {
      try {
        os.close();
      }
      catch (Throwable t) {
        try {
          Logger.log.info("Unhandled exception while closing OutputStream", t);
        }
        catch (Throwable t2) {
        }
      }
    }
  }

  /**
   * See comments for closeWriter.
   * 
   * @param is
   */
  public static void closeInputStream(InputStream is) {
    if (is != null) {
      try {
        is.close();
      }
      catch (Throwable t) {
        try {
          Logger.log.info("Unhandled exception while closing InputStream", t);
        }
        catch (Throwable t2) {
        }
      }
    }
  }
  
  /**
   * See comments above.
   */
  public static void closeReader(Reader r) {
    if (r != null) {
      try {
        r.close();
      }
      catch (Throwable t) {
        try {
          Logger.log.info("Unhandled exception while closing Reader", t);
        }
        catch (Throwable t2) {
        }
      }
    }
  }
}