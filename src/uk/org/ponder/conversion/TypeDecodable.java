/*
 * Created on 1 Jul 2008
 */
package uk.org.ponder.conversion;

/** An interface implemented by a consumer of a deserialization operation, which
 * allows it to report a Java Object or Class to be used to initiated decoding,
 * which can then proceed in a type-aware fashion.
 * 
 * @author Antranig Basman (amb26@ponder.org.uk)
 */

public interface TypeDecodable {
  /** Return either a Java Class or Object which should be used to begin a 
   * deserialization operation. This is the same semantic as is used for the
   * "classorobject" parameters applied to the {@link SerializationProvider}.
   * 
   * @return Either a Java class or Object to be used to begin deserializing onto.
   * If a Class, the Class needs to be default-constructible.
   */
  public Object getDecodeTarget();
}
