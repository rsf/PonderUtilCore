package uk.org.ponder.saxalizer;
  /** The SAXalizable interface is implemented by any object wishing
   * to call on the services of the SAXalizer class for serialization 
   * from a SAX stream. The interface requires the object to report
   * a list of set methods suitable for attaching subobjects discovered
   * in the stream. */

public interface SAXalizable {
  //  SAXAccessMethodSpec[] getSAXGetMethods();

  /** This method returns a list of <code>set</code> methods which the <code>SAXalizer</code>
   * will call when it sees subtags of the tag corresponding to this object. 
   * The <code>SAXalizer</code> will call this method on one object of each
   * <code>SAXalizable</code> class that it determines corresponds to an XML
   * tag seen in the SAX stream.
   * <p>The returned array of method specifications has an entry specifying the
   * XML tagname, method name and return type of each suitable set method of this
   * class. The <code>SAXalizer</code> will use reflection to locate and invoke
   * the specified method, so it must be a public method of a public class.
   *
   * @return An array of set method specifications; <code>null</code> is allowed if
   * there are no such set methods.
   */
  SAXAccessMethodSpec[] getSAXSetMethods();
  }

