/*
 * Created on Dec 7, 2004
 */
package uk.org.ponder.sortutil;

import java.util.Comparator;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class DefaultComparator implements Comparator {
  public static final DefaultComparator instance = new DefaultComparator();
  public int compare(Object o1, Object o2) {
    Comparable co1 = (Comparable) o1;
    Comparable co2 = (Comparable) o2;
    return co1.compareTo(co2);
  }

}
