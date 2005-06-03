package uk.org.ponder.byteutil;

import java.text.DecimalFormat;

/** A very useful utility class to wrap a sequence of bytes in an array, covering over to
 * some extent the lack of pointers in the Java language.
 */

public class ByteWrap {
  /** The array holding the bytes represented by this ByteWrap. */
  public byte[] bytes;

  /** The offset within the array that the represented bytes begin. */
  public int offset;

  /** The number of bytes starting at <code>offset</code> that form the represented
   * sequence. Making this protected is an oddly inconistent decision, so sue me. */
  protected int size; 

  /** The default constructor performs no initialization, for those who wish to use
   * ByteWraps in the raw.
   */
  public ByteWrap() {
    }

  /** Constructs a new ByteWrap with the specified length. A new array of the correct 
   * length is constructed to represent the sequence, which begins at the beginning of
   * the array.
   * @param length The length of the ByteWrap to be constructed. */

  public ByteWrap(int length) {
    bytes = new byte[length];
    size = length;
    }

  /** Constructs a new ByteWrap wrapping a portion of an already existing array.
   * @param bytes The array holding the sequence to be wrapped.
   * @param offset The index of the beginning of the sequence to be wrapped.
   * @param size The length of the sequence to be wrapped.
   */

  public ByteWrap(byte[] bytes, int offset, int size) {
    this.bytes = bytes;
    this.offset = offset;
    this.size = size;
    }
  
  /** Imbues an already existing ByteWrap to wrap a portion of another ByteWrap.
   * @param other The other ByteWrap containing the sequence to be wrapped.
   * @param index The index within the other ByteWrap's sequence of the sequence
   * to be wrapped.
   * @param length The length of the sequence to be wrapped.
   */

  public ByteWrap imbue(ByteWrap other, int index, int length) {
    index += other.offset;
    bytes = other.bytes; offset = index; size = length;
    return this;
    }

  /** Ensures that this ByteWrap has enough capacity in its array to store content
   * of the specified size, by rebinding it to a larger-sized array if necessary.
   * @param requiredsize The length of the sequence the ByteWrap is required to
   * store.
   */

  public void ensureCapacity(int requiredsize) {
    if (bytes == null || bytes.length < requiredsize) {
      bytes = new byte[requiredsize];
      offset = 0; // set offset to 0, although strictly should not be called on shared array
      }
    size = requiredsize;
    }

  // All methods after this point do NOT rebind the array reference.

  /** Writes the supplied <code>long</code> one byte at a time starting at the specified
   * position in the wrapped sequence. The data is written in the normal big-endian order.
   * @param pos The position to start writing the bytes.
   * @param data The data to be written.
   */

  public void write_at8(int pos, long data) {
    pos += offset;
    bytes[pos++] = (byte) (data >> 56);
    bytes[pos++] = (byte) (data >> 48);
    bytes[pos++] = (byte) (data >> 40);
    bytes[pos++] = (byte) (data >> 32);
    bytes[pos++] = (byte) (data >> 24);
    bytes[pos++] = (byte) (data >> 16);
    bytes[pos++] = (byte) (data >>  8);
    bytes[pos] =   (byte)  data;
    }

  /** Reads the bytes starting at the specified position, interpreting them as a
   * <code>long</code> in the usual big-endian order.
   * @param pos The position from which to read bytes.
   * @return The byte sequence interpreted as a <code>long</code>
   */

  public long read_at8(int pos) {
    pos += offset;
    // masking is necessary since widening conversions extend sign bit
    // casting is necessary, although the langspec is ambiguous!! 4.2.2
    return 
      ((long)(bytes[pos] & 0xff) << 56) +
      ((long)(bytes[pos+1] & 0xff) << 48) +
      ((long)(bytes[pos+2] & 0xff) << 40) +
      ((long)(bytes[pos+3] & 0xff) << 32) +
      ((bytes[pos+4] & 0xff) << 24) +
      ((bytes[pos+5] & 0xff) << 16) +
      ((bytes[pos+6] & 0xff) << 8) +
      ( bytes[pos+7] & 0xff);
    }

  /** Writes the supplied <code>int</code> one byte at a time starting at the specified
   * position in the wrapped sequence. The data is written in the normal big-endian order.
   * @param pos The position to start writing the bytes.
   * @param data The data to be written.
   */

  public void write_at4(int pos, int data) {
    pos += offset;
    // NB narrowing conversions in Java automatically mask
    bytes[pos++] = (byte) (data >> 24);
    bytes[pos++] = (byte) (data >> 16);
    bytes[pos++] = (byte) (data >>  8);
    bytes[pos] =   (byte)  data;
    }

  /** Reads the bytes starting at the specified position, interpreting them as a
   * <code>int</code> in the usual big-endian order.
   * @param pos The position from which to read bytes.
   * @return The byte sequence interpreted as a <code>int</code>
   */

  public int read_at4(int pos) {
    pos += offset;
    // masking is necessary since widening conversions extend sign bit
    return 
      ((bytes[pos]   & 0xff) << 24) +
      ((bytes[pos+1] & 0xff) << 16) +
      ((bytes[pos+2] & 0xff) << 8) +
      ( bytes[pos+3] & 0xff);
    }

  /** Writes the three low-order bytes of the supplied <code>int</code> one
   * byte at a time starting at the specified position in the wrapped
   * sequence. The data is written in the normal big-endian order.
   * @param pos The position to start writing the bytes.
   * @param data The data to be written.  
   */

  public void write_at3(int pos, int data) {
    pos += offset;
    bytes[pos++] = (byte) (data >> 16);
    bytes[pos++] = (byte) (data >>  8);
    bytes[pos] =   (byte)  data;
    }

  /** Reads three bytes starting at the specified position, interpreting
   * them as the low-order bytes of an <code>int</code> in the usual
   * big-endian order.
   * @param pos The position from which to read bytes.
   * @return The byte sequence interpreted as a <code>int</code> 
   */

  public int read_at3(int pos) {
    pos += offset;
    return 
      ((bytes[pos]   & 0xff) << 16) + 
      ((bytes[pos+1] & 0xff) << 8) + 
      ( bytes[pos+2] & 0xff);
    }

  /** Writes the two low-order bytes of the supplied <code>int</code> one
   * byte at a time starting at the specified position in the wrapped
   * sequence. The data is written in the normal big-endian order.
   * @param pos The position to start writing the bytes.
   * @param data The data to be written.  
   */

  public void write_at2(int pos, int data) {
    pos += offset;
    bytes[pos++] = (byte) (data >> 8);
    bytes[pos] =   (byte)  data;
    }

  /** Reads two bytes starting at the specified position, interpreting
   * them as the low-order bytes of an <code>int</code> in the usual
   * big-endian order.
   * @param pos The position from which to read bytes.
   * @return The byte sequence interpreted as a <code>int</code> 
   */

  public int read_at2(int pos) {
    pos += offset;
    return 
      ((bytes[pos]   & 0xff) << 8) + 
      ( bytes[pos+1] & 0xff);
    }

  /** Writes the low-order byte of the supplied <code>int</code> at the
   * specified position in the wrapped sequence.
   * @param pos The position to write the byte.
   * @param data The data to be written.  
   */

  public void write_at1(int pos, int data) {
    bytes[pos + offset] = (byte)data;
    }

  /** Reads a byte from the specified position, interpreting
   * it as the low-order byte of an <code>int</code>.
   * @param pos The position from which to read the byte.
   * @return The byte sequence interpreted as a <code>int</code> 
   */

  public int read_at1(int pos) {
    return bytes[pos + offset] & 0xff;
    }

  /** Writes the entire sequence from another ByteWrap to a specified position into
   * this one. The target ByteWrap is assumed to have enough space to accommodate the
   * data.
   * @param pos The position within this ByteWrap to write the sequence.
   * @param other The ByteWrap wrapping the sequence to be copied.
   */
  
  public void write_at(int pos, ByteWrap other) {
    pos += offset;
    for (int i = 0; i < other.size; ++ i) {
      bytes[pos + i] = other.bytes[other.offset + i];
      }
    }

  /** Writes a portion of the sequence from another ByteWrap to a specified position into
   * this one. The target ByteWrap is assumed to have enough space to accommodate the
   * data.
   * @param pos The position within this ByteWrap to write the sequence.
   * @param other The ByteWrap wrapping the sequence to be copied.
   * @param index The index within the other ByteWrap marking the start of the sequence
   * to be copied.
   * @param length The length of the sequence to be copied.
   */
 
  public void write_at(int pos, ByteWrap other, int index, int length) {
    pos += offset;
    index += other.offset;
    for (int i = 0; i < length; ++ i) {
      bytes[pos + i] = other.bytes[index + i];
      }
    }

  /** Returns the number of elements in the sequence wrapped by this ByteWrap.
   * @return the number of elements in the sequence wrapped by this ByteWrap.
   */

  public int size() {
    return size;
    }

  /** Shuffles part of the contents of this ByteWrap forwards, to make space for more
   * elements at a particular position. It is assumed there is enough space at the
   * end of the wrapped array to accommodate the resulting expansion of the sequence.
   * @param fromposition The index at which more space is to be made.
   * @param amount The number of positions forward the data is to be shuffled.
   */

  public void shuffle(int fromposition, int amount) {
    /*
    System.out.println("shuffle: arraysize = " + bytes.length + " offset = "+offset 
		       +" size = "+ size + " fromposition = "+ fromposition
		       +" amount = "+ amount);
    */
    fromposition += offset;
    System.arraycopy(bytes, fromposition, 
		     bytes, fromposition + amount, offset + size - fromposition);
    size += amount;
    }

  private static final int SPAN = 16;
  private static DecimalFormat column;

  /** Converts the supplied integer in the range <code>[0..15]</code> into its equivalent
   * hexadecimal digit.
   * @param nibble The integer to be converted.
   * @return The integer represented as a hexadecimal digit.
   */

  public static char toHex(int nibble) {
    if (nibble < 10) return (char)('0'+nibble);
    else return (char)('A'+nibble-10);
    }

  /** Compares this ByteWrap with another object. It will compare equal to another 
   * ByteWrap wrapping the same sequence of bytes. This method is very inefficient.
   * @param othero The ByteWrap to be compared to.
   * @return <code>true</code> if the other object if the supplied object is a
   * ByteWrap wrapping the same sequence of bytes.
   */

  public boolean equals(Object othero) {
    if (othero instanceof ByteWrap) {
      ByteWrap other = (ByteWrap)othero;
      if (other.size() == size()) {
	for (int i = 0; i < size; ++ i) {
	  if (read_at1(i) != other.read_at1(i))
	    return false;
	  }
	return true;
	}
      }
    return false;
    }

  /** Represents this ByteWrap as a String for debugging purposes. If it contains
   * 4 characters or less, it will be compactly represented as a hexadecimal number. 
   * If it is longer, it will be represented in a style similar to UNIX "od" or "dd"
   * @return The contents of this ByteWrap as a debug string.
   */

  public String toString() {
    if (size <= 4) {
      if (size == 4)
	return intToHex(read_at4(0), 4);
      else if (size == 3) 
	return intToHex(read_at3(0), 3);
      else if (size == 2) 
	return intToHex(read_at2(0), 2);
      else return intToHex(read_at1(0), 1);
      }
    if (column == null) {
      column = new DecimalFormat("000:" );
      }
    StringBuffer build = new StringBuffer();
    for (int i = 0; i < size; i+= SPAN) {
      int limit = (size - i > SPAN)? SPAN:size - i;
      build.append(column.format(i));
      for (int j = 0; j < SPAN; ++j) {
	if (j < limit) {
	  build.append(toHex((bytes[i+j+offset] & 0xf0) >> 4));
	  build.append(toHex( bytes[i+j+offset] & 0xf)).append(' ');
	  }
	else build.append("   ");
	}
      build.append("    ");
      for (int j = 0; j < limit; ++ j) {
	int c = bytes[i + j + offset];
	build.append( c >= 32 && c < 127? (char)c : '.');
	}
      build.append("\n"); 
      }
    return build.toString();
    }
  private static ByteWrap temphex = new ByteWrap(4);
  /** Converts an integer to a string containing a hexadecimal representation.
   * @param tohex THe integer to be represented.
   * @return A string representing the integer's value in hexadecimal.
   */
  public static final String intToHex(int tohex) {
    return intToHex(tohex, 4);
    }
  /** Converts some number of low-order bytes of an integer into a hexadecimal
   * representation in a String.
   * @param tohex The integer to be converted to hex.
   * @param bytes The number of low-order bytes (between 1 and 4) to be converted
   * into hex.
   * @return The required hexadecimal representation.
   */
  public static final String intToHex(int tohex, int bytes) {
    StringBuffer build = new StringBuffer();
    //    System.out.println("intToHex "+tohex+" "+bytes);
    if (bytes >= 2) { // reverse switch statement!
      if (bytes >= 3) {
	if (bytes == 4) {
	  build.append(toHex((tohex >> 28) & 0xf));
	  build.append(toHex((tohex >> 24) & 0xf));
	  build.append(' ');
	  }
	build.append(toHex((tohex >> 20) & 0xf));
	build.append(toHex((tohex >> 16) & 0xf));
	build.append(' ');
	}
      build.append(toHex((tohex >> 12) & 0xf));
      build.append(toHex((tohex >> 8) & 0xf));
      build.append(' ');
      }
    build.append(toHex((tohex >> 4) & 0xf));
    build.append(toHex((tohex) & 0xf));
    //    build.append(' ');
    return build.toString();
    }

  

  }
