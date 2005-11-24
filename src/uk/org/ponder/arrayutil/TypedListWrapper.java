/*
 * Created on Nov 22, 2005
 */
package uk.org.ponder.arrayutil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/** In the absence of any concrete generics, this class makes it a bit more
 * pleasant to have a typesafe list. The primary value of this wrapper will
 * be realised in dynamic environments like Hibernate, which will want directed
 * to perform a "wrap-under" proxy of the contained List, while letting us
 * retain our concrete exterior with type information 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *
 */

public abstract class TypedListWrapper implements List {
  protected List wrapped = new ArrayList();
  /** Implementors will override this method, and in addition provide some
   * typesafe add and get methods of their choosing *.
   */
  public abstract Class getWrappedType();
  
  public void setWrappedList(List wrapped) {
    this.wrapped = wrapped;
  }
  public int size() {
    return wrapped.size();
  }
  public void clear() {
    wrapped.clear();
  }
  public boolean isEmpty() {
    return wrapped.isEmpty();
  }
  public Object[] toArray() {
    return wrapped.toArray();
  }
  public Object get(int index) {
    return wrapped.get(index);
  }
  public Object remove(int index) {
    return wrapped.remove(index);
  }
  public void add(int index, Object element) {
    wrapped.add(index, element);
  }
  public int indexOf(Object o) {
    return wrapped.indexOf(o);
  }
  public int lastIndexOf(Object o) {
    return wrapped.lastIndexOf(o);
  }
  public boolean add(Object o) {
    return wrapped.add(o); 
  }
  public boolean contains(Object o) {
    return wrapped.contains(o);
  }
  public boolean remove(Object o) {
    return wrapped.remove(o);
  }
  public boolean addAll(int index, Collection c) {
    return wrapped.addAll(index, c);
  }
  public boolean addAll(Collection c) {
    return wrapped.addAll(c);
  }
  public boolean containsAll(Collection c) {
    return wrapped.containsAll(c);
  }
  public boolean removeAll(Collection c) {
    return wrapped.removeAll(c);
  }
  public boolean retainAll(Collection c) {
    return wrapped.retainAll(c);
  }
  public Iterator iterator() {
    return wrapped.iterator();
  }
  public List subList(int fromIndex, int toIndex) {
    return wrapped.subList(fromIndex, toIndex);
  }
  public ListIterator listIterator() {
    return wrapped.listIterator();
  }
  public ListIterator listIterator(int index) {
    return wrapped.listIterator(index);
  }
  public Object set(int index, Object element) {
    return wrapped.set(index, element);
  }
  public Object[] toArray(Object[] a) {
    return wrapped.toArray(a);
  }
}
