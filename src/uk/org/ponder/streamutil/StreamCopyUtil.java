package uk.org.ponder.streamutil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

import uk.org.ponder.stringutil.CharWrap;
import uk.org.ponder.stringutil.CharWrapVector;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * This class contains static utility methods for operating on streams.
 */

public class StreamCopyUtil {
  /**
   * A natural buffer size to be used where some significant processing is
   * applied to streams.
   */
  public static final int PROCESS_BUFFER_SIZE = 4096;
  // a buffer size for internal bulk copying functions
  private static final int BUF_SIZ = 16384;

  /**
   * Copies the supplied input stream to the specified output, closing both
   * streams on completion or error.
   * 
   * @param source
   *          The input stream to be copied.
   * @param dest
   *          The output stream where the input data is to be copied.
   * @exception IOException
   *              if an I/O error occurs.
   */
  public static final void inputToOutput(InputStream source, OutputStream dest,
      byte[] buffer) {
    inputToOutput(source, dest, true, true, buffer);
  }

  /**
   * Copies the supplied input stream to the specified output, allowing the user
   * to specify which of the streams are to be closed on completion or error.
   * 
   * @param source
   *          The input stream to be copied.
   * @param dest
   *          The output stream where the input data is to be copied.
   * @param closeinput
   *          <code>true</code> if the input stream is to be closed on
   *          completion or error.
   * @param closeoutput
   *          <code>true</code> if the output stream is to be closed on
   *          completion or error.
   * @exception IOException
   *              if an I/O error occurs.
   */

  public static final void inputToOutput(InputStream source, OutputStream dest,
      boolean closeinput, boolean closeoutput, byte[] buffer) {
    if (buffer == null)
      buffer = new byte[BUF_SIZ];
    long totalbytes = 0;
    try {
      while (true) {
        int bytesread = source.read(buffer);
      
        if (bytesread > 0)
          dest.write(buffer, 0, bytesread);
        if (bytesread == -1)
          break;
        totalbytes += bytesread;
      }
      System.out.println("inputToOutput copied " + totalbytes + " bytes");
    }
    catch (Throwable t) {
      throw UniversalRuntimeException.accumulate(t,
          "Error copying stream after " + totalbytes + " bytes");
    }
    finally {
      if (closeinput)
        StreamCloseUtil.closeInputStream(source);
      if (closeoutput)
        StreamCloseUtil.closeOutputStream(dest);
    }
  }

  /**
   * Copies the supplied reader to the specified writer, closing both streams on
   * completion or error.
   * 
   * @param source
   *          The reader to be copied.
   * @param dest
   *          The writer where the input data is to be copied.
   * @exception IOException
   *              if an I/O error occurs.
   */

  public static final void readerToWriter(Reader source, Writer dest)
      throws IOException {
    char[] buffer = new char[BUF_SIZ];
    try {
      while (true) {
        int charsread = source.read(buffer);
        if (charsread > 0)
          dest.write(buffer, 0, charsread);
        if (charsread == -1)
          break;
      }
    }
    finally {
      try {
        source.close();
      }
      finally {
        dest.close();
      }
    }
  }

  /*
   * -- dangerous method relies on platform encoding public static final String
   * streamToString(InputStream source) throws IOException { return
   * readerToString (new InputStreamReader(source)); }
   */

  /**
   * A useful utility method to fully read the data from the specified reader
   * and return it as a string.
   * 
   * @param source
   *          A reader containing the data to be read. This stream will be
   *          closed on completion or error.
   * @return A string holding the complete contents read from the reader.
   * @exception IOException
   *              if an I/O error occurs.
   */

  public static final String readerToString(Reader source) throws IOException {
    char[] buffer = new char[CharWrap.INITIAL_SIZE];
    CharWrap build = new CharWrap();
    try {
      while (true) {
        int charsread = source.read(buffer);
        if (charsread > 0)
          build.append(buffer, 0, charsread);
        if (charsread != CharWrap.INITIAL_SIZE)
          break;
      }
    }
    finally {
      source.close();
    }
    return build.toString();
  }

  /**
   * Writes the contents of the supplied CharWrapVector to the specified writer,
   * closing the writer on completion or error.
   * 
   * @param source
   *          The CharWrapVector to be written
   * @param dest
   *          The writer where the input data is to be copied.
   * @exception IOException
   *              if an I/O error occurs.
   */

  public static final void charWrapVectorToWriter(CharWrapVector source,
      Writer dest) throws IOException {
    for (int i = 0; i < source.size(); ++i) {
      CharWrap wrap = source.charWrapAt(i);
      dest.write(wrap.storage, wrap.offset, wrap.size);
    }
  }

  /**
   * Produces a String representation of the complete contents of the supplied
   * string, assuming it to be encoded in UTF-8. The supplied stream WILL be
   * closed.
   * 
   * @param source
   * @return
   */
  public static String streamToString(InputStream source) {
    DirectInputStreamReader disr = null;
    try {

      disr = new DirectInputStreamReader(source);
      return readerToString(disr);
    }
    catch (Throwable t) {
      throw UniversalRuntimeException.accumulate(t,
          "Error converting stream to string");
    }
    finally {
      StreamCloseUtil.closeReader(disr);
    }
  }

  // A debug method which will save a stream to disk and return a copy of
  // it.
  public static InputStream bottleToDisk(InputStream is, String filename) {
    try {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    inputToOutput(is, baos, new byte[1024]);
    byte[] buffer = baos.toByteArray();
    ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
    FileOutputStream fos = new FileOutputStream(filename);
    inputToOutput(bais, fos, new byte[1024]);
    bais = new ByteArrayInputStream(buffer);
    return bais;
    }
    catch (Throwable t) {
      throw UniversalRuntimeException.accumulate(t, "Error bottling input stream");
    }
  }
}