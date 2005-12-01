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
  /** Clones an object by returning an equivalent object which shares no
   * state with the original. For most leaf objects simply returns the original
   * if they are known to be immutable.
   * <p>This method is useful since the JDK Object.clone() method is not only
   * vastly inefficient (costing around a microsecond per throw) but also
   * greatly intrusive.
   */
  public Object copy(Object tocopy);
  }
