package uk.org.ponder.streamutil;

import java.io.InputStream;
import java.io.Reader;
import java.io.IOException;

import uk.org.ponder.stringutil.ByteToCharConverter;
import uk.org.ponder.stringutil.ByteToCharUTF8;
import uk.org.ponder.stringutil.EncodingErrorHandler;

/**
 * A more efficient and sane rendering of the standard Java <code>InputStreamReader</code>
 * class. 
 * This version features:
 * <br> No silly exceptions indicating lack of output space, but instead a proper streaming
 * architecture
 * <br> No locking overhead, clearly anyone is silly enough to read from the same reader from two
 * different threads should do the locking himself.
 * <br> Correct handling of UTF-16 in the decoder, the Sun version would overflow its buffer
 * on receiving any surrogate pairs in a UTF-8 stream.
 * <br> Proper official names for UTF-16 and UTF-8 formats.
 * <br> Powerful error handling architecture allows users to actually tell where in the input
 * stream the byte occured that failed conversion, Sun approach of randomly throwing exceptions
 * and then packing it in was simply not good enough.
 * <br> Implementation of Markus Kuhn's "erroneous byte in erroneous surrogate" error encapsulation
 * strategy. This scheme allows proper rendering of erroneous bytes further on down the pipeline.
 * @author Antranig Basman
 */
public class DirectInputStreamReader extends Reader {
  /*
   * Substitution mode flag.
   */
  protected boolean subMode = true;

  InputStream inputstream;

  private ByteToCharConverter converter;

  /**
   * Returns the character set id for the conversion
   */
  public String getCharacterEncoding() {
    return converter.getCharacterEncoding();
    }

  public DirectInputStreamReader() {
    inputstream = null;
    }

  public DirectInputStreamReader(InputStream inputstream) {
    setInputStream(inputstream);
    }

  public void setInputStream(InputStream inputstream) {
    setInputStream(inputstream, "UTF-8", StreamCopier.PROCESS_BUFFER_SIZE);
    }

  public void setInputStream(InputStream inputstream, String encoding) {
    setInputStream(inputstream, encoding, StreamCopier.PROCESS_BUFFER_SIZE);
    }

  public void setInputStream(InputStream inputstream, String encoding, int buffersize) {
    if (converter == null || converter.getCharacterEncoding() != encoding) {
      if (encoding == "UTF-8") {
	converter = new ByteToCharUTF8();
	}
      }
    converter.blastState();
    converter.ensureInputBuffer(buffersize);
    this.inputstream = inputstream;
    }

  public void setEncodingErrorHandler(EncodingErrorHandler handler) {
    if (converter != null) {
      converter.setEncodingErrorHandler(handler);
      }
    }

  public void close() throws IOException {
    if (inputstream != null) {
      inputstream.close();
      }
    converter.blastState();
    }

  private int acceptInput() throws IOException {
    converter.swizzInputBuffer();
 
    byte[] inbuffer = converter.getInputBuffer();
    int inbufferlimit = converter.getInputBufferLimit();

    int bytesread = inputstream.read
      (inbuffer, inbufferlimit, inbuffer.length - inbufferlimit);
    if (bytesread != -1) {
      converter.increaseInputBufferLimit(bytesread);
      }
    //    System.out.println("acceptInput read "+ bytesread+" bytes");
    return bytesread;
    }


  private int convertInternal() throws IOException {
    int outbufferstart = converter.getOutputBufferPos();
    int bytesread = 0;
    //    System.out.println("Beginning convertInternal at output buffer pos "+outbufferstart);
    while (true) { // big loop attempts to fill up outbuffer
      int stop_reason = converter.convert();
      // when we get to here, break was either because sequence was incomplete,
      // we have completely come to the end of input or else we are out of output buffer space.
      if (stop_reason == ByteToCharConverter.STOP_OUTPUT_EXHAUSTED) {
	break;
	}
      else { // otherwise we must be short of input
	// attempt to get more input. NB 1.2.2 InputStream contract says that this will
	// return at least 1 byte if not at EOF, and a sequence may be longer than this.
	bytesread = acceptInput(); // nb all inbuffer parameters blasted by this
	//	System.out.println("missing_bytes:" +converter.missing_bytes());
	if (bytesread == -1) { // only break if at EOF 
	  if (converter.missing_bytes() > 0) { 
	    // if there is STILL not enough data, signal error
	    converter.handleEncodingError("Premature end of input stream during "+
					  converter.getCharacterEncoding()+" sequence");
	    }
	  break;
	  } 
	// otherwise, there (may be) now sufficient data, if not now then after a few times
	// round this loop if inputstream perversely returns the minimum 1 byte each time
	// restart sequence and continue reading where we left off.
	} // end if no useable bytes WERE in buffer
      } // end loop over output buffer
    // output buffer is full, or no more input remaining. Return number of useful
    // bytes in output buffer.
    int outputchars = converter.getOutputBufferPos() - outbufferstart;
    /*
    System.out.println("convertInternal converted "+outputchars+" output characters, "
		       + bytesread +" of input read ");
    */
    // nb - why may we have output 0 chars and not be at EOF? Perhaps only because perverse
    // bugger supplied < 2-element output array?
    return (outputchars == 0 && bytesread == -1)? bytesread : outputchars;
    }

  public int read(char[] outbuffer, int outbufferpos, int length) throws IOException {
    converter.setOutputBuffer(outbuffer, outbufferpos, outbufferpos + length);
    return convertInternal();
    }

  }
