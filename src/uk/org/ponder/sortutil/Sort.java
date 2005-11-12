/*
 * Created on Dec 8, 2004
 */
package uk.org.ponder.sortutil;


/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class Sort {
  public static void quicksort(SortFacade facade) {
    quicksort(facade, 0, facade.size() - 1);
  }
  // simple quicksort by Sedgewick that behaves badly (very slightly)
  // with equal keys.
  public static void quicksort(SortFacade facade, int l, int r) { 
    int i = l-1, j = r; 
    if (r <= l) return;
    while (true) {
      while (facade.compare(++i, r) < 0) {}
      while (facade.compare(r, --j) < 0) if (j == l) break;
      if (i >= j) break;
      facade.swap(i, j);
    }
    facade.swap(i, r);
    quicksort(facade, l, i-1);
    quicksort(facade, i+1, r);
  }
  
}