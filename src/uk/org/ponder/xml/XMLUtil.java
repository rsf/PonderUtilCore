package uk.org.ponder.xml;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import uk.org.ponder.stringutil.CharWrapVector;

import uk.org.ponder.streamutil.StreamCopier;

/** A simple utility class to write a CharWrapVector (vector of char arrays) as
 * an XML file.*/

public class XMLUtil {
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
      StreamCopier.charWrapVectorToWriter(chars, patchwrite);
      }
    catch (UnsupportedEncodingException uee) {
      throw new IOException("UTF-8 encoding unsupported: "+uee.getMessage());
      }
    finally {
      patchwrite.close();
      }
    }
  }
