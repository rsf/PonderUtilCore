/*
 * Created on 15-Aug-2003
 */
package uk.org.ponder.intutil;

/**
 * @author Bosmon
 *
 * The class intPair represents a pair of integers. 
 */
public class intPair {
  public int first;
  public int second;
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
