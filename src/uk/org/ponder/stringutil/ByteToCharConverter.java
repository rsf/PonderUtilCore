package uk.org.ponder.stringutil;

/** This abstract class forms part of the fast and better instrumented
 * uk.org.ponder.streamutil.DirectInputStreamReader architecture. It is
 * intended as a base class for specific byte to character decoders 
 * (such as ByteToCharUTF8), and abstracts away the non-stream and 
 * non-encoding specific tasks of working out whether anything needs 
 * doing or not, and if so how much and where it is.
 */

public abstract class ByteToCharConverter {

  public ByteToCharConverter() {}

  /** Return code from <code>convert()</code> indicating conversion stopped because 
   * there was no space left in the output buffer. */

  public static final int STOP_OUTPUT_EXHAUSTED = 0;
  /** Return code from <code>convert()</code> indicating conversion stopped because 
   * there was no space left in the input buffer. */
  
  public static final int STOP_INPUT_EXHAUSTED  = 1;

  /** Return code from <code>convert()</code> indicating conversion stopped because 
   * there was no input left in the input buffer, but also that no partial input sequence
   * was left in it. */
  public static final int STOP_INPUT_EXHAUSTED_EXACTLY = 2;


  /** Convert as many bytes from <code>inbuffer</code> to characters in <code>outbuffer</code>
   * as possible. The return codes from this method are listed above, indicating
   * which out of the input and the output was actually exhausted.
   */

  public abstract int convert();

  /** Returns the name of the byte to character (UTF-16) encoding performed by this
   * converter */
  public abstract String getCharacterEncoding();

  /** Returns the maximum possible number of characters that could be decoded from
   * an input byte sequence of the specified length. Currently disused.
   * @param inputsize The number of input bytes for which the maximum decoded characters
   * are required.
   */
  public abstract int getMaxOutput(int inputsize);
  /*
   * Offset of next character to be output
   */
  protected int outbufferpos;
  protected int outbufferlimit;

  protected char[] outbuffer;

  protected int totalbytesin;
  /*
   * Offset of next byte to be converted
   */
  protected int inbufferpos;
  protected int inbufferlimit;
  
  protected byte[] inbuffer;
  /*
   * Length of bad input that caused a MalformedInputException.
   */
  protected int input_sequence_length;
  protected int output_sequence_length;
  /*
   * Number of lines that have gone by
   */
  protected int linenumber;

  private EncodingErrorHandler errorhandler;

  // The following four methods require public access since they are used 
  // from above by DirectInputStreamReader,
  // resulting from a possible factorisation error in all of this logic.
  // See Felixified I/O routines involving crank() etc.

  public int getOutputBufferPos() {
    return outbufferpos;
    }
  
  public int getInputBufferLimit() {
    return inbufferlimit;
    }
  
  public byte[] getInputBuffer() {
    return inbuffer;
    }

  public void increaseInputBufferLimit(int bytesread) {
    inbufferlimit += bytesread;
    }

  /** Sets the output buffer to which decoded character data should be written.
   * @param outbuffer A character buffer to which character data can be written.
   * @param outbufferpos The position within the buffer to which the character data
   * can be written.
   * @param outbufferlimit The index of the logical end of the buffer. If data
   * is written exactly up to this point, the buffer will be considered full and
   * decoding will stop until another buffer is supplied.
   */

  public void setOutputBuffer(char[] outbuffer, int outbufferpos, int outbufferlimit) {
    this.outbuffer = outbuffer;
    this.outbufferpos = outbufferpos;
    this.outbufferlimit = outbufferlimit;
    }

  /** Sets the error handler that will be used to report errors encountered in the
   * byte encoding of the data.
   * @param errorhandler An interface through which decoding errors may be reported.
   */
  public void setEncodingErrorHandler(EncodingErrorHandler errorhandler) {
    this.errorhandler = errorhandler;
    }

  /** Reorganise the input buffer by rotating the current input point to the beginning,
   * ready to receive more input after <code>inbufferlimit</code> */

  public void swizzInputBuffer() {
    System.arraycopy(inbuffer, inbufferpos, inbuffer, 0, inbufferlimit - inbufferpos);
    //    totalbytesin += inbufferpos;
    inbufferlimit = inbufferlimit - inbufferpos;
    inbufferpos = 0;
    }

  
  /** Trigger an encoding error to be delivered to any registered EncodingErrorHandler.
   * There is one sort of error that can only be detected from the
   * outside of this class, namely an incomplete input sequence but no
   * further input available.  For this reason this method has been
   * given public access to allow an error report to be triggered
   * externally 
   * @param errortype A String reprenting the type of the error that has occurred.
   * This string will be passed on via the EncodingErrorHandler interface.*/
  public void handleEncodingError(String errortype) {
    if (errorhandler != null) {
      int max_sequence_available = inbufferlimit - inbufferpos;
      // do not surprise our clients by returning pointers to nonexistent bytes
      // should the error be invoked by DirectInputStreamReader as a result of 
      // incomplete final sequence.
      if (max_sequence_available > input_sequence_length)
	max_sequence_available = input_sequence_length;
      errorhandler.reportEncodingError
	(errortype, linenumber, totalbytesin, inbuffer, inbufferpos, 
	 max_sequence_available);
      }
    }

  /** Ensure that the current input buffer is big enough to accommodate the specified
   * number of input bytes, by reallocating it if necessary. This method does not
   * preserve the buffer contents.
   * @param buffersize The required input buffer size.
   */
  public void ensureInputBuffer(int buffersize) {
    if (inbuffer == null || inbuffer.length < buffersize) {
      inbuffer = new byte[buffersize];
      }
    }

  /** Destroy all the state stored in this converter, so it holds no resources
   * and is ready to begin conversion again.
   */

  public void blastState() {
    inbufferpos = 0;
    inbufferlimit = 0;
    input_sequence_length = 0;
    outbufferpos = 0;
    outbufferlimit = 0;
    output_sequence_length = 0;
    
    outbuffer = null;
    inbuffer = null;
    
    linenumber = 1;
    totalbytesin = 0;
    errorhandler = null;
    }

  /** Returns the number of bytes needed to complete the current input sequence.
   * @return the number of bytes needed toc complete the current input sequence.
   * positive if we need more bytes to complete the current sequence, zero if we have exactly
   * used up all input, negative if there is more input remaining.
   */
  public int missing_bytes() { 
    return inbufferpos + input_sequence_length - inbufferlimit;
    }

  }
