package uk.org.ponder.arrayutil;

import java.util.Enumeration;

/** This utility class converts a Java array into an Enumeration.
 */

public class ArrayEnumeration implements Enumeration {
  private Object[] array;
  private int index;
  private int limit;

  /** Constructs an Enumeration from the specified array.
   * @param array The array to be converted into an enumeration.
   */

  public ArrayEnumeration(Object[] array) {
    this.array = array;
    this.index = 0;
    this.limit = array.length;
    }

  /** Constructs an Enumeration from a portion of the supplied array.
   * @param array The array of which a portion is to be made into an enumeration.
   * @param index The index of the first array element to be returned from the enumeration.
   * @param limit One more than the final array element to be returned from the enumeration.
   */

  public ArrayEnumeration(Object[] array, int index, int limit) {
    this.array = array;
    this.index = index;
    this.limit = limit;
    }

  public boolean hasMoreElements() {
    return index < limit;
    }

  public Object nextElement() {
    return array[index++];
    }
  }
