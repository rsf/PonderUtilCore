/*
 * Created on 11-Jan-2006
 */
package uk.org.ponder.saxalizer;

public class Constants {
  /** The name of the special attribute specifying the type of created object
   * as parsed by SAXalizer. This is either a fully qualified Java class name,
   * or a nickname as recognised by ClassNameManager.
   */
  public static final String TYPE_ATTRIBUTE_NAME = "type";

  /** The name of the attribute specifying the key, where this element 
   * represents an entry in a Java Map or BeanLocator.
   */
  public static final String KEY_ATTRIBUTE_NAME = "key";
  
  /** The name of the attribute specifying the type of key object - will
   * default to java.lang.String where absent.
   */
  public static final String KEY_TYPE_ATTRIBUTE_NAME = "key-type";
}
