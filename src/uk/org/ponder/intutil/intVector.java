package uk.org.ponder.intutil;

/** A simple container class representing a vector of native integers.
 */

public class intVector {
  private int[] ints;
  private int size;

  public class intVectorIterator implements intIterator {
    int index = 0;
    public boolean valid() {
      return index != size;
    }
    public void next() {
      ++index;
    }
    public void setInt(int toset) {
      ints[index] = toset;
    }
    public int getInt() {
      return ints[index];
    }
    public boolean equals(Object other) {
      intVectorIterator ivi = (intVectorIterator) other;
      return //ivi.intVector.this == intVector.this &&  // QQQQQ how to express this?
      ivi.index == index;
    }
  }

  public intIterator beginIterator() {
    return new intVectorIterator();
  }

  /** Constructs an intVector with the specified initial capacity.
   * @param initalcapacity The required initial capacity.
   */
  public intVector(int initialcapacity) {
    ints = new int[initialcapacity];
    size = 0;
  }
  public intVector(int[] array) {
    this.ints = array;
    this.size = array.length;
  }
  public void ensureIndex(int index) {
    if (index >= size) {
      int oldsize = size;
      setSize(index + 1);
      for (int i = oldsize; i < index + 1; ++i) {
        ints[i] = 0;
      }
    }
  }

  /** Returns the integer at the specified index.
   * @param i The index of the required integer.
   * @return The integer at index <code>i</code>
   */
  public int intAt(int i) {
    return ints[i];
  }

  public int intAtSafe(int i) {
    ensureIndex(i);
    return intAt(i);
  }
  /** Assigns a value to a member of the vector.
   * @param i the index to assign a value at.
   * @param value the value to assign.
   */

  public void setIntAt(int i, int value) {
    if (i < 0 || i > size) {
      throw new ArrayIndexOutOfBoundsException(
        "Index " + i + " out of bounds [0," + size + ") in setIntAt");
    }
    ints[i] = value;
  }
  /** Assigns a value to a member of the vector, expanding the vector if the supplied index exceeds
   * the vector bounds.
   * @param i the index to assign a value at
   * @param value the value to assign
   */
  public void setIntAtSafe(int i, int value) {
    ensureIndex(i);
    setIntAt(i, value);
  }

  /** Returns the current size of this vector.
   * @return the current size of this vector.
   */
  public int size() {
    return size;
  }

  /** Sets the new size of this vector. 
   * @param newsize The new size of the vector.
   */

  public void setSize(int newsize) {
    if (newsize > ints.length)
      reallocate(newsize + ints.length);
    size = newsize;
  }

  private void reallocate(int newsize) {
    int[] newints = new int[newsize];
    System.arraycopy(ints, 0, newints, 0, size);
    ints = newints;
  }
  /** Appends a new element to the end of this vector, reallocating its storage space if necessary.
   * @param i The integer value to be appended to the vector.
   */
  public void addElement(int i) {
    if (size == ints.length) {
      reallocate(ints.length * 2);
    }
    ints[size] = i;
    ++size;
  }

  /** Inserts the supplied value at the index position specified - following elements
   * will be shifted to the right and the vector capacity expanded if necessary.
   * @param i The index to add the value at.
   * @param value The value to be added.
   */
  public void insertElementAt(int i, int value) {
    if (size == ints.length) {
      reallocate(ints.length * 2);
    }

    System.arraycopy(ints, i, ints, i + 1, size - i);
    ints[i] = value;
    ++size;
  }
  /** Removes the element at the specified index.
   * @param i The index of the element to be removed from this vector.
   */
  public int removeElementAt(int i) {
    int togo = ints[i];
    System.arraycopy(ints, i + 1, ints, i, size - (i + 1));
    --size;
    return togo;
  }
  /** Removes and returns the final element of the vector.
   * @return The value of the final element of the vector, which was removed.
   */
  public int popElement() {
    return ints[--size];
  }

  /** Returns the final element of the vector.
   * @return The value of the final element of the vector.
   */

  public int peek() {
    return ints[size - 1];
  }

  /** Sets this vector to zero size, effectively removing all its elements.
   */
  public void clear() {
    size = 0;
  }

  /** Determines whether this vector is empty.
   * @return <code>true</code> if this vector is empty.
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /** Searches for the supplied value within this vector.
   * @param tofind The value to be searched for.
   * @return The first index at which this value occurs, or -1 if it does not appear.
   */
  public int findInt(int tofind) {
    for (int i = 0; i < size; ++i) {
      if (ints[i] == tofind)
        return i;
    }
    return -1;
  }
  /** Assigns to this intVector the contents of another, overwriting our contents.
   * @param other The intVector to assign to us.
   */

  public void assign(intVector other) {
    ints = new int[other.ints.length];
    System.arraycopy(other.ints, 0, ints, 0, ints.length);
    size = other.size;
  }

  /** Returns the contents of this intVector as an array of ints.
     * @return A int array with the contents of this intVector.
     */
  public int[] asArray() {
    int[] togo = new int[size];
    System.arraycopy(ints, 0, togo, 0, size);
    return togo;
  }

  public intVector copy() {
    intVector togo = new intVector(ints.length);
    System.arraycopy(ints, 0, togo.ints, 0, ints.length);
    togo.size = size;
    return togo;
  }

  /** Renders this intVector as a String for debugging purposes.
   * @return the contents of this intVector as a debug string.
   */

  public String toString() {
    StringBuffer togo = new StringBuffer();
    for (int i = 0; i < size - 1; ++i) {
      togo.append(ints[i]).append(' ');
    }
    if (size > 0) {
      togo.append(ints[size - 1]);
    }
    return togo.toString();
  }

}
