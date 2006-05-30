/*
 * Created on Dec 23, 2004
 */
package uk.org.ponder.saxalizer.mapping;

import uk.org.ponder.saxalizer.SAXalizerMappingContext;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface MappingLoader extends ContainerTypeRegistrar {
  public void loadExtendedMappings(SAXalizerMappingContext context);
  public void loadStandardMappings(MappableXMLProvider xmlprovider);
}