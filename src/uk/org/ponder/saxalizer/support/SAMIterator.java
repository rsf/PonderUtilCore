/*
 * Created on Dec 14, 2004
 */
package uk.org.ponder.saxalizer.support;


/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface SAMIterator {
  public boolean valid();
  public void next();
  public SAXAccessMethod get();
}
