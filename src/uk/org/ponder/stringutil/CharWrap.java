package uk.org.ponder.stringutil;

import uk.org.ponder.hashutil.CRC32;

import uk.org.ponder.byteutil.ByteWrap;

/** <code>CharWrap</code> provides the basic functionality of
 * the Java <code>StringBuffer</code> class, only without the overhead
 * of synchonised methods and with the ability (and corresponding
 * danger) of direct access to the underlying array of characters.
 *
 * <br><code>CharWrap</code> also integrates with the
 * <code>StringVat</code> to map a string to an integer
 * <code>StringVat</code> ID without causing an extra object creation.
 *
 * <br>At some point either the functionality needs to be added to
 * "pre-intern" <code>String</code> objects from their
 * <code>CharWrap</code> counterparts, or else <code>String</code>s
 * should be removed completely from critical pathways through the
 * code (e.g. <code>SAXalizer</code> and interfaces) 
 */

public class CharWrap {
  /** The default initial size for a CharWrap */
  public static final int INITIAL_SIZE = 64;
  /** An array holding the character data for this CharWrap */
  public char[] storage;
  /** The offset within the <code>storage</code> array at which the data represented
   * by this CharWrap begins */
  public int offset = 0;
  /** The length of the data represented by this CharWrap */
  public int size = 0;

  /** Constructs a CharWrap with the default <code>INITIAL_SIZE</code>
   */
  public CharWrap() {
    storage = new char[INITIAL_SIZE];
    }
  /** Constructs a CharWrap with the specified initial size.
   * @param initialsize The initial size of the character array.
   */
  public CharWrap(int initialsize) {
    storage = new char[initialsize];
    }

  /** Constructs a CharWrap with the specified initial contents, in an array allowing
   * for expansion of factor 2.
   * @param initialcontents The initial contents of the CharWrap
   */
  public CharWrap(String initialcontents) {
    this(initialcontents.length() * 2);
    append(initialcontents);
    }

  /** Constructs a CharWrap around the supplied parameters, i.e.&nbsp;aliasing the supplied
   * storage array.
   * @param storage The array holding the character data.
   * @param offset The offset within the array of the beginning of the character data.
   * @param size The length of the character data.
   */

  public CharWrap(char[] storage, int offset, int size) {
    imbue(storage, offset, size);
    }

  /** Imbues this CharWrap with the supplied parameters, i.e.&nbsp; aliasing the supplied
   * storage array.
   * @param storage The array holding the character data.
   * @param offset The offset of the beginning of the character data within the array.
   * @param size The length of the character data.
   */

  public void imbue(char[] storage, int offset, int size) {
    this.storage = storage;
    this.offset = offset;
    this.size = size;
    }

  // copy the current contents into a new buffer if required to
  // accommodate extra data
  private void ensureCapacity(int requiredsize) {
    if (requiredsize + offset > storage.length) {
      char[] newstorage = new char[requiredsize * 2];
      System.arraycopy(storage, offset, newstorage, 0, size);
      storage = newstorage;
      offset = 0;
      }
    }

  /** Appends the characters in the supplied string onto this CharWrap.
   * @param s A String holding the character data to be appended.
   * @return A reference to this CharWrap.
   */
  
  public CharWrap append(String s) {
    int length = s.length();
    ensureCapacity(size + length);
    s.getChars(0, length, storage, size + offset);
    size += length;
    return this;
    }

  /** Appends the characters in the supplied CharWrap onto this CharWrap.
   * @param toappend A CharWrap holding the character data to be appended.
   * @return A reference to this CharWrap.
   */

  public CharWrap append(CharWrap toappend) {
    append(toappend.storage, toappend.offset, toappend.size);
    return this;
    }

  /** Appends the specified character data onto this CharWrap.
   * @param storage The array holding the character data to be appended.
   * @param offset The offset within the array of the beginning of the character data.
   * @param size The length of the character data.
   * @return A reference to this CharWrap.
   */

  public CharWrap append(char[] array, int start, int length) {
    ensureCapacity(size + length);
    System.arraycopy(array, start, storage, size + offset, length);
    size += length;
    return this;
    }

  /** Appends the specified character onto this CharWrap.
   * @param c The character to be appended.
   * @return A reference to this CharWrap.
   */

  public CharWrap append(char c) {
    ensureCapacity(size + 1);
    storage[size + offset] = c;
    size++;
    return this;
    }

  /**
   * @param i Appends the specified integer rendered as a string onto this 
   * CharWrap.
   * @return A reference to this CharWrap
   */
  public CharWrap append(int i) {
    append(Integer.toString(i));
    return this;
  }
  
  /** Clears this CharWrap by setting its size to zero.
   */

  public CharWrap clear() {
    size = 0;
    return this;
    }

  // All methods after this point do not rebind the buffer
 
  /** Returns the first index at which a character appears in this CharWrap.
   * @param tofind The character to find.
   * @return The first index at which the specified character occurs, or <code>-1</code>
   * if it does not appear.
   */
 
  public int indexOf(char tofind) {
    for (int i = 0; i < size; ++ i) {
      if (storage[i + offset] == tofind) return i;
      }
    return -1;
    }

  /** Returns the number of characters stored within this CharWrap.
   * @return The size of the character data for this CharWrap.
   */
  public int size() {
    return size;
    }

  /** Returns the number of characters that can be appended to this CharWrap before
   * it needs to reallocate its underlying character array.
   * @return The spare capacity at the end of this CharWrap's character array.
   */
   
  public int capacity() {
    return storage.length - offset;
    }
    
  /* Returns the character stored at the specified index of this CharWrap.
   * @param index The index for which the stored character is required.
   * @return The character at the specified index.
   */
  public char charAt(int index) {
    return storage[index + offset];
    }

  /** Converts this CharWrap to a new String object.
   * @return A new String object holding the same character data as this CharWrap.
   */
  public String toString() {
    return new String(storage, offset, size);
    }

  /** Computes a hashcode for this CharWrap.
   * @return An integer hashcode for this CharWap, currently by the CRC32 algorithm.
   */

  public int hashCode() {
    return CRC32.eatquick(storage, offset, size);
    }

  /** Converts this CharWrap to a new String object for debugging purposes. This string
   * resolves all non-printable-ASCII characters into their hex equivalents.
   * @return A String representing this CharWrap with only ASCII printable characters.
   */

  public String toDebugString() {
    CharWrap buildup = new CharWrap();
    for (int i = 0; i < size; ++ i) {
      char charat = storage[offset + i];
      if (charat >= 32 && charat < 127) {
	buildup.append(charat);
	}
      else buildup.append(ByteWrap.charToHex(charat));
      }
    return buildup.toString();
    }

  /** Compares this CharWrap with another object. It is possible for a CharWrap to 
   * compare equal to a String with the same contents as well as to another CharWrap.
   * @param othero An object to compare this CharWrap to.
   * @return Returns <code>true</code> if the other object is either a String or a CharWrap
   * with the same contents as this CharWrap.
   */

  public boolean equals(Object othero) {
    if (othero instanceof String) {
      String other = (String) othero;
      if (other.length() != size) return false;
      for (int i = 0; i < size; ++ i) {
	if (storage[i + offset] != other.charAt(i)) return false;
	}
      return true;
      }
    else if (othero instanceof CharWrap) {
      CharWrap other = (CharWrap)othero;
      if (other.size != size) return false;
      for (int i = 0; i < size; ++ i) {
	if (storage[i + offset] != other.storage[i + other.offset]) return false;
	}
      return true;
      }
    return false;
    }
  }
