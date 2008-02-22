/*
 * Created on Nov 22, 2004
 */
package uk.org.ponder.mapping;

/**
 * Encodes a primitive operation on an abstract bean model, consisting of either
 * the attachment or detachment of an object appearing at a particular bean
 * path.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class DataAlterationRequest {
  public static final String ADD = "add";
  public static final String DELETE = "delete";
  
  /** A Distinguished value for the field "data" which determines that the
   * the value will not apply. However the DAR will proceed through the applier
   * in order that any guards may execute.
   */
  public static final Object INAPPLICABLE_VALUE = new String("Inapplicable Value");
  /** The EL path to which the object value is to be delivered, or to be 
   * deleted from.
   */
  public String path;
  /** In the case of an ADD request, the value to be added. In the case of a
   * DELETE request, can be null if the path designates a Map property, 
   * in which case the final path component will be used as the key to be deleted.
   * If the path designates a java.util.Collection, the object will be used as
   * the argument to the remove() method, in which case reasonable Object 
   * equality semantics must have been provided. 
   */
  public Object data;
  /** Whether any data conversions should be considered when applying the
   * request */
  public boolean applyconversions = true;
  /** The type of the request, either an ADD (default) or DELETE.
   */
  
  public String type = ADD;
  
  /** The encoding which has been applied to the {@link #data} object, if it is
   * not already in the Java-native form. 
   */
  public String encoding = DEFAULT_ENCODING;
  /** Value for {@link #encoding}, indicating that non-leaf data should be
   * encoded in JSON.
   */
  public static final String JSON_ENCODING = "JSON";
  /** Value for {@link #encoding}, indicating that non-leaf data should be
   * encoded in XML.
   */
  public static final String XML_ENCODING = "XML";
  
  /** Default encoding value where none is specified **/
  public static final String DEFAULT_ENCODING = JSON_ENCODING;
  
  public DataAlterationRequest(String path, Object data) {
    this.path = path;
    this.data = data;
  }
  public DataAlterationRequest(String path, Object data, String type) {
    this.path = path;
    this.data = data;
    this.type = type;
  }
  
  public static String flattenData(Object data) {
    String leafdata = data instanceof String[] ? ((String[])data)[0] : (String)data;
    return leafdata;
  }
}
