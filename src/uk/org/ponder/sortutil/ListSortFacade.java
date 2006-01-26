/*
 * Created on Dec 7, 2004
 */
package uk.org.ponder.sortutil;

import java.util.Comparator;
import java.util.List;

import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class ListSortFacade implements SortFacade {
  private List list;
  private Comparator comparator;
  private Object temp = null;
  public ListSortFacade(List list, Comparator comparator) {
    this.list = list;
    this.comparator = comparator == null? DefaultComparator.instance : comparator;
  }
  public int size() {
    return list.size();
  }

  public void move(int source, int dest) {
    list.set(dest, list.get(source));
  }

  public void storeTemp(int source) {
    if (temp != null) {
      throw new UniversalRuntimeException("Temporary object already used in storeTemp!");
    }
    temp = list.get(source);
  }

  public void releaseTemp(int dest) {
    if (temp == null) {
      throw new UniversalRuntimeException("Temporary object not set in releaseTemp ");
    }
    list.set(dest, temp);
  }

  public int compare(int index1, int index2) {
    return comparator.compare(list.get(index1), list.get(index2));
  }
  public void swap(int i, int j) {
    Object temp = list.get(i);
    list.set(i, list.get(j));
    list.set(j, temp);
  }
  
}
