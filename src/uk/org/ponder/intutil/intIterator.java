/*
 * Created on 15-Aug-2003
 */
package uk.org.ponder.doubleutil;

/**
 * @author Bosmon
 *
 * The class intIterator represents an iteration through a sequence of native
 * integers. 
 */
// This technique is deprecated by Kevlin, but we use it anyway.
public interface doubleIterator extends constDoubleIterator {
   public void setDouble(double toset);
}