package uk.org.ponder.streamutil;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import uk.org.ponder.util.Logger;

/** This class abstracts the idea of an array of bytes, and handles the task of 
 * converting it (the array, not the idea) to and from input and output streams. 
 */

public class BytePen {
  private InputStream stashed;
  private byte[] bytes;
  /** Returns an output stream to which the byte array contents can be written.
   * @return An output stream to which the byte array contents can be written.
   */
  public OutputStream getOutputStream() {
    final ByteArrayOutputStream togo = new ByteArrayOutputStream();
    OutputStream toreallygo = new TrackerOutputStream
      (togo, new StreamClosedCallback() {
	  public void streamClosed(Object o) {
	    Logger.println("BytePen OutputStream closed", Logger.DEBUG_INFORMATIONAL);
	    bytes = togo.toByteArray();
	    }
	  });
    return toreallygo;
    }
 
  /** Sets the input stream from which the byte array can be read.
   * @param is The input stream from which the byte array can be read.
   */
  public void setInputStream(InputStream is) {
    stashed = is;
    }

  /** Returns an input stream from which the byte array can be read. If the
   * array was specified via <code>setInputStream</code>, the original input
   * stream will be returned.
   * @return An input stream from which the byte array can be read.
   */

  public InputStream getInputStream() {
    if (stashed != null) {
      InputStream togo = stashed;
      stashed = null;
      return togo;
      }
    else return new ByteArrayInputStream(bytes);
    }

  }
