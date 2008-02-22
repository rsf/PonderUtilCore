/*
 * Created on Sep 20, 2004
 */
package uk.org.ponder.saxalizer.mapping;

import java.io.InputStream;

import uk.org.ponder.conversion.SerializationProvider;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface MappableXMLProvider extends SerializationProvider {
  public void loadMapping(InputStream is);
  public void registerClass(String classname, Class resourceclass);
}
