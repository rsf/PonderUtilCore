/*
 * Created on Dec 7, 2004
 */
package uk.org.ponder.sortutil;

import java.util.Comparator;

import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class ObjArraySortFacade implements SortFacade {
  private Object[] array;
  private Comparator comparator;
  private Object temp = null;
  public ObjArraySortFacade(Object[] array, Comparator comparator) {
    this.array = array;
    this.comparator = comparator == null? DefaultComparator.instance : comparator;
  }
  public int size() {
    return array.length;
  }

  public void move(int source, int dest) {
    array[dest] = array[source];
  }

  public void storeTemp(int source) {
    if (temp != null) {
      throw new UniversalRuntimeException("Temporary object already used in storeTemp!");
    }
    temp = array[source];
  }

  public void releaseTemp(int dest) {
    if (temp == null) {
      throw new UniversalRuntimeException("Temporary object not set in releaseTemp ");
    }
    array[dest] = temp;
  }

  public int compare(int index1, int index2) {
    return comparator.compare(array[index1], array[index2]);
  }
  public void swap(int i, int j) {
    Object temp = array[i];
    array[i] = array[j];
    array[j] = temp;
  }
  
}
