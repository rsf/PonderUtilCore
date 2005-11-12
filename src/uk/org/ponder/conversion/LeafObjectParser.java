package uk.org.ponder.conversion;


/** A minimal interface which expresses the conversion of simple (leaf, 
 * non-composite) objects to and from a String representation.
 */

public interface LeafObjectParser {
  /** Parses a leaf object (i.e. one which is not composite) from a String
   * representation.
   * @param toparse The putative leaf data to be parsed.
   * @return The successfully parsed object.
   */
  Object parse(String toparse);
  /** The DeSAXalizer calls this method when it wishes to render a leaf node
   * object into textual form.
   * @param torender The object to be rendered.
   * @return A String holding the rendered text.
   */
  String render(Object torender);
  }
