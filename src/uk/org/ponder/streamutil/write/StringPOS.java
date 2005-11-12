/*
 * Created on Sep 15, 2005
 */
package uk.org.ponder.streamutil.write;

import uk.org.ponder.stringutil.CharWrap;

public class StringPOS implements PrintOutputStream {
  private CharWrap buffer = new CharWrap();

  public void println(String toprint) {
    print(toprint);
    print("\n");
  }

  public void flush() {
  }

  public void close() {
  }

  public PrintOutputStream print(String string) {
    buffer.append(string);
    return this;
  }

  public void println() {
    println("");
  }

  public void println(Object obj) {
    println(obj.toString());
  }

  public String toString() {
    return buffer.toString();
  }

  public void write(char[] storage, int offset, int size) {
   buffer.append(storage, offset, size);
  }
}
