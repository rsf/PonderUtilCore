package uk.org.ponder.saxalizer;

import org.xml.sax.Parser;

/** A tiny utility class to unify the process of obtaining SaxParsers from whatever
 * peculiar scheme the author created. At present it is designed for the Sun version
 * 1.0 JAXP parsers.
 */

public class SAXParserFactory {
  private static javax.xml.parsers.SAXParserFactory spf = 
    javax.xml.parsers.SAXParserFactory.newInstance();
  static {spf.setValidating(false);}

  /** Returns a new SAX Parser object.
   * @return a new parser object.
   */

  public static Parser newParser() {
    try {
      javax.xml.parsers.SAXParser saxparser = spf.newSAXParser();
      // unwrap the crap that jaxp puts around the parser
      return saxparser.getParser();
      }
    catch (Exception e) {
      e.printStackTrace();
      }
    return null;
    
    //    return new com.jclark.xml.sax.Driver();
    }
  }
