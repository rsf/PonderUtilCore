package uk.org.ponder.hashutil;

import uk.org.ponder.byteutil.ByteWrap;

/** This interface specifies an object capable of incrementally digesting a stream
 * of bytes in order to produce an integer hash value.
 */

public interface Hasher {
  /** Resets this hasher to its initial state. */
  public void reset();
  //public int eat(byte[] buffer, int start, int length);
  /** Digests the specified sequence of bytes, and returns the new hash value.
   * @param b The bytes to be digested.
   * @return The new hash value.
   */
  public int eat(ByteWrap b);
  }
