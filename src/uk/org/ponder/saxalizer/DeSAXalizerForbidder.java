package uk.org.ponder.saxalizer;

/** An interface used to supply a callback to the DeSAXalizer. The DeSAXalizer makes
 *  a call to the method of this interface before serializing every object in the subtree. If the
 *  method returns <code>false</code>, serialization of the object and its entire subtree will 
 *   be skipped.
 */

public interface DeSAXalizerForbidder {
  /** @param tag The XML tag name that will be supplied for the target object.
   * @param o The target object itself.
   * @return <code>true</code> if serialization of the target object is to be permitted.
   */
  boolean permitSerialization(String tag, Object o);
  }
