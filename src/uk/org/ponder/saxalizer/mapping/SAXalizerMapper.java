/*
 * Created on Sep 22, 2004
 */
package uk.org.ponder.saxalizer.mapping;

import java.util.HashMap;

/**
 * Holds details of dynamic mappings between Java classes and
 * XML representations. 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 */
public class SAXalizerMapper {
  // This is a hashmap of java classes to SAXalizerMapperEntries.
  private HashMap handlermap = new HashMap();
  public void addEntry(SAXalizerMapperEntry entry) {
    handlermap.put(entry.targetclass, entry);
  }
  public SAXalizerMapperEntry byClass(Class clazz) {
    SAXalizerMapperEntry togo = (SAXalizerMapperEntry)handlermap.get(clazz);
    return togo;
  }
}
