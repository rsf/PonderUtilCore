/*
 * Created on Sep 22, 2004
 */
package uk.org.ponder.saxalizer.mapping;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface SAXalizerMapperInferrer {
  SAXalizerMapperEntry inferEntry(Class clazz);
}
