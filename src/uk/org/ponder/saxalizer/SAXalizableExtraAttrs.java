package uk.org.ponder.saxalizer;

import java.util.Map;

/** A class which in addition to associating various attribute names with method names, also 
 * accepts arbitrary additional attributes implements this interface.
 */

public interface SAXalizableExtraAttrs { 
  /** This method is called to report the discovery of arbitary additional attributes
   * attached to the tag corresponding to this node in the SAX stream.
   * @param hal The extra attributes to report.
   */
  public Map getAttributes();
  }
