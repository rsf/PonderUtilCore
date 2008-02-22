/*
 * Created on Oct 15, 2004
 */
package uk.org.ponder.conversion;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * An interface specifying conversion of Java objects into a flat textual
 * representation (e.g. XML or JSON) and back again.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 */

public interface SerializationProvider {
  public void writeObject(Object towrite, OutputStream os);
  public Object readObject(Object classorobject, InputStream is);
  
  public String toString(Object towrite);
  public String toString(Object towrite, boolean compact);
  public Object fromString(String toread);
}