/*
 * Created on Oct 15, 2004
 */
package uk.org.ponder.saxalizer;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface XMLProvider {
  public void writeXML(Object towrite, OutputStream os);
  public Object readXML(Object classorobject, InputStream is);
}