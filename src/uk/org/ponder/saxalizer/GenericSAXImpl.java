package uk.org.ponder.saxalizer;

import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

//import uk.org.ponder.stringutil.CharWrap;

// This class is similar to the DOM ElementNode!
// It maintains a tree of children all of type GenericSax, and also keeps
// hold of data for arbitary attributes.

/** The class <code>GenericSAXImpl</code> is useful as a base class for SAXalizable
 * classes that wish to store the information from arbitrary XML subtags. 
 * <code>GenericSAXImpl</code> stores all attributes and all subtags that are
 * seen for the tag that corresponds to it, and in addition will create
 * <code>GenericSAXImpl</code> children so that subtags are also stored
 * recursively. 
 * <p>Ultimately we will arrive at a <code>GenericSAXImpl</code> leaf node, which
 * has no children (although it may have attributes), and contains a <code>String</code>
 * representing the text of the node.
 */

public class GenericSAXImpl implements GenericSAX {
  // There are no specific SAX methods that a GenericSAXImpl provides --- 
  // the SAXalizer directly recognises it and sends it other GenericSAXImpl objects. 
  public SAXAccessMethodSpec[] getSAXSetMethods() {return null;}
  public SAXAccessMethodSpec[] getSAXSetAttrMethods() {return null;}

  public SAXAccessMethodSpec[] getSAXGetMethods() {return null;}
  public SAXAccessMethodSpec[] getSAXGetAttrMethods() {return null;}

  String tagname;
  String data; // wtf is this data - only meaningful for leaf nodes.
  TreeMap attrs;
  String comment;
  // A heterogeneous vector. 
  Vector children; // not a hash, since tags may be identical - also, order should be preserved.

  // The child will be another GenericSax. Leaf nodes will just have data.
  /** Add the specified child object to the collection.
   * @param child The child object to add.
   */
  public void addChild(GenericSAX child) {
    if (children == null) children = new Vector();
    children.addElement(child);
    }
  /** Return the number of child objects.
   * @return The number of child objects.
   */
  public int size() {
    return (children == null? 0 : children.size() );
    }
  /** Return an enumeration of all child objects.
   @return The required child object.
  */
  public Enumeration getChildEnum() {
    return children == null? null : children.elements();
    }
  /** Return the child object corresponding to the tag with the given name.
   @param tagname The tag name that the child object is required for.
   @return The required child object, or <code>null</code> if not found.
  */
  public GenericSAXImpl findChild(String tagname) {
    //    System.out.println("Tagname: "+ tagname +" required");
    for (int i = 0; i < children.size(); ++ i) {
      GenericSAXImpl childi = (GenericSAXImpl)children.elementAt(i);
      //      System.out.println("Passing child with tag: "+childi.tagname);
      if (childi.tagname.equals(tagname)) return childi;
      }
    return null;
    }
  /** Set the data for this node to the specified string - only used if this
   * is a leaf node.
   *  @param s The new node data.
   */
  public void setData(String s) {
    data = s;
    }
  /** Return the stored data for this node.
   * @return The stored data.
   */
  public String getData() {
    return data;
    }
  /** Set the tag name for this node to the specified string.
   * @param s The new tag name.
   */
  public void setTag(String s) {
    tagname = s;
    }

  /** Gets the tag name for this node.
   * @return The required tag name
   */
  public String getTag() {
    return tagname;
    }
  public Map getAttributes() {
    return attrs;
  }
 

  // The data attribute will only be used if this is a leaf node.

  /*  GenericSAXImpl(String tagname, String data, AttributeList attrs) {
    this.tagname = tagname;
    this.data = data;
    this.attrs = new SAXAttributeHash(attrs);
    }*/
  }
