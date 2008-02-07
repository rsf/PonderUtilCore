package uk.org.ponder.iterationutil;

import java.util.Enumeration;

/** Converts a single object into an enumeration that dispenses just that object.
 */

public class SingleEnumeration implements Enumeration {
  private Object element;
  public boolean hasMoreElements() {
    return element != null;
    }
  public Object nextElement() {
    Object togo = element;
    element = null;
    return togo;
    }
  /** Constructs an Enumeration that will enumerate over just the object supplied.
   * @param element The object to be enumerated over.
   */
  public SingleEnumeration(Object element) {
    this.element = element;
    }
  }
