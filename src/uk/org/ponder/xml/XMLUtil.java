package uk.org.ponder.xml;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import uk.org.ponder.stringutil.CharWrap;
import uk.org.ponder.stringutil.CharWrapVector;

import uk.org.ponder.streamutil.StreamCopyUtil;

/** A simple utility class to write a CharWrapVector (vector of char arrays) as
 * an XML file.*/

public class XMLUtil {
  public static String XMLEscape(String toescape) {
    int length = toescape.length();
    CharWrap togo = new CharWrap(length + 10);
    
    for (int i = 0; i < length; ++ i) {
      char c = toescape.charAt(i);
      if (c == '&') {
        togo.append("&amp;");
      }
      else if (c == '<') {
        togo.append("&lt");
      }
      else if (c == '>') {
        togo.append("&gt;");
      }
      else togo.append(c);
    }
    return togo.toString();
  }
  /** Writes the supplied CharWrapVector to the filename specified as XML. Useful
   * when using the jdiff package which manipulates diffs as CharWrapVectors.
   * @param filename The filename to receive the character data.
   * @param chars The characters to be written
   * @exception IOException if an I/O error occurs while writing the file.
   */
  public static void writeCharsAsXMLFile(String filename, CharWrapVector chars) 
    throws IOException {
    FileOutputStream patchout = new FileOutputStream(filename);
    OutputStreamWriter patchwrite = null;
    try {
      patchwrite = new OutputStreamWriter(patchout, "UTF-8");
      patchwrite.write(XMLWriter.getDefaultDeclaration());
      StreamCopyUtil.charWrapVectorToWriter(chars, patchwrite);
      }
    catch (UnsupportedEncodingException uee) {
      throw new IOException("UTF-8 encoding unsupported: "+uee.getMessage());
      }
    finally {
      patchwrite.close();
      }
    }
  }
