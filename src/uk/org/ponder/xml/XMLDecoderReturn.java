package uk.org.ponder.xml;

import java.io.Reader;

/** A simple structure which serves as the return value from the XMLDecoder class.
 * It encapsulates the state of an XML parse immediately following parsing the declaration.
 */

public class XMLDecoderReturn {
  /** A String holding the decoded declaration.*/
  public String declaration;
  /** The number of bytes in the underlying file consumed by parsing the declaration.*/
  public int declarationbytes;
  /** A Reader from which the remaining contents of the file may be read.*/
  public Reader decodedreader;
  public XMLDecoderReturn(String declaration, int declarationbytes, 
			  Reader decodedreader) {
    this.declaration = declaration;
    this.declarationbytes = declarationbytes;
    this.decodedreader = decodedreader;
    }
  }
