/*
 * Created on 20-Mar-2006
 */
package uk.org.ponder.streamutil.read;

import java.io.Reader;

import uk.org.ponder.streamutil.StreamCloseUtil;
import uk.org.ponder.util.UniversalRuntimeException;

public class ReaderRIS implements ReadInputStream {

  private Reader reader;
  boolean eof = false;

  public ReaderRIS(Reader reader) {
    this.reader = reader;
  }
  
  public char get() {
    try {
      int read = reader.read();
      if (read == -1) {
        eof = true;
      }
      return (char) read;
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e, "Error reading from Reader ");
    }
  }

  public int read(char[] target, int start, int length) {
    try {
      return reader.read(target, start, length);
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e, "Error reading from Reader ");
    }
  }

  public boolean EOF() {
    return eof;
  }

  public void close() {
    StreamCloseUtil.closeReader(reader);
  }
}
