/*
 * Created on Sep 22, 2004
 */
package uk.org.ponder.saxalizer.mapping;

import java.util.HashMap;

/**
 * Holds details of dynamic mappings between Java classes and
 * XML representations. If no entry has been supplied, and a
 * non-null <code>SAXalizerMapperInferrer</code> has been 
 * provided, it will be asked to synthesize a "default" mapping
 * based on reflection of the class.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 */
public class SAXalizerMapper {
  private SAXalizerMapperInferrer inferrer = new DefaultMapperInferrer();
  // This is a hashmap of java classes to SAXalizerMapperEntries.
  private HashMap handlermap = new HashMap();
  public void addEntry(SAXalizerMapperEntry entry) {
    handlermap.put(entry.targetclass, entry);
  }
  public SAXalizerMapperEntry byClass(Class clazz) {
    SAXalizerMapperEntry togo = (SAXalizerMapperEntry)handlermap.get(clazz);
    if (togo == null && inferrer != null) {
      togo = inferrer.inferEntry(clazz);
      addEntry(togo);
    }
    return togo;
  }
  public void setMapperEntryInferrer(SAXalizerMapperInferrer inferrer) {
    this.inferrer = inferrer;
  }
}
