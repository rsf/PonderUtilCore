package uk.org.ponder.saxalizer;

/** A class which in addition to associating various attribute names with method names, also 
 * accepts arbitrary additional attributes implements this interface.
 */

public interface SAXalizableExtraAttrs extends SAXalizableAttrs { 
  /** This method is called to report the discovery of arbitary additional attributes
   * attached to the tag corresponding to this node in the SAX stream.
   * @param hal The extra attributes to report.
   */
  void setExtraAttributes(SAXAttributeHash hal);
  }
