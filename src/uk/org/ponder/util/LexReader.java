/*
 * Created on 07-Oct-2003
 */
package uk.org.ponder.util;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

/**
 * A PushbackReader with exactly one byte of pushback, which silently swallows any
 * EOF exceptions. Instead, EOF is reported by returning the value (char)-1 which
 * Unicode defines to be nothing useful. EOF may be independently verified by 
 * checking the EOF() method.
 * @author Bosmon
 */
public class LexReader extends PushbackReader {
  private int read = 0;
  private boolean EOF = false;
  /** Creates a LexReader wrapping the supplied Reader */
  public LexReader(Reader r) {
    super(r);
  }
  /** Checks if end of the wrapped stream has been reached.
   * @return <code>true</code> if the stream has reached the end of file.
   */
  public boolean EOF() {
    return EOF;
  }
  /** Reads a single character from the stream and returns it. If the stream
   * has reached the end of file, return the value (char)-1.
   * @return The character that was read.
   * @throws IOException If a read error other than EOF occurs.
   */
  public char get() throws IOException {
    int c = read();
    if (c != -1) {
      ++read;
    }
    else {
      EOF = true;
    }
    return (char)c;
  }
}
