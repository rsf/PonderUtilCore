package uk.org.ponder.xml;

import java.io.Reader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.IOException;

import uk.org.ponder.byteutil.ByteWrap;
import uk.org.ponder.streamutil.DirectInputStreamReader;
import uk.org.ponder.stringutil.EncodingErrorHandler;

/** The XMLDecoder is used to provide a more instrumented decoding facility for 
 * parsing XML files. The Sun default UTF-8 decoder supplies inaccurate location
 * information for invalid characters, provides minimal UTF-8 validation and 
 * minimal description of decoding errors. XMLDecoder does the work of parsing
 * an XML declaration in a small repertoire of encodings, and hands off the 
 * task of character conversion for the rest of the stream to the
 * <code>org.ponder.streamutil.DirectInputStreamReader</code> if the declaration
 * is consistent with the UTF-8 encoding scheme.
 */

public class XMLDecoder {
  /** Strips off the declaration from an XML file, and returns information suitable
   * for further processing. The declaration is decoded to infer the encoding scheme
   * as far as possible, and the return value includes the number of bytes decoded,
   * the declaration as a String, and a Reader from which the decoded contents of the
   * rest of the file may be read.
   * <p> This decoder currently detects UCS16 both little and big-endian using the BOM
   * (Byte Order Mark) <code>0xffef</code> invented by the Evil Empire, and 8-bit
   * encodings for which the JVM includes a converter with a name matching the XML
   * declaration version. Files without an encoding declaration which appear to be
   * 8-bit are interpreted as UTF-8 (as per XML specification).
   * @param is An inputstream containing an XML file.
   * @param handler An EncodingErrorHandler to which errors encountered during UTF-8
   * decoding may be reported.
   * @return An XMLDecoderReturn object providing the client with everything required
   * to continue parsing the XML file.
   */

  public static XMLDecoderReturn stripDeclaration(InputStream is, EncodingErrorHandler handler)
    throws IOException {
    Reader readertogo = null;
    ByteWrap first4 = new ByteWrap(4);
    int totalbytesread = 0;
    String decstring = "";
    do {
      int bytesread = is.read(first4.bytes);
      if (bytesread == -1) {
	throw new IOException("Unexpected EOF found while parsing XML declaration after "+totalbytesread +" bytes");
	}
      totalbytesread += bytesread;
      } while (totalbytesread != 4);
    PushbackInputStream pushback = new PushbackInputStream(is, 4);
    pushback.unread(first4.bytes);
    int magic2 = first4.read_at2(0);
    int magic4 = first4.read_at4(0);
    if (magic2 == 0xfeff) {
      readertogo = new InputStreamReader(pushback, "UnicodeBig");
      }
    else if (magic2 == 0xffef) {
      readertogo = new InputStreamReader(pushback, "UnicodeLittle");
      }
    StringBuffer declaration = new StringBuffer();
    if (readertogo != null) { // fully decoded as some sort of UCS16, scan ahead for ">"
      while (true) {
	int nextchar = readertogo.read();
	if (nextchar == -1) {
	  throw new IOException
	    ("Unexpected end of XML declaration while decoding UCS-16, read so far: "+
				declaration.toString());
	  }
	else declaration.append(nextchar);
	if (nextchar == (int)'>') break;
	}
      }
    else if (magic4 == 0x3c3f786d) { // not fully decoded, but declaration is 8-bit
      while (true) {
	int nextchar = is.read();
	if (nextchar == -1) {
	  throw new IOException
	    ("Unexpected end of XML declaration while scanning 8-bit encoding, read so far: "+
	     declaration.toString());
	  }
	else {
	  declaration.append(nextchar);
	  ++ totalbytesread;
	  if (nextchar == (int)'>') break;
	  }
	}
      decstring = declaration.toString();
      int encodingpos = decstring.indexOf("encoding");
      if (encodingpos == -1) {
	System.out.println("Encoding declaration not found, assuming UTF-8");
	readertogo = new DirectInputStreamReader(is);
	((DirectInputStreamReader)readertogo).setEncodingErrorHandler(handler);
	}
      else { // found an encoding declaration
	int encodequotepos = decstring.indexOf('"', encodingpos);
	int encodeapospos = decstring.indexOf('\'', encodingpos);
	if (encodequotepos == -1 && encodeapospos == -1) {
	  throw new IOException
	    ("Invalid XML declaration --- encoding declared but not specified: "+ decstring);
	  }
	int encodestartpos = -1, encodeendpos = -1;
	if (encodequotepos != -1) {
	  encodestartpos = encodequotepos;
	  encodeendpos = decstring.indexOf('"', encodequotepos);
	  }
	if (encodeapospos != -1) {
	  encodestartpos = encodeapospos;
	  encodeendpos = decstring.indexOf('\'', encodeapospos);
	  }
	if (encodeendpos == -1)
	  throw new IOException
	    ("Invalid XML declaration --- unterminated encoding name: "+ decstring);
	String encodingname = decstring.substring(encodestartpos, encodeendpos);
	readertogo = new InputStreamReader(is, encodingname);
	}
      }
    
    else {
      throw new IOException("Unexpected bytes at start of XML file:" + 
			    ByteWrap.intToHex(magic4));
      }
    XMLDecoderReturn togo = new XMLDecoderReturn(decstring, totalbytesread, readertogo);
    return togo;
    }
  }
