/*
 * Created on Sep 20, 2004
 */
package uk.org.ponder.saxalizer.mapping;

import java.io.InputStream;

import uk.org.ponder.saxalizer.XMLProvider;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface MappableXMLProvider extends XMLProvider {
  public void loadMapping(InputStream is);
}
