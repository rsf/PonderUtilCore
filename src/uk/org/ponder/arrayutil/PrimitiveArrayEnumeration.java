/*
 * Created on 29 Feb 2008
 */
package uk.org.ponder.arrayutil;

import java.lang.reflect.Array;
import java.util.Enumeration;

/** Constructs an enumeration which will enumerate elements of a primitive array type.
 * Current implementation is reflective and so the concrete implementation is not 
 * exposed, to allow a future time-optimised implementation if required.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *
 */

public class PrimitiveArrayEnumeration {
  public static Enumeration get(final Object array) {
    if (!array.getClass().isArray() && array.getClass().getComponentType().isPrimitive()) {
      throw new IllegalArgumentException(
          "PrimitiveArrayEnumeration can only enumerate over arrays of primitive component type, but was supplised an object of " + array.getClass());
    }
    
    return new Enumeration() {
      private int index = 0;
      private int limit = Array.getLength(array);
      
      public boolean hasMoreElements() {
        return index < limit;
      }

      public Object nextElement() {
        return Array.get(array, index++);
      }
      
    };
    
  }
}
