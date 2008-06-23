/*
 * Created on Sep 22, 2004
 */
package uk.org.ponder.saxalizer.mapping;

/**
 * Interface to a family of strategies which automatically (generally via reflection)
 * serialization mappings given a Java Class.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface SAXalizerMapperInferrer {
  public void setChainedInferrer(SAXalizerMapperInferrer target);
  public SAXalizerMapperEntry inferEntry(Class clazz, SAXalizerMapperEntry entry);
  public void setDefaultInferrible(Class clazz);
  public boolean isDefaultInferrible(Class clazz);
}
