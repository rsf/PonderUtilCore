/*
 * Created on Sep 30, 2005
 */
package uk.org.ponder.streamutil;

public class DevNullPOS implements PrintOutputStream{

  public void println(String toprint) {
   }

  public void flush() {
  }

  public void close() {
  }

  public PrintOutputStream print(String string) {
    return this;
  }

  public void println() {
  }

  public void println(Object obj) {
  }

  public void write(char[] storage, int offset, int size) {
  }

}
