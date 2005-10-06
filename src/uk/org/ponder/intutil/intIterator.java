/*
 * Created on 15-Aug-2003
 */
package uk.org.ponder.intutil;

/**
 * @author Bosmon
 *
 * The class intIterator represents an iteration through a sequence of native
 * integers. 
 */
// This technique is deprecated by Kevlin, but we use it anyway.
public interface intIterator extends constIntIterator {
   public void setInt(int toset);
}
