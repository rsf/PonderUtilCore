/*
 * Created on May 19, 2005
 */
package uk.org.ponder.streamutil.write;

import java.io.IOException;
import java.io.Writer;

import uk.org.ponder.streamutil.StreamCloseUtil;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * A wrapper to convert a Java Writer object into a PrintOutputStream. All
 * IOExceptions (or others) will be wrapped into unchecked 
 * UniversalRuntimeExceptions, q.v. Note that since Writers have horrible
 * dependence on synchronization, you'd be better off using an OutputStreamPOS.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
public class WriterPOS implements PrintOutputStream {
  private Writer w;

  public WriterPOS(Writer w) {
    this.w = w;
  }

  public void println(String toprint) {
    try {
      w.write(toprint);
      w.write("\n");
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e);
    }
  }

  public void flush() {
    try {
      w.flush();
    }
    catch (IOException e) {
      throw UniversalRuntimeException.accumulate(e);
    }
  }

  public void close() {
    StreamCloseUtil.closeWriter(w);
  }

  public PrintOutputStream print(String string) {
    try {
      w.write(string);
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e);
    }
    return this;
  }

  public void println() {
    print("\n");
  }

  public void println(Object obj) {
    println(obj.toString());
  }

  public void write(char[] storage, int offset, int size) {
    try {
      w.write(storage, offset, size);
    }
    catch (IOException e) {
      throw UniversalRuntimeException.accumulate(e);
    }
  }

}