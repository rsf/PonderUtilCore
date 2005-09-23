package uk.org.ponder.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import uk.org.ponder.streamutil.OutputStreamPOS;
import uk.org.ponder.streamutil.PrintOutputStream;
import uk.org.ponder.streamutil.WriterPOS;
import uk.org.ponder.stringutil.CharWrap;
import uk.org.ponder.util.Logger;

/**
 * A utility class to write XML data either raw or with XML/HTML entity
 * escaping. An XMLWriter wraps either an OutputStream or a Writer, and supplies
 * methods with similar names to <code>Writer</code> which escape all
 * mandatory XML/HTML entities, and methods named with suffix <code>Raw</code>
 * which write the data without transformation.
 */

public class XMLWriter {
  /**
   * The number of characters to indent by for each nesting level of a tag to be
   * written. Some <code>writeRaw</code> methods accept a nesting level used
   * as a multiplier for this factor.
   */
  public static final int INDENT_WIDTH = 2;
  // Sun are evil cretins who do not call UTF-8 by its proper name
  /**
   * The default encoding to be used when writing byte streams - currently the
   * UTF-8 encoding
   */
  public static final String DEFAULT_ENCODING = "UTF-8";
  private static String DEFAULT_DECLARATION = "<?xml version=\"1.0\" ?>\n";
  private PrintOutputStream internalwriter;

  /**
   * Creates an XMLWriter wrapping the supplied OutputStream. Character data is
   * converted using the default encoding scheme above
   * 
   * @param os
   *          The output stream to be wrapped.
   */

  public XMLWriter(OutputStream os) {
    internalwriter = new OutputStreamPOS(os, DEFAULT_ENCODING);
  }

  /**
   * Creates an XMLWriter wrapping the supplied Writer.
   * 
   * @param internalwriter
   *          The writer to be wrapped.
   */

  public XMLWriter(Writer internalwriter) {
    this.internalwriter = new WriterPOS(internalwriter);
  }

  public XMLWriter(PrintOutputStream pos) {
    this.internalwriter = pos;
  }

  /**
   * Writes the supplied data to the wrapped stream without conversion.
   * 
   * @param towrite
   *          A character array holding the data to be written.
   * @param start
   *          The offset of the data to be written within the array.
   * @param length
   *          The length of the data to be written.
   * @exception IOException
   *              If an I/O error occurs while writing the data.
   */

  public void writeRaw(char[] towrite, int start, int length) {
    internalwriter.write(towrite, start, length);
  }

  /**
   * Writes the supplied string to the wrapped stream without conversion.
   * 
   * @param tag
   *          The string to be written.
   * @exception IOException
   *              If an I/O error occurs while writing the string.
   */

  public XMLWriter writeRaw(String tag) {
    internalwriter.print(tag);
    return this;
  }

  public static void indent(int nestinglevel, PrintOutputStream writer) {
    for (int i = 0; i < nestinglevel * INDENT_WIDTH; ++i) {
      writer.print(" ");
    }
  }

  // write with specified indenting and without deentitising
  /**
   * Writes the supplied string to the wrapped stream with the specified indent
   * level.
   * 
   * @param tag
   *          The string to be written.
   * @param nestinglevel
   *          The multiplier for the <code>INDENT_WIDTH</code>, giving the
   *          number of spaces to be written before the supplied string.
   * @exception IOException
   *              If an I/O error occurs while writing the string.
   */
  public void writeRaw(String tag, int nestinglevel) {
    indent(nestinglevel, internalwriter);
    internalwriter.print(tag);
    // Logger.println(tag, Logger.DEBUG_SUBATOMIC);
  }

  public void closeTag(String tag, int nestinglevel, boolean writtenchildren) {
    if (writtenchildren) {
      indent(nestinglevel, internalwriter);
      internalwriter.print("</");
      internalwriter.print(tag);
      internalwriter.print(">");
    }
    else {
      internalwriter.print("/>");
    }
    if (nestinglevel >= 0) {
      internalwriter.print("\n");
    }
  }

  /**
   * Returns the default declaration that will be written by the
   * <code>writeDeclaration</code> method.
   * 
   * @return The required default declaration.
   */
  public static String getDefaultDeclaration() {
    return DEFAULT_DECLARATION;
  }

  /**
   * Writes a default declaration to the wrapped stream.
   * 
   * @exception IOException
   *              If an I/O error occurs while writing the declaration.
   */

  public void writeDeclaration() {
    internalwriter.print(DEFAULT_DECLARATION);
  }

  /**
   * Writes the supplied data to the wrapped stream, escaping all mandatory
   * XML/HTML entities, being &amp;, &lt;, &gt;, &quot and &#39;. &#39; is
   * escaped to &amp;#39; since HTML 4.0 does not define the &amp;apos; entity
   * and does not plan to.
   * 
   * @param towrite
   *          A character array holding the data to be written.
   * @param start
   *          The offset of the data to be written within the array.
   * @param length
   *          The length of the data to be written.
   * @exception IOException
   *              If an I/O error occurs while writing the data.
   */

  public void write(char[] towrite, int start, int length) {
    CharWrap svb = new CharWrap();
    for (int i = 0; i < length; ++i) {
      char c = towrite[start + i];
      if (c == '&') {
        svb.append("&amp;");
      }
      else if (c == '<') {
        svb.append("&lt;");
      }
      else if (c == '>') {
        svb.append("&gt;");
      }
      else if (c == '"') {
        svb.append("&quot;");
      }
      else if (c == '\'') {
        // HTML 4.0 does not define &apos; and does not plan to
        svb.append("&#39;");
      }
      else
        svb.append(c);
    }
    internalwriter.write(svb.storage, svb.offset, svb.size);
  }

  /**
   * Writes the supplied data to the wrapped stream, escaping all mandatory
   * XML/HTML entities, being &amp;, &lt;, &gt;, &quot and &#39;. &#39; is
   * escaped to &amp;#39; since HTML 4.0 does not define the &amp;apos; entity
   * and does not plan to.
   * 
   * @param towrite
   *          The string to be written.
   * @exception IOException
   *              If an I/O error occurs while writing the string.
   */

  public void write(String towrite) {
    char[] array = towrite.toCharArray();
    write(array, 0, array.length);
  }

  /**
   * Flushes the wrapped stream.
   * 
   * @exception IOException
   *              If an I/O error occurs while flushing the stream.
   */

  public void flush() {
    internalwriter.flush();
  }

  /**
   * Closes this XMLWriter object, in effect flushing it and making it unusable
   * for any further write operations.
   * <p>
   * Closing this does not close the underlying input stream!
   * 
   * @exception IOException
   *              If an I/O error occurs while closing the stream.
   */
  public void close() {
    if (internalwriter != null) {
      try {
        flush();
      }
      catch (Throwable t) {
        Logger.println("Unhandled exception closing XML Writer: " + t,
            Logger.DEBUG_SEVERE);
        // internalwriter.close();
        internalwriter = null;
      }
    }
  }
}
