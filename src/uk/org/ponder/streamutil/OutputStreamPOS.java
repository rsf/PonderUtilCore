/*
 * Created on Sep 15, 2005
 */
package uk.org.ponder.streamutil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import uk.org.ponder.util.UniversalRuntimeException;

/**
 * A combination of OutputStreamWriter and BufferedWriter - converts and outputs
 * characters to an OutputStream with the minimum of fuss.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */

public class OutputStreamPOS implements PrintOutputStream {
  public int BUFFER_MAX = 1024;
  private OutputStream os;
  private static String DEFAULT_ENCODING = "UTF-8";
  private CharBuffer charbuffer;
  private char[] bufchars;
  private CharsetEncoder ce;
  private ByteBuffer bytebuffer;

  public OutputStreamPOS(OutputStream os) {
    this(os, DEFAULT_ENCODING);
  }

  public OutputStreamPOS(OutputStream os, String encoding) {
    this.os = os;
    Charset cs = Charset.forName(encoding);
    ce = cs.newEncoder();
    charbuffer = CharBuffer.allocate(BUFFER_MAX);
    bufchars = charbuffer.array();
    allocateByteBuffer(charbuffer.capacity());
  }

  private void allocateByteBuffer(int chars) {
    int reqsize = (int) (chars * ce.maxBytesPerChar());
    if (bytebuffer == null || bytebuffer.capacity() < reqsize) {
      bytebuffer = ByteBuffer.allocate(reqsize * 2);
    }
  }

  public void println(String toprint) {
    print(toprint);
    print("\n");
  }

  public void flush() {
    try {
      flushInternal();
      os.flush();
    }
    catch (IOException e) {
      throw UniversalRuntimeException.accumulate(e);
    }
  }

  public void close() {
    flush();
    StreamCloseUtil.closeOutputStream(os);
  }

  private void flushInternal() {
    bytebuffer.position(0);
    charbuffer.limit(charbuffer.position());
    charbuffer.position(0);
    // deal with EOI and errors better at some point.
    ce.encode(charbuffer, bytebuffer, true);
    charbuffer.position(0);
    charbuffer.limit(charbuffer.capacity());
    try {
      os.write(bytebuffer.array(), 0, bytebuffer.position());
    }
    catch (IOException e) {
      throw UniversalRuntimeException.accumulate(e);
    }
  }

  public PrintOutputStream print(String string) {
//    byte[] bytes;
//    try {
//      bytes = string.getBytes(encoding);
//      os.write(bytes);
//    }
//    catch (Exception e) {
//      e.printStackTrace();
//    }
    if (string == null) string = "null";
    int stringlength = string.length();
    int stringpos = 0;
    
    while (true) {
      int remaining = charbuffer.remaining();
      int towrite = stringlength - stringpos;
      if (towrite > remaining) {
        towrite = remaining;
      }
      int bufpos = charbuffer.position();
      string.getChars(stringpos, stringpos + towrite, bufchars, bufpos);
      stringpos += towrite;
      charbuffer.position(bufpos + towrite);
      if (stringpos == stringlength)
        break;
      flushInternal();
    }

    return this;
  }

  public void write(char[] storage, int offset, int size) {
  // TODO: optimise this someday.
    String towrite = new String(storage, offset, size);
    print(towrite);
  }

  public void println() {
    print("\n");
  }

  public void println(Object obj) {
    print(obj.toString());
    print("\n");
  }

}
