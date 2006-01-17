/*
 * Created on Nov 8, 2004
 */
package uk.org.ponder.stringutil;

import java.util.HashSet;

import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class StringSet extends HashSet {
  public void addAll(String[] array) {
    for (int i = 0; i < array.length; ++ i) {
      add(array[i]);
    }
  }
  public boolean add(Object o) {
    if (!(o instanceof String)) {
      throw new UniversalRuntimeException("Object " + o + " of " + o.getClass()
          + " added to StringSet");
    }
    return super.add(o);
  }
}
