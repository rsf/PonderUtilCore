package uk.org.ponder.xml;

import uk.org.ponder.saxalizer.SAXalizable;
import uk.org.ponder.saxalizer.DeSAXalizable;
import uk.org.ponder.saxalizer.SAXAccessMethodSpec;

/** An XMLAccessManifestEntry stores the details for one entry in an XMLAccessFile. These
 * objects are stored in an XMLAccessManifest.
 */

public class XMLAccessManifestEntry implements SAXalizable, DeSAXalizable {
  public SAXAccessMethodSpec[] getSAXSetMethods() {
    return new SAXAccessMethodSpec[] {
      new SAXAccessMethodSpec("tagname", "setTagName", String.class),
      new SAXAccessMethodSpec("key", "setKey", String.class),
      new SAXAccessMethodSpec("offset", "setOffset", RandomAccessPointer.class)};
    }

  public SAXAccessMethodSpec[] getSAXGetMethods() {
    return new SAXAccessMethodSpec[] {
      new SAXAccessMethodSpec("tagname", "getTagName", String.class),
      new SAXAccessMethodSpec("key", "getKey", String.class),
      new SAXAccessMethodSpec("offset", "getOffset", RandomAccessPointer.class)};
    }
  RandomAccessPointer offsetobj;
  long offset;
  String tagname;
  String key;

  /** Sets the offset of this entry within the XMLAccessFile. This is a SAXalization method.
   * @param offset The required new offset.
   */
  public void setOffset(RandomAccessPointer offsetobj) {
    this.offsetobj = offsetobj;
    this.offset = offsetobj.offset;
    }
  /** Sets the tag name for this entry. This is a SAXalization method.
   * @param tagname The required new tagname.
   */
  public void setTagName(String tagname) {
    this.tagname = tagname;
    }
  /** Sets the key used to look up this entry within the XMLAccessFile. 
   * This is a SAXalization method.
   * @param key The required new kay.
   */
      
  public void setKey(String key) {
    this.key = key;
    }

  /** Returns the offset of this entry within the XMLAccessFile. This is a DeSAXalization method.
   * @return The required byte offset.
   */

  public RandomAccessPointer getOffset() {
    offsetobj.offset = offset;
    return offsetobj;
    }

  /** Returns the tag name for this entry. This is a DeSAXalization method.
   * @return The required tag name.
   */

  public String getTagName() {
    return tagname;
    }

  /** Returns the key used to look up this entry within the XMLAccessFile. 
   * This is a DeSAXalization method.
   * @return The required key.
   */

  public String getKey() {
    return key;
    }

  /** Returns the offset of this entry within the XMLAccessFile as a <code>long</code>
   * @return The required byte offset.
   */

  public long getOffsetLong() {
    return offset;
    }

  /** Public default constructor for the XMLAccessManifestEntry required for SAXalization.
   */

  public XMLAccessManifestEntry() {
    offsetobj = new RandomAccessPointer();
    }

  /** Constructs an XMLAccessManifestEntry with the specified fields.
   * @param key The key used to look up this entry within the XMLAccessFile.
   * @param offset The byte offset of this entry within the XMLAccessFile.
   * @param tagname The tag name with which this entry will appear within the XMLAccessFile.
   */

  public XMLAccessManifestEntry(String key, long offset, String tagname) {
    this.key = key;
    this.tagname = tagname;
    this.offset = offset;
    offsetobj = new RandomAccessPointer(offset);
    }
  }
