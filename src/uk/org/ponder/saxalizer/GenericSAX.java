package uk.org.ponder.saxalizer;

import java.util.Enumeration;

/** Classes implementing the GenericSAX interface do not have
 * specifically named methods for all of the subobjects that they
 * want serialized as XML. Instead, objects of arbitary types are
 * piled into them via the <code>addChild</code> method, and read
 * out by the <code>size</code> and <code>elementAt</code>
 * methods. This makes GenericSAX objects very much more like
 * DOM nodes than plain SAXalizable objects.  */

public interface GenericSAX extends SAXalizableExtraAttrs, DeSAXalizableExtraAttrs {
  /** Gets the (text) data, if any, associated with this XML node. Note that text is
   * currently not supported for nodes which in addition have children.
   * @return The required text data.
   */
  public String getData();
  /** Sets the text data associated with this XML node.
   * @param s The required text data.
   */
  public void setData(String s);
  /** Gets the XML tag that will be supplied to this object in its serialized form.
   * @return The required XML tag.
   */
  public String getTag();
  /** Sets the XML tag that will be applied to this object in its serialized form.
   * @param s The required XML tag.
   */
  public void setTag(String s);
  /** Adds the specified node to this node as a child.
   * @param child The required child node.
   */
  public void addChild (GenericSAX child);
  /** Finds the first (if any) child node having the specified tag.
   * @param The required tag to be found.
   * @return The first child node bearing the supplied tag, if any, or
   * <code>null</code> if no such child exists.  */
  public GenericSAXImpl findChild(String tagname);
  /** Returns the number of child nodes for this node.
   * @return The required number of child nodes.
   */
  public int size();
  /** Returns an enumeration of all child nodes for this node.
   * @return The required enumeration of all child nodes.
   */
  public Enumeration getChildEnum();
  }
