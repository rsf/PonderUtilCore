/*
 * Created on 11 Sep 2008
 */
package uk.org.ponder.conversion;

/** A refinement of LeafObjectParser which is capable of parsing into multiple
 * return types.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *
 */
public interface PolymorphicLeafObjectParser extends LeafObjectParser {
  public Object parse(Class returntype, String bulk);
  
}
