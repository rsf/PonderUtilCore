package uk.org.ponder.saxalizer;

/** An object which can return a SAX attribute list (and oneday
 * comments, with SAX 2) to be written to a SAX stream
 * implements this interface.
 */

public interface DeSAXalizableAttrs extends DeSAXalizable {
  /** Return an array of methods for querying the attributes
   * of an object to be written to a SAX stream.
   * @return The array of method specifications.
   */
  SAXAccessMethodSpec[] getSAXGetAttrMethods();

  }
