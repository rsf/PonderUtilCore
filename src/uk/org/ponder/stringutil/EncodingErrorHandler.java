package uk.org.ponder.stringutil;

/** This is an interface used by the advanced InputStreamReader architecture 
 * (uk.org.ponder.streamutil.DirectInputStreamReader and ByteToCharUTF8) to
 * allow far more authoritative and precise reporting of errors encountered
 * while converting byte-encoded data back into characters.
 */

public interface EncodingErrorHandler {
  /** Called by the decoder when an error is encountered in the byte data.
   * @param errortype A string representing the type of the encountered error.
   * @param linenumber A line number (perhaps formed by counting \n) for the 
   * encountered error.
   * @param byteoffset The byte offset of the data that is in error, within the
   * entire data to be converted.
   * @param sourcearray An array of bytes in which the erroneous input characters
   * may be inspected.
   * @param errorpos An offset within <code>sourcearray</code> of the erroneous bytes.
   * @param errorlength As best as can be determined, the length in bytes of the 
   * error data.
   */
  public void reportEncodingError(String errortype, 
				  int linenumber, int byteoffset, byte[] sourcearray, 
				  int errorpos, int errorlength);
  }
