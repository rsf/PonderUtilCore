/*
 * Created on Oct 4, 2004
 */
package uk.org.ponder.util;

/**
 * The equivalent of a C++ OutputIterator - delivers objects into a collection.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface Denumeration {
  public void add(Object o);
  public boolean remove(Object o);
}
