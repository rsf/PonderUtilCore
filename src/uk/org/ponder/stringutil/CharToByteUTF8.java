package uk.org.ponder.stringutil;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import uk.org.ponder.util.Logger;

// stub implementation simply passes through to SUN implementation
// until time or necessity to make efficient implementation like
// ByteToCharUTF8
public class CharToByteUTF8 {
  /** Converts the supplied string into a byte array holding its UTF-8 encoded
   * form. This method call is currently very inefficient and simply passes
   * through to the SUN implementation.
   * @param tomeasure A string to be converted to UTF-8.
   * @return A byte array holding a UTF-8 encoded version of the input string.
   * @exception IOException If an error occurs whilst converting the supplied
   * string (believed impossible)
   */
  public static byte[] convert(String tomeasure) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      OutputStreamWriter osw = new OutputStreamWriter(baos, "UTF8");
      osw.write(tomeasure);
      osw.close();
      }
    catch (UnsupportedEncodingException uee) {
      Logger.println("Critical error: UTF-8 encoder not present", Logger.DEBUG_ENTIRELY_CRITICAL);
      Logger.printStackTrace(uee);
      }
    return baos.toByteArray();
    }
  }
