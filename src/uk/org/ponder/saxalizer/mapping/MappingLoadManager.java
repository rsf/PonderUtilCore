/*
 * Created on Dec 23, 2004
 */
package uk.org.ponder.saxalizer.mapping;

import java.io.InputStream;
import java.util.List;

import uk.org.ponder.saxalizer.SAXalizerMappingContext;
import uk.org.ponder.saxalizer.XMLProvider;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class MappingLoadManager {
  private MappableXMLProvider xmlprovider;
  private SAXalizerMappingContext mappingcontext;

  public void setMappableXMLProvider(MappableXMLProvider xmlprovider) {
    this.xmlprovider = xmlprovider;
  }
  public void setSAXalizerMappingContext(SAXalizerMappingContext mappingcontext) {
    this.mappingcontext = mappingcontext;
  }
  
  
  public void setMappingLoaders(List mappingloaders) {
    for (int i = 0; i < mappingloaders.size(); ++ i) {
      MappingLoader mappingloader = (MappingLoader) mappingloaders.get(i);
      mappingloader.loadExtendedMappings(mappingcontext);
      mappingloader.loadStandardMappings(xmlprovider);
    }
  }
  
  public XMLProvider getXMLProvider() {
    return xmlprovider;
  }
   
  public static void loadClasspathMapping(MappableXMLProvider xmlprovider, String path) {
    try {
    InputStream is = xmlprovider.getClass().getClassLoader().getResourceAsStream(path);
    if (is == null) {
      throw new UniversalRuntimeException("Classpath resource for path "+ path + " not found");
    }
    xmlprovider.loadMapping(is);
    }
    catch (Throwable t) {
      throw UniversalRuntimeException.accumulate(t, "Failed to load mapping for "+ path);
    }
  }
}
