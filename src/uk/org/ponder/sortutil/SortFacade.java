/*
 * Created on Dec 7, 2004
 */
package uk.org.ponder.sortutil;

/**
 * This interface abstracts all the operations a sorting algorithm
 * may reasonably want to perform.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface SortFacade {
  public int size();
  public void swap(int i, int j);
  public void move(int source, int dest);
  public void storeTemp(int source);
  public void releaseTemp(int dest);
  public int compare(int index1, int index2);
}
