/*
 * Created on 21 Feb 2008
 */
package uk.org.ponder.json;

import java.io.OutputStream;
import java.io.Writer;

import uk.org.ponder.streamutil.write.OutputStreamPOS;
import uk.org.ponder.streamutil.write.PrintOutputStream;
import uk.org.ponder.streamutil.write.WriterPOS;
import uk.org.ponder.stringutil.CharWrap;

public class JSONWriter {
  /**
   * The default encoding to be used when writing byte streams - currently the
   * UTF-8 encoding
   */
  public static final String DEFAULT_ENCODING = "UTF-8";
  
  private PrintOutputStream internalwriter;

  public PrintOutputStream getInternalWriter() {
    return internalwriter;
  }

  /**
   * Creates an XMLWriter wrapping the supplied OutputStream. Character data is
   * converted using the default encoding scheme above
   * 
   * @param os The output stream to be wrapped.
   */

  public JSONWriter(OutputStream os) {
    internalwriter = new OutputStreamPOS(os, DEFAULT_ENCODING);
  }

  /**
   * Creates an XMLWriter wrapping the supplied Writer.
   * @param internalwriter
   *          The writer to be wrapped.
   */

  public JSONWriter(Writer internalwriter) {
    this.internalwriter = new WriterPOS(internalwriter);
  }

  public JSONWriter(PrintOutputStream pos) {
    this.internalwriter = pos;
  }
  
  public JSONWriter writeRaw(String tag) {
    internalwriter.print(tag);
    return this;
  }
  
  public void write(String towrite) {
    char[] array = (towrite == null ? "null" : towrite).toCharArray();
    write(array, 0, array.length);
  }
  
  // http://www.ietf.org/rfc/rfc4627.txt
  
  public final void write(char[] towrite, int start, int length) {
    int limit = start + length;
    for (; length > 0; --length) {
      char c = towrite[limit - length];
      if (c == '"' || c == '\\' || c == '/') break;
    }
    internalwriter.write(towrite, start, limit - start - length);
    if (length > 0) {
      writeSlow(towrite, start + limit - length, length);
    }
  }

  public final void writeSlow(char[] towrite, int start, int length) {
    // AMAZINGLY, in 1.5 it is quicker to create this here than economise it.
    CharWrap svb = new CharWrap(length + 10);
    int limit = start + length;
    for (int i = length; i > 0; --i) {
      char c = towrite[limit - i];
      switch (c) {
      case '"': case '\\': case '/':
        svb.append('\\');
        svb.append(c);
        svb.ensureCapacity(svb.size + i);
        break;
      default:
        svb.appendFast(c);
      }
    }
    internalwriter.write(svb.storage, svb.offset, svb.size);
  } 
}
