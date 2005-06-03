/*
 * Created on May 19, 2005
 */
package uk.org.ponder.streamutil;

import java.io.IOException;
import java.io.Writer;

import uk.org.ponder.util.UniversalRuntimeException;

/**
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

  public void print(String string) {
    try {
      w.write(string);
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e);
    }
  }

  public void println() {
    print("\n");
  }

  public void println(Object obj) {
    println(obj.toString());
  }

}