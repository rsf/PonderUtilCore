package uk.org.ponder.doubleutil;

/** A simple container class representing a vector of native integers.
 */

public class doubleVector {
  private double[] doubles;
  private int size;

  public class doubleVectorIterator implements doubleIterator {
    int index = 0;
    public boolean hasNextDouble() {
      return index != size;
    }
    public void next() {
      ++index;
    }
    public void setDouble(double toset) {
      doubles[index] = toset;
    }
    public double getDouble() {
      return doubles[index];
    }
  }

  public doubleIterator beginIterator() {
    return new doubleVectorIterator();
  }

  /** Constructs an intVector with the specified initial capacity.
   * @param initalcapacity The required initial capacity.
   */
  public doubleVector(int initialcapacity) {
    doubles = new double[initialcapacity];
    size = 0;
  }
  public void ensureIndex(int i) {
    if (i >= size) {
      setSize(i + 1);
    }
  }

  /** Returns the integer at the specified index.
   * @param i The index of the required integer.
   * @return The integer at index <code>i</code>
   */
  public double doubleAt(int i) {
    return doubles[i];
  }

  public double doubleAtSafe(int i) {
    ensureIndex(i);
    return doubleAt(i);
  }
  /** Assigns a value to a member of the vector.
   * @param i the index to assign a value at.
   * @param value the value to assign.
   */

  public void setDoubleAt(int i, double value) {
    doubles[i] = value;
  }

  public void setDoubleAtSafe(int i, double value) {
    ensureIndex(i);
    setDoubleAt(i, value);
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
    if (newsize > doubles.length)
      reallocate(newsize + doubles.length);
    size = newsize;
  }

  private void reallocate(int newsize) {
    double[] newdoubles = new double[newsize];
    System.arraycopy(doubles, 0, newdoubles, 0, size);
    doubles = newdoubles;
  }
  /** Appends a new element to the end of this vector, reallocating its storage space if necessary.
   * @param i The integer value to be appended to the vector.
   */
  public void addElement(double i) {
    if (size == doubles.length) {
      reallocate(doubles.length * 2);
    }
    doubles[size] = i;
    ++size;
  }

  /** Removes the element at the specified index.
   * @param i The index of the element to be removed from this vector.
   */
  public void removeElementAt(int i) {
    System.arraycopy(doubles, i + 1, doubles, i, size - (i + 1));
    --size;
  }
  /** Removes and returns the final element of the vector.
   * @return The value of the final element of the vector, which was removed.
   */
  public double popElement() {
    return doubles[--size];
  }

  /** Returns the final element of the vector.
   * @return The value of the final element of the vector.
   */

  public double peek() {
    return doubles[size - 1];
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

  /** Assigns to this intVector the contents of another, overwriting our contents.
   * @param other The intVector to assign to us.
   */

  public void assign(doubleVector other) {
    doubles = new double[other.doubles.length];
    System.arraycopy(other.doubles, 0, doubles, 0, doubles.length);
    size = other.size;
  }

  /** Renders this intVector as a String for debugging purposes.
   * @return the contents of this intVector as a debug string.
   */

  public String toString() {
    StringBuffer togo = new StringBuffer();
    for (int i = 0; i < size - 1; ++i) {
      togo.append(doubles[i]).append(' ');
    }
    togo.append(doubles[size - 1]);
    return togo.toString();
  }
  /** Returns the contents of this doubleVector as an array of doubles.
   * @return A double array with the contents of this doubleVector.
   */
  public double[] asArray() {
    double[] togo = new double[size];
    System.arraycopy(doubles, 0, togo, 0, size);
    return togo;
  }
}
