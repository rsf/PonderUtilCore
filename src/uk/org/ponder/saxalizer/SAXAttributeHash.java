package uk.org.ponder.saxalizer;

import java.util.Collections;
import java.util.Enumeration;
import java.util.TreeMap;

import org.xml.sax.AttributeList;

//import uk.org.ponder.util11.TreeMap;

// NB - this is similar the DOM NamedNodeMap, only we do not allow indexing by integer.
// An oddly restrictive DOM thing....

/** The class SAXAttributeHash is used within the class GenericSAXImpl in order to
 * allow quick access to the XML attribute values stored within it by lookup
 * on attribute name.
 */

public class SAXAttributeHash {

  // this is a hashtable of Strings (attribute names) to SAXAttributes
  private TreeMap attributes = new TreeMap();
  /** Puts the supplied attribute into this hash.
   * @param name The name of the attribute to be added.
   * @param type The type of the attribute to be added. This parameter is effectively disused,
   * but should really be set to <code>CDATA</code>. Isn't XML boring.
   * @param value The value of the attribute to be added.
   */
  public void put(String name, String type, String value) {
    //    System.out.println("Putting attribute "+name+" with value "+value);
    attributes.put(name, new SAXAttribute(type, value));
    }
  /** Returns the attribute with the specified name.
   * @param The name of the required attribute.
   * @return The required attribute.
   */
  public SAXAttribute get(String name) {
    return (SAXAttribute)attributes.get(name);
    }
  // Ultimately this will have to fabricate an AttributeList once we have serialization.
  // This will need a new concrete implementation of AttributeList that actually allows
  // random set access (silly people)
  Enumeration keys() {
    return Collections.enumeration(attributes.keySet());
    }
  SAXAttributeHash(AttributeList attrs) {
    for (int i = 0; i < attrs.getLength(); ++ i) {
      put(attrs.getName(i), attrs.getType(i), attrs.getValue(i));
      }
    }
  public SAXAttributeHash() {}
  }
