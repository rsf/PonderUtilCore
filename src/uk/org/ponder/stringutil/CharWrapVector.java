package uk.org.ponder.stringutil;

//import uk.org.ponder.util.Logger;

import uk.org.ponder.arrayutil.ArrayUtil;

/** This class allows chunk-wise manipulation of character data, in the form of 
 * a vector of CharWrap objects.
 */

public class CharWrapVector {
  private CharWrap[] storage;
  private int filled;

  /** Constructs a new CharWrapVector with the specified initial size.
   * @param size The initial size of the new CharWrapVector.
   */

  public CharWrapVector(int size) {
    if (size < 10) size = 10;
    storage = new CharWrap[size];
    filled = 0;
    }
  
  /** Returns the contents of this CharWrapVector as an array of CharWrap. The represented
   * data will start at index 0 of this array.
   * @return The contents of this CharWrapVector as an array of CharWrap.
   */

  public CharWrap[] getArray() {
    return storage;
    }
  
  private void expand(int newsize) {
    //    Logger.println("Expand from "+storage.length+" to "+newsize, Logger.DEBUG_INFORMATIONAL);
    storage = (CharWrap[]) ArrayUtil.expand(storage, newsize);
    }

  /** Appends the supplied CharWrap object onto the end of this CharWrapVector.
   * @param toappend The CharWrap to be appended.
   */
  
  public void append(CharWrap toappend) {
    if (filled == storage.length) 
      expand(storage.length * 2);
    storage[filled] = toappend;
    filled++;
    }

  /** Appends the specified portion of the supplied CharWrapVector onto the end of this
   * CharWrapVector.
   * @param other The CharWrapVector holding the CharWraps to be appended.
   * @param offset The start index of the CharWraps to be appended.
   * @param length The number of CharWraps to be appended.
   */

  public void append(CharWrapVector other, int offset, int length) {
    if (filled + length > storage.length)
      expand(filled + length > storage.length * 2? filled + length : storage.length * 2);
    /*
    Logger.println("About to call System.arraycopy: ol "+other.storage.length+" offset "
		   +offset+" l "+storage.length+" filled "+filled+" length "+length,
		   Logger.DEBUG_INFORMATIONAL);
    */
    System.arraycopy(other.storage, offset, storage, filled, length);
    filled += length;
    }

  /** This method compares the range of this vector with a range of the same length
   * in another. If there is a mismatch, the index of the mismatch is returned - otherwise
   * it returns -1.
   * @param thisoffset The offset within this CharWrapVector to begin the comparison at.
   * @param other The other CharWrapVector to compare this object with.
   * @param otheroffset The offset within the other CharWrapVector to begin the comparison at.
   * @param length The common length of the segments to be compared from each CharWrapVector.
   * @return -1 if the specified ranges match exactly, otherwise the index of the mismatch
   * point from the offset position.
   */

  public int compareRange(int thisoffset, CharWrapVector other, int otheroffset, int length) {
    for (int i = 0; i < length; ++ i) {
      if ( ! (storage[thisoffset + i].equals(other.storage[otheroffset + i])))
	return i;
      }
    return -1;
    }

  /** Returns the number of elements in this CharWrapVector.
   * @return The number of elements in this CharWrapVector.
   */
  public int size() {
    return filled;
    }

  /** Returns the CharWrap object at the specified index.
   * @param i The index of the required CharWrap object.
   * @return The CharWrap object at the specified index.
   */
  public CharWrap charWrapAt(int i) {
    return storage[i];
    }

  /** Converts this CharWrapVector to a String for debugging purposes. Word boundaries
   * are represented with the character |.
   * @return The data in this CharWrapVector as a String for debugging purposes.
   */

  public String toDebugString() {
    CharWrap togo = new CharWrap();
    for (int i = 0; i < filled; ++ i) {
      togo.append('|').append(storage[i]).append('|');
      }
    return togo.toString();
    }
  }
