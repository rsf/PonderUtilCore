package uk.org.ponder.stringutil;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import uk.org.ponder.util.UniversalRuntimeException;

// stub implementation simply passes through to SUN implementation
// until time or necessity to make efficient implementation like
// ByteToCharUTF8
public class CharToByteUTF8 {
  /**
   * Converts the supplied string into a byte array holding its UTF-8 encoded
   * form. This method call is currently very inefficient and simply passes
   * through to the SUN implementation.
   * 
   * @param toconvert A string to be converted to UTF-8.
   * @return A byte array holding a UTF-8 encoded version of the input string.
   */
  public static byte[] convert(String toconvert) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      OutputStreamWriter osw = new OutputStreamWriter(baos, "UTF8");
      osw.write(toconvert);
      osw.close();
    }
    catch (Exception uee) {
      throw UniversalRuntimeException.accumulate(uee,
          "Critical error: UTF-8 encoder not present");
    }
    return baos.toByteArray();
  }
}
