package uk.org.ponder.arrayutil;

import java.lang.reflect.Array;

import uk.org.ponder.reflect.ClassGetter;

/** This class supplies a selection of useful methods to operate on Java arrays. With
 * these methods, arrays can behave very much like Vectors, only with much greater
 * time and space efficiency where the length is short or modifications are infrequent.
 */

public class ArrayUtil {
  // TODO: Does the [L syntax also work for primitive types?
  private static double[] doubleexemplar = new double[0];
  public static Class doubleArrayClass = doubleexemplar.getClass();
  private static int[] intexemplar = new int[0];
  public static Class intArrayClass = intexemplar.getClass();
  public static Class stringArrayClass = ClassGetter.forName("[Ljava.lang.String;");
  
  /** Concatenates two arrays of the same reference type to return a larger array.
   * @param array1 The first array to be concatenated.
   * @param array2 The second array to be concatenated.
   * @return An array whose length is the sum of the lengths of the input arrays, and
   * contains the elements of the second array appended to the elements of the first.
   */
  public static final Object[] concat (Object[] array1, Object[] array2) {
    Class component1 = array1.getClass().getComponentType();
    Object[] togo = (Object[])Array.newInstance(component1, array1.length + array2.length);
    System.arraycopy(array1, 0, togo, 0, array1.length);
    System.arraycopy(array2, 0, togo, array1.length, array2.length);
    return togo;
    }
  
  /** Expands the supplied array by the given factor, to return an array with the
   * same contents padded by null.
   * @param array1 The array to be expanded.
   * @param scalefactor The factor the size of the array is to be enlarged by - clearly
   * this must be a number greater than 1.0.
   * @return An array with the required size, with the contents of the supplied array
   * copied into its first portion.
   */

  public static final Object[] expand (Object[] array1, double scalefactor) {
    Class component1 = array1.getClass().getComponentType();
    Object[] togo = (Object[])Array.newInstance(component1, (int)(array1.length * scalefactor));
    System.arraycopy(array1, 0, togo, 0, array1.length);
    return togo;
    }

  /** Expands the supplied array to the specified size, to return an array with the
   * same contents padded by null.
   * @param array1 The array to be expanded.
   * @param newsize The required size of the array.
   * @return An array with the required size, with the contents of the supplied array
   * copied into its first portion.
   */

  public static final Object[] expand (Object[] array1, int newsize) {
    Class component1 = array1.getClass().getComponentType();
    Object[] togo = (Object[])Array.newInstance(component1, newsize);
    System.arraycopy(array1, 0, togo, 0, array1.length);
    return togo;
    }

  
  /** Expands the supplied array by the given factor, to return an array with the
   * same contents padded by null.
   * @param array1 The array to be expanded.
   * @param scalefactor The factor the size of the array is to be enlarged by - clearly
   * this must be a number greater than 1.0.
   * @return An array with the required size, with the contents of the supplied array
   * copied into its first portion.
   */

  public static final byte[] expand (byte[] array1, double scalefactor) {
    byte[] togo = new byte[(int)(array1.length * scalefactor)];
    System.arraycopy(array1, 0, togo, 0, array1.length);
    return togo;
    }

  /** Trims the supplied array to the specified size, to return an array with
   * only the elements from the first portion of the supplied array.
   * @param array1 The array to be trimmed.
   * @param tolength The number of elements to be returned from the beginning of the
   * array.
   * @return An array containing only the first <code>tolength</code> elements of the
   * supplied array.
   */

  public static final Object[] trim(Object[] array1, int tolength) {
    Class component1 = array1.getClass().getComponentType();
    Object[] togo = (Object[])Array.newInstance(component1, tolength);
    System.arraycopy(array1, 0, togo, 0, tolength);
    return togo;
    }

  /** Appends a single object to the end of the supplied array, returning an array
   * one element longer than the supplied array.
   * @param array1 The array to which the element is to be appended, or <code>null</code>
   * if no array yet exists.
   * @param toappend The object to be appended to the array.
   * @return A new array one element longer than the supplied array, with the supplied
   * object copied into the final place.
   */

  public static final Object[] append(Object[] array1, Object toappend) {
    Class component1 = array1 == null? toappend.getClass() : array1.getClass().getComponentType();
    int length = array1 == null? 0 : array1.length;
    Object[] togo = (Object[])Array.newInstance(component1, length + 1);
    if (length > 0) {
      System.arraycopy(array1, 0, togo, 0, array1.length);
    }
    togo[length] = toappend;
    return togo;
    }

  /** Determines whether the supplied array contains a given element. Equality
   * will be determined by the <code>.equals()</code> method.
   * @param array The array to be searched for the supplied element.
   * @param tofind The element to be found in the array.
   * @return <code>true</code> if the element was found in the array.
   */

  public static final boolean contains(Object[] array, Object tofind) {
    for (int i = 0; i < array.length; ++ i) {
      if (array[i].equals(tofind)) 
	return true;
      }
    return false;
    }

  /** Finds the first index that the supplied element appears at in a given array.
   * Equality will be determined by the <code>.equals()</code> method.
   * @param array The array to be searched for the supplied element.
   * @param tofind The object to be searched for.
   * @return The first index that the object appears at in the array, or <code>-1</code> if 
   * it is not found.
   */

  public static final int indexOf(Object[] array, Object tofind) {
    for (int i = 0; i < array.length; ++ i) {
      if (array[i].equals(tofind))
	return i;
      }
    return -1;
    }


  /** Finds the first index that the supplied element appears at in a given array.
   * Equality will be determined by the <code>.equals()</code> method.
   * @param array The array to be searched for the supplied element.
   * @param tofind The object to be searched for.
   * @return The first index that the object appears at in the array, or <code>-1</code> if 
   * it is not found.
   */

  public static final int indexOf(int[] array, int tofind) {
    for (int i = 0; i < array.length; ++ i) {
      if (array[i] == tofind)
  return i;
      }
    return -1;
    }


  /** Removes an element from an array, by causing all the elements beyond it to 
   * shuffle back one place.
   * @param array The array from which the element is to be removed.
   * @param index The index from which the element is to be removed.
   * @return The same array that was input, with the elements past the specified
   * index shuffled back one place.
   */

  public static final void removeElementAtShift(Object[] array, int index) {
    System.arraycopy(array, index + 1, array, index, array.length - index - 1);
    }

  
  public static final Object[] removeElementAt(Object[] array, int index) {
    Class component1 = array.getClass().getComponentType();
    Object[] togo = (Object[])Array.newInstance(component1, array.length - 1);
    
    for (int i = 0; i < index; ++ i) {
      togo[i] = array[i];
    }
    for (int i = index + 1; i < array.length; ++ i) {
      togo[i - 1] = array[i];
    }
    
    return togo;
  }
  
  public static int lexicalCompare(Comparable[] array1, int length1, Comparable[] array2, int length2) {
    int i = 0;
    while (true) {
      if (i >= length1) { 
        // off the end of 1 means that 1 is possibly shorter
        if (length1 == length2) return 0;
        else return -1;
      }
      if (i >= length2) return 1;
      Comparable a1 = array1[i];
      Comparable a2 = array2[i];
      if (a1.compareTo(a2) < 0) return -1;
      else if (a1.compareTo(a2) > 0) return 1;
      ++i;
    }
  }
  
  public static boolean equals(String string, char[] buffer, int start, int length) {
    if (length != string.length()) return false;
    for (int i = length - 1; i >= 0; -- i) {
      if (buffer[start + i] != string.charAt(i)) return false;
    }
    return true;
  }
  
  /** Converts the supplied array into a String for debugging purposes.
   * @return A String formed from the results of the <code>.toString()</code> method
   * on each array element, separated by single space characters. If the supplied
   * array is null, returns the string <code>null</code>.
   */

  public static final String toString(Object[] array) {
    if (array == null) return "null";
    StringBuffer togo = new StringBuffer();
    for (int i = 0; i < array.length; ++ i) {
      togo.append(array[i].toString());
      if (i != array.length - 1) togo.append(' ');
      }
    return togo.toString();
    }

  }
