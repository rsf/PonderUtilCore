package uk.org.ponder.xml;

import org.xml.sax.SAXException;

import uk.org.ponder.util.Logger;

import uk.org.ponder.stringutil.CharWrap;

import uk.org.ponder.saxalizer.SAXalizable;
import uk.org.ponder.saxalizer.DeSAXalizable;
import uk.org.ponder.saxalizer.SAXAccessMethodSpec;

/** This class encapsulates the data in the final fixed-format section of an
 * XMLAccessFile. This section is written with the value of <code>MANIFEST_POINTER_TAG</code>
 * (currently <code>&lt;manifestpointer&gt;</code>) and contains the byte offset
 * of the manifest itself within the file (of type <code>XMLAccessManifest</code> and a
 * string containing the manifest type.
 */

public class XMLAccessManifestPointer implements SAXalizable, DeSAXalizable {
  /** The tag to be used when writing the <code>XMLAccessManifestPointer</code> to the
   * XMLAccessFile (currently <code>&lt;manifestpointer&gt;</code>).
   */
  public static final String MANIFEST_POINTER_TAG = "manifestpointer";
  private static final int MANIFEST_TYPE_LENGTH = 32;
  private RandomAccessPointer manifestoffsetobj;
  private long manifestoffset;
  private String manifesttype;
    //   <manifestpointer>\n             2 + manifestpointer(15) + 2     = 19
    //     <manifestoffset>(11 bytes)</manifestoffset>\n   4 + 7 + 11 + 9                  = 31
    //     <manifesttype>(16 bytes)</manifesttype>\n     4 + 6 + 16 + 8                  = 34
    //   </manifestpointer>\n            2 + 2 + manifestpointer(15) + 2 = 21
    // </roottag>\n                      4 + roottaglength               = 4 + roottaglength
    //                                                                   = 109 + roottaglength
  private static SAXAccessMethodSpec[] setmethods = new SAXAccessMethodSpec[] {
    new SAXAccessMethodSpec("manifestoffset", "setManifestOffset", RandomAccessPointer.class),
    new SAXAccessMethodSpec("manifesttype", "setManifestType", String.class)};
  
  /** Returns the length in bytes of this XMLAccessManifestPointer object when written
   * to the XMLAccessFile. This is probably 109 bytes plus the length of the root tag
   * for the XMLAccessFile, assuming that the XMLWriter indent width is 2 and the 
   * underlying file is encoded in UTF-8 or a similar 8-bit encoding.
   */

  public static int getEncodedPointerLength() {
    return MANIFEST_POINTER_TAG.length() * 2 + 5 + // length of enclosing tag
      setmethods[0].xmlname.length() * 2 + 5 +         // length of manifestoffset tag
      setmethods[1].xmlname.length() * 2 + 5 +         // length of manifesttype
      11 + MANIFEST_TYPE_LENGTH +                  // length of data within
      6 * XMLWriter.INDENT_WIDTH +                 // length of indentation
      4;                                           // length of newlines
    }

  public SAXAccessMethodSpec[] getSAXSetMethods() {
    return setmethods;
    }
  
  public SAXAccessMethodSpec[] getSAXGetMethods() {
    return new SAXAccessMethodSpec[] {
      new SAXAccessMethodSpec("manifestoffset", "getManifestOffset", RandomAccessPointer.class),
      new SAXAccessMethodSpec("manifesttype", "getManifestType", String.class)};
    }

  /** Sets the byte offset of the XMLAccessManifest within the XMLAccessFile pointed
   * to by this pointer. This is a SAXalization method.
   * @param manifestoffestobj The new manifest offset.
   */

  public void setManifestOffset(RandomAccessPointer manifestoffsetobj) {
    this.manifestoffsetobj = manifestoffsetobj;
    manifestoffset = manifestoffsetobj.offset;
    Logger.println("Manifest offset determined to be "+manifestoffset,
		   Logger.DEBUG_INFORMATIONAL);
    }
  /** Sets the type of the XMLAccessManifest for this XMLAccessFile. This is a 
   * SAXalization method.
   * @param manifesttype The required manifesttype. This will be padded with spaces or truncated
   * as appropriate to make it exactly <code>MANIFEST_TYPE_LENGTH</code> characters
   * long (currently 32).
   * @exception SAXException If the manifest type does not match the manifest type
   * previously stored. Since this is a SAXalization method, this will cause an 
   * error when deserializing the manifest pointer.
   */
  
  public void setManifestType(String manifesttype) throws SAXException {
    // always make sure stored string is exactly length of MANIFEST_TYPE_LENGTH,
    // by padding or truncating as necessary.
    String truncated;
    if (manifesttype.length() > MANIFEST_TYPE_LENGTH) {
      truncated = manifesttype.substring(0, MANIFEST_TYPE_LENGTH);
      }
    else if (manifesttype.length() < MANIFEST_TYPE_LENGTH) {
      CharWrap svb = new CharWrap(manifesttype);
      for (int i = 0; i < MANIFEST_TYPE_LENGTH - manifesttype.length(); ++i) {
	svb.append(' ');
	}
      truncated = svb.toString();
      }
    else truncated = manifesttype;

    if (this.manifesttype != null && ! (truncated.equals(manifesttype))) {
      throw new SAXException("Incorrect file format for XMLAccessFile: expected type |"+
			     this.manifesttype +"| but found type |"+truncated+"|");
      }
    this.manifesttype = truncated;
    Logger.println("Manifest type set to |"+this.manifesttype+"|",
		   Logger.DEBUG_INFORMATIONAL);
    }

  /** Returns the byte offset of the XMLAccessManifest object pointed to by this
   * pointer. This is a DeSAXalization method.
   * @return The required manifest offset.
   */
 
  public RandomAccessPointer getManifestOffset() {
    if (manifestoffsetobj == null) {
      manifestoffsetobj = new RandomAccessPointer();
      }
    manifestoffsetobj.offset = manifestoffset;
    return manifestoffsetobj;
    }

  /** Returns the byte offset of the XMLAccessManifest object as a <code>long</code>.
   * @return The required manifest offset.
   */

  public long getManifestOffsetLong() {
    return manifestoffset;
    }

  /** Sets the byte offset of the XMLAccessManifest object as a <code>long</code>.
   * @return The new required manifest offset.
   */

  public void setManifestOffsetLong(long manifestoffset) {
    this.manifestoffset = manifestoffset;
    }

  /** Gets the manifest type string associated with this XMLAccessFile. This is a 
   * DeSAXalization method.
   */

  public String getManifestType() {
    return manifesttype;
    }

  /** Removes all state associated with this XMLAccessManifestPointer object. This
   * allows reuse of this object for SAXalization without the chance for a
   * <code>SAXException</code> if the manifest types do not match.
   */

  public void blastState() {
    manifesttype = null;
    }

  }
