/*
 * Created on 16-Aug-2003
 */
package uk.org.ponder.intutil;

/**
 * @author Bosmon
 *
 * The class const IntIterator represents an iteration through a sequence
 * of unmodifiable native integers.
 */
public interface constIntIterator {
  /** Does the sequence contain another integer?*/
  public boolean valid();
  /** Steps the iterator along to the next element of the sequence */
  public void next();
  /** Returns the integer currently pointed at by this iterator */
  public int getInt();
}
