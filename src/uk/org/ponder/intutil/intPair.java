/*
 * Created on 15-Aug-2003
 */
package uk.org.ponder.intutil;

import java.util.Comparator;

/**
 * @author Bosmon
 *
 * The class intPair represents a pair of integers. 
 */
public class intPair {
  public int first;
  public int second;
  public static Comparator compare_second = new Comparator() {
    public int compare(Object o1, Object o2) {
     return ((intPair)o1).second - ((intPair)o2).second;
    }};
    
  public intPair(int first, int second) {
    this.first = first;
    this.second = second;
  }
  public void sortAscending() {
    if (first > second) {
      int temp = first; first = second; second = temp;
    }
  }
}
