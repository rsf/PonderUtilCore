/*
 * Created on Dec 4, 2006
 */
package uk.org.ponder.util;

import java.util.Collection;
import java.util.Set;

public abstract class ObstinateSet implements Set {

  public boolean add(Object o) {
    throw new UniversalRuntimeException("Cannot add value in " + getClass() + " set");  
  }

  public boolean addAll(Collection c) {
    throw new UniversalRuntimeException("Cannot modify " + getClass() + " set");     
  }

  public void clear() {
    throw new UniversalRuntimeException("Cannot modify " + getClass() + " set");     
  }

  public boolean containsAll(Collection c) {
    throw new UniversalRuntimeException("Cannot containsAll " + getClass() + " set");     
  }

  public boolean isEmpty() {
    return false;
  }

  public boolean remove(Object o) {
    throw new UniversalRuntimeException("Cannot modify " + getClass() + " set");     
  }

  public boolean removeAll(Collection c) {
    throw new UniversalRuntimeException("Cannot modify " + getClass() + " set");      
  }

  public boolean retainAll(Collection c) {
    throw new UniversalRuntimeException("Cannot modify " + getClass() + " set");     
  }

  public int size() {
    throw new UniversalRuntimeException("Cannot compute size of " + getClass() + " set");
  }

  public Object[] toArray() {
    throw new UniversalRuntimeException("Cannot convert " + getClass() + " set");
  }

  public Object[] toArray(Object[] a) {
    throw new UniversalRuntimeException("Cannot convert " + getClass() + " set");
  }

}
