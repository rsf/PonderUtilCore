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
public class TwinListSortFacade implements SortFacade {
  private List main;
  private List slave;
  private Comparator comparator;
  private Object tempmain;
  private Object tempslave;
  public TwinListSortFacade(List main, List slave, Comparator comparator) {
    this.main = main;
    this.slave = slave;
    if (main.size() != slave.size()) {
      throw new UniversalRuntimeException("Lists of unequal size supplied to TwinListSortFacade");
    }
    this.comparator = comparator;
  }
  
  public int size() {
    return main.size();
  }

  public void move(int source, int dest) {
    main.set(dest, main.get(source));
  }

  public void storeTemp(int source) {
    if (tempmain != null) {
      throw new UniversalRuntimeException("Temporary object already used in storeTemp!");
    }
    tempmain = main.get(source);
    tempslave = slave.get(source);
  }

  public void releaseTemp(int dest) {
    if (tempmain == null) {
      throw new UniversalRuntimeException("Temporary object not set in releaseTemp ");
    }
    main.set(dest, tempmain);
    slave.set(dest, tempslave);
  }

  public int compare(int index1, int index2) {
    return comparator.compare(main.get(index1), main.get(index2));
  }

  public void swap(int i, int j) {
    Object temp = main.get(i);
    main.set(i, main.get(j));
    main.set(j, temp);
    temp = slave.get(i);
    slave.set(i, slave.get(j));
    slave.set(j, temp);
  }
}
