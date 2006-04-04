/*
 * Created on Nov 11, 2005
 */
package uk.org.ponder.streamutil.read;

public class StringRIS implements ReadInputStream {
  private String read;
  public int pos = 0;
  public StringRIS(String target) {
    this.read = target;
  }
  /** Reads a single character from the stream and returns it. If the stream
   * has reached the end of file, return the value (char)-1.
   * @return The character that was read.
   */
  public char get() {
    if (pos == read.length()) {
      return EOF;
    }
    else {
      return read.charAt(pos++);
    }
  }
  
  public int read(char[] target, int start, int length) {
    int remain = read.length() - pos;
    if (length > remain) length = remain;
    read.getChars(pos, pos + length, target, start);
    return length;
  }
  
  public boolean EOF() {
    return pos == read.length();
  }
  
  public void close() {
  }
}
