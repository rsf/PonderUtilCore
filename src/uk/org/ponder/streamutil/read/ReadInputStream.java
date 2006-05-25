/*
 * Created on Nov 11, 2005
 */
package uk.org.ponder.streamutil.read;

import java.io.IOException;

/** A slimline, checked-exception-free INTERFACE replacing java.io.Reader */

public interface ReadInputStream {
  public static final char EOF = (char) - 1;
  /** Reads a single character from the stream and returns it. If the stream
   * has reached the end of file, return the value (char)-1.
   * @return The character that was read.
   * @throws IOException If a read error other than EOF occurs.
   */
  public char get();
  public int read(char[] target, int start, int length);
  /** Checks if end of the wrapped stream has been reached.
   * @return <code>true</code> if the stream has reached the end of file.
   */
  public boolean EOF();
  /** A close method that will throw NO exception */
  public void close();
}
