package uk.org.ponder.saxalizer;

/** A class which can accept a SAX attribute list (and oneday
 * comments, with SAX 2) that arrive from a SAX stream implements
 * this interface.
 */

public interface SAXalizableAttrs extends SAXalizable {
  /** Return an array of methods suitable for reporting attributes
   * attached to the tag corresponding to this node in the SAX stream.
   * @return The array of method specifications.
   */
  SAXAccessMethodSpec[] getSAXSetAttrMethods();
  //  SAXAccessMethodSpec[] getSAXGetAttrMethods();

  //  void setComment(String comment);
  //  String getComment();
  }
