package uk.org.ponder.saxalizer;

import org.xml.sax.SAXException;

import uk.org.ponder.stringutil.CharWrap;

/** A class wishing to be registered as a SAX leaf type implements this interface.
 * A SAX leaf type is one that may appear as a leaf node or as an attribute of the
 * XML tree, during either SAXalization or DeSAXalization.
 */

public interface SAXLeafTypeParser {
  /** The SAXalizer calls this method when it wishes to deserialize a piece of
   * putative leaf node data. The method returns the parsed leaf object.
   * @param toparse The putative leaf data to be parsed.
   * @exception SAXException If the data cannot be parsed as an object of the
   * required type.
   * @return The successfully parsed object.
   */
  Object parse(String toparse) throws SAXException;
  /** The DeSAXalizer calls this method when it wishes to render a leaf node
   * object into textual form.
   * @param torender The object to be rendered.
   * @param renderinto A CharWrap object that the renderer may choose to use to
   * perform the rendering. This allows global economisation of objects, since this
   * object is unique per DeSAXalizer invocation.
   * @return A CharWrap containing the rendered text.
   */
  CharWrap render(Object torender, CharWrap renderinto);
  }
