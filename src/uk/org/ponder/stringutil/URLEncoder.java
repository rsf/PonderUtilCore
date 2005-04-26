package uk.org.ponder.stringutil;

import java.io.UnsupportedEncodingException;

import java.util.BitSet;

import uk.org.ponder.util.Logger;

/**
 * This class is similar to <CODE>java.net.URLEncoder</CODE> except that it
 * can handle non-Latin characters, whereas Java's version is 
 * <A HREF="http://developer.java.sun.com/developer/bugParade/bugs/4257115.html">
 * documented</A> to use only <CODE>ISO 8859-1</CODE>.
 */

public abstract class URLEncoder {
  /**
   * The default encoding, used by <CODE>encode(s)</CODE>.
  */
  public static final String DEFAULT_ENCODING = "UTF8";

  /**
   * Mapping of characters that don't need encoding.
   * Stolen from the source code to java.net.URLEncoder. Sans apologie.
   */
  
  static BitSet dontNeedEncoding;
  static {
    dontNeedEncoding = new BitSet(256);
    int i;
    for (i = 'a'; i <= 'z'; i++) {
      dontNeedEncoding.set(i);
      }
    for (i = 'A'; i <= 'Z'; i++) {
      dontNeedEncoding.set(i);
      }
    for (i = '0'; i <= '9'; i++) {
      dontNeedEncoding.set(i);
      }
    //dontNeedEncoding.set(' '); // encoding a space to a + is done in the encode() method
    dontNeedEncoding.set('-');
    dontNeedEncoding.set('_');
    dontNeedEncoding.set('.');
    dontNeedEncoding.set('*');
    }
  

  /**
   * URL-encodes a string using the default encoding, UTF-8. Essentially,
   * this is a UTF-8 version of <CODE>java.net.URLEncoder.encode()</CODE>.
   * @param s what to encode
   * @return URL-encoded version of <CODE>s</CODE>
   * @pre s != null
   */
  
  public static String encode(String s) {
    try {   // won't throw UnsupportedEncodingException b/c this encoding is good
      return encode(s, DEFAULT_ENCODING);
      } 
    catch (java.io.UnsupportedEncodingException uee) {    // should never happen
      Logger.println("Caught exception " + uee.toString() + " trying to encode in " + 
		     DEFAULT_ENCODING, Logger.DEBUG_SEVERE);
      return null;
      }
    }

  /**
   * URL-encodes a string using any available encoding.
   * @param s what to encode
   * @param encoding name of the encoding to use;
   * @return URL-encoded version of <CODE>s</CODE>
   * @throws java.io.UnsupportedEncodingException if <CODE>encoding</CODE> isn't
   * supported by the Java VM and/or class-libraries
   * @pre s != null
   * @pre encoding != null
   */
  public static String encode(String s, String encoding) throws UnsupportedEncodingException {
    StringBuffer result = new StringBuffer (s.length() * 2);
    char[] sChars = s.toCharArray();

    for (int iS = 0; iS < sChars.length; iS++) {
      char c = sChars[iS];
      // does this character need  encoding?
      if (dontNeedEncoding.get(c)) {  //  no encoding necessary
//	if (' ' == c) { // unless it's a space
//	  c = '+';
//	  }
	result.append(c);
	} 
      else {        // this character needs encoding
	byte[] cBytes = new Character(c).toString().getBytes(encoding);
	
	for (int iCB = 0; iCB < cBytes.length; iCB++) {
	  int charValue = cBytes[iCB];
	  result.append('%');
	  result.append(Character.forDigit((charValue >> 4) & 0xF, 16));
	  result.append(Character.forDigit(charValue & 0xF, 16));
	  }
	}
      }
    return result.toString();
    }
  }
