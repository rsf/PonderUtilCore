package uk.org.ponder.stringutil;

/** The Sun "ByteToCharUTF8" gives neither control nor
 * feedback from the UTF8 conversion process.
 * The following URLs:
 * <br><a href="http://www-106.ibm.com/developerworks/library/utfencodingforms/">
 * http://www-106.ibm.com/developerworks/library/utfencodingforms/</a>
 * <br><a href="http://www.cl.cam.ac.uk/~mgk25/unicode.html">
 * http://www.cl.cam.ac.uk/~mgk25/unicode.html</a>
 * <br>are invaluable in understanding what is going on here.
 * Note these two are actually in disagreement - IBM agrees with this file,
 * and says that max UTF-8 is 4 bytes, whereas Kuhn goes up to 6. 
 * RFC2781 says that &gt; 4byte UTF-8 values (&gt; U+1ffff) are simply not convertible to 
 * UTF-16 which is presumably what is used by Java, so the difference is academic.
 * It would appear characters &gt; U+10000 have not appeared until Unicode 3.1,
 * standard dated 23/03/01.
 *
 * <p>Sun implementation insists on throwing MalformedInputException on all
 * possible occasions, this is not acceptable strategy for getting user to
 * correct invalid characters. It should be able to throw UnknownCharacterException,
 * but this decoder never does.
 *
 * <p>Note that this code obeys recommendation against accepting overlong encodings
 * of the same characer.
 */

public class ByteToCharUTF8 extends ByteToCharConverter {

  public ByteToCharUTF8() {}

  /*
   * Characters to use for automatic substitution.  
   */
  // This character \uFFFD is actually assigned, NB 
  // http://oss.software.ibm.com/developerworks/opensource/icu/project/archives/icu-bugrfe/icu-bugrfe.0007/msg00062.html
  // which claims that some converters incorrectly use it as unassigned.
  // this is "Hollow box, width 1"
  // Markus Kuhn recommends 0xDCxx rather than 0xfffd
  // DCxx are in the "low-surrogate" range for UTF-16, which are in error if they appear alone 
  // not preceded by 0xD8xx (Unicode book, ch 13).
  // Kuhn argues that this is wise because it will generate an error on any further
  // decoding, but is lossless.
  //  protected char[] subChars = { '\uFFFD' };

  // small array to avoid output of incomplete UTF-16
  char[] outputChar = new char[6];

  // The input_sequence_length variable is left set until the head of this loop,
  // so that error handlers can find the erroneous sequence, and the input fillers can tell 
  // how much input is being awaited. However, the true position of fully converted input
  // is always stored in inbufferpos.

  public void handleEncodingError(String errortype) {
    for (int i = 0; i < input_sequence_length; ++ i) {
      // remember to mask for sign-extension widening
      outputChar[i] = (char)(0xdc00 + (inbuffer[inbufferpos + i] & 0xff));
      }
    // Kuhn actually recommends output sequence of 1 byte here, but this is
    // inconsistent with his other advice about lossless error encoding
    output_sequence_length = input_sequence_length;
    super.handleEncodingError(errortype);
    }

  public int convert() {
    int byte1, byte2, byte3, byte4;
    input_sequence_length = 0;

    // This loop touches:
    // inbufferpos, inbufferlimit, inbuffer, input_sequence_length
    // outbufferpos, outbufferlimit, outbuffer, output_sequence_length
    // (errorhandler)

    /*    System.out.println("ByteToCharUTF8 beginning convert() with inbufferpos "+ inbufferpos +
	  " inbufferlimit "+inbufferlimit);*/
    while (inbufferpos < inbufferlimit) { // smaller loop to repeatedly get input
      
      byte1 = inbuffer[inbufferpos] & 0xff;
      // useful aide-memoire - the first level boundary gains 4 bits, all subsequent gain 5
      if ((byte1 & 0x80) == 0) { // level 1 character - single ASCII byte U-0x0 - 0x7f
	if (byte1 == (int)'\n') ++ linenumber;
	input_sequence_length = 1;
	outputChar[0] = (char)byte1;
	output_sequence_length = 1;
	//	System.out.print(outputChar[0]);
	} 
      else if ((byte1 & 0xe0) == 0xc0) { // level 2 high bits should be 110llll0
	// level 2 - two bytes U-0x80 - 0x7ff
	input_sequence_length = 2;
	if (missing_bytes() > 0) { // overflow of 1 byte into next round
	  return STOP_INPUT_EXHAUSTED;
	  }
	byte2 = inbuffer[inbufferpos + 1] & 0xff;
	if ((byte2 & 0xc0) != 0x80) { // level 2 error.
	  handleEncodingError("Invalid 2-byte UTF-8 encoding");
	  }
	else if ((byte1 & 0x1e) == 0) { // reject overlong sequence 0x7f or less
	  handleEncodingError("Overlong 2-byte UTF-8 encoding");
	  }
	else {
	  outputChar[0] = (char)(((byte1 & 0x1f) << 6) | (byte2 & 0x3f));
	  output_sequence_length = 1;
	  }
	System.out.print("[2]"+outputChar[0]);
	} 
      else if ((byte1 & 0xf0) == 0xe0) { // level 3 high bits should be 1110llll 10lxxxxx 
	// level 3 - 3 bytes U-0x800 - 0xffff
	input_sequence_length = 3;
	if (missing_bytes() > 0) { // overflow of one byte into next round
	  return STOP_INPUT_EXHAUSTED;
	  }
	byte2 = inbuffer[inbufferpos + 1] & 0xff;
	byte3 = inbuffer[inbufferpos + 2] & 0xff;
	if ((byte2 & 0xc0) != 0x80 || (byte3 & 0xc0) != 0x80) { // level 3 error
	  handleEncodingError("Invalid 3-byte UTF-8 encoding");
	  }
	else if ((byte1 & 0xf) == 0 && (byte2 & 0x20) == 0) { 
	  handleEncodingError("Overlong 3-byte UTF-8 encoding");
	  // reject overlong sequence - 0x7ff or less
	  }
	else {
	  outputChar[0] = (char)(((byte1 & 0x0f) << 12)
				 | ((byte2 & 0x3f) << 6)
				 | (byte3 & 0x3f));
	  output_sequence_length = 1;
	  }
	// This is the place we would reject incorrect UTF-16 surrogates if we could
	// be bothered
	System.out.print("[3]"+outputChar[0]);
	} 
      else if ((byte1 & 0xf8) == 0xf0) { // level 4 high bits should be 11110lll 10llxxxxtc.
	// l bits should be 1 for non-overlong sequence
	// level 4 - 4 bytes U-0x10000 - 0x1fffff
	input_sequence_length = 4;
	if (missing_bytes() > 0) {
	  return STOP_INPUT_EXHAUSTED;
	  }
	byte2 = inbuffer[inbufferpos + 1] & 0xff;
	byte3 = inbuffer[inbufferpos + 2] & 0xff;
	byte4 = inbuffer[inbufferpos + 3] & 0xff;
	if ((byte2 & 0xc0) != 0x80 || 
	    (byte3 & 0xc0) != 0x80 ||
	    (byte4 & 0xc0) != 0x80) { // level 4 error if all high bits are not 10xxxxxx
	  handleEncodingError("Invalid 4-byte UTF-8 encoding");
	  }
	else if ((byte1 & 0x7) == 0 && (byte2 & 0x30) == 0) { // reject overlong sequence
	  handleEncodingError("Overlong 4-byte UTF-8 encoding");
	  }
	else if ((byte1 & 0x4) != 0) {
	  handleEncodingError("4-byte UTF-8 encoding unrepresentable as UTF-16");
	  }
	// this byte sequence is UTF-16 character, needs encoding as surrogate pair
	// see RFC2781 for specification.
	/*
	 * -  Characters with values between 0x10000 and 0x10FFFF are
	 * represented by a 16-bit integer with a value between 0xD800 and
	 * 0xDBFF (within the so-called high-half zone or high surrogate
	 * area) followed by a 16-bit integer with a value between 0xDC00 and
	 * 0xDFFF (within the so-called low-half zone or low surrogate area).
	 * 
	 * -  Characters with values greater than 0x10FFFF cannot be encoded in
	 * UTF-16.
	 */
	// UTF-16 packs bits as 110110xx xxxxxxxx 110111xx xxxxxxxx for 20 bits.
	else {
	  int ucs4 = (0x07 & byte1) << 18 |
	    (0x3f & byte2) << 12 |
	    (0x3f & byte3) <<  6 |
	    (0x3f & byte4); // get 3, 6, 6, 6 bits = 21 bits with high 0.
	  outputChar[0] = (char)((ucs4 - 0x10000) / 0x400 + 0xd800);
	  outputChar[1] = (char)((ucs4 - 0x10000) % 0x400 + 0xdc00);
	  output_sequence_length = 2;
	  System.out.print("[4]"+outputChar[0]+outputChar[1]);
	  }
	} 
      else if ((byte1 & 0xfc) == 0xf8) {
	input_sequence_length = 5;
	if (missing_bytes() > 0) {
	  return STOP_INPUT_EXHAUSTED;
	  }
	byte2 = inbuffer[inbufferpos + 1] & 0xff;
	byte3 = inbuffer[inbufferpos + 2] & 0xff;
	byte4 = inbuffer[inbufferpos + 3] & 0xff;
	int byte5 = inbuffer[inbufferpos + 4] & 0xff;
	if ((byte2 & 0xc0) != 0x80 || 
	    (byte3 & 0xc0) != 0x80 ||
	    (byte4 & 0xc0) != 0x80 ||
	    (byte5 & 0xc0) != 0x80) { // level 5 error if all high bits are not 10xxxxxx
	  handleEncodingError("Invalid 5-byte UTF-8 encoding");
	  }
	else {
	  handleEncodingError("5-byte UTF-8 encoding unrepresentable as UTF-16");
	  }
	}
      else if ((byte1 & 0xfe) == 0xfc) {
	input_sequence_length = 6;
	if (missing_bytes() > 0) {
	  return STOP_INPUT_EXHAUSTED;
	  }
	byte2 = inbuffer[inbufferpos + 1] & 0xff;
	byte3 = inbuffer[inbufferpos + 2] & 0xff;
	byte4 = inbuffer[inbufferpos + 3] & 0xff;
	int byte5 = inbuffer[inbufferpos + 4] & 0xff;
	int byte6 = inbuffer[inbufferpos + 5] & 0xff;
	if ((byte2 & 0xc0) != 0x80 || 
	    (byte3 & 0xc0) != 0x80 ||
	    (byte4 & 0xc0) != 0x80 ||
	    (byte5 & 0xc0) != 0x80 ||
	    (byte6 & 0xc0) != 0x80) { // level 6 error if all high bits are not 10xxxxxx
	  handleEncodingError("Invalid 6-byte UTF-8 encoding");
	  }
	else {
	  handleEncodingError("6-byte UTF-8 encoding unrepresentable as UTF-16");
	  }
	}
      else {
	input_sequence_length = 1;
	handleEncodingError("Invalid 1-byte UTF-8 encoding");
	}
      // run out of output space --- back up to beginning of sequence and wait
      // for another output buffer to be supplied. 
      if (outbufferpos + output_sequence_length > outbufferlimit) {
	/*
	System.out.println("Output buffer exhausted with sequence length "+output_sequence_length+
			   " remaining");
	*/
	return STOP_OUTPUT_EXHAUSTED;
	}
      else { // not out of space, can step along
	for (int i = 0; i < output_sequence_length; i++) {
	  outbuffer[outbufferpos + i] = outputChar[i];
	  }
	outbufferpos += output_sequence_length;
	inbufferpos += input_sequence_length;
	totalbytesin += input_sequence_length;
	input_sequence_length = 0;
	}
      } // end loop over this buffer of input
    return STOP_INPUT_EXHAUSTED_EXACTLY;
    }
  
    /*
     * Return the character set id
     */
  public String getCharacterEncoding() {
    return "UTF8";
    }
  
  public int getMaxOutput(int inputsize) {
    // possibilities are 1-1, 2-1, 3-1 or 4-2.
    return inputsize;
    }
  }    
