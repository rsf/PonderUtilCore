package uk.org.ponder.saxalizer;
  /** The DeSAXalizable interface is implemented by any object wishing
   * to call on the services of the DeSAXalizer class for serialization to
   * a SAX stream. The interface requires the object to report
   * a list of get methods for querying existing subobjects 
   * requiring serialisation. */

public interface DeSAXalizable {
  /** This method returns a list of <code>get</code> methods which the <code>DeSAXalizer</code>
   * will call when wishes to write this object to a stream. 
   * The <code>DeSAXalizer</code> will call this method on one object of each
   * <code>DeSAXalizable</code> class that it decides to serialize to an XML stream.
   * <p>The returned array of method specifications has an entry specifying the
   * XML tagname, method name and return type of each suitable get method of this
   * class. The <code>DeSAXalizer</code> will use reflection to locate and invoke
   * the specified method, so it must be a public method of a public class.
   *
   * @return An array of get method specifications; <code>null</code> is allowed if
   * there are no such get methods.
   */
  SAXAccessMethodSpec[] getSAXGetMethods();

  }

