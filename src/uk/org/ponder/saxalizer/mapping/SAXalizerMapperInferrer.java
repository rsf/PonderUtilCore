/*
 * Created on Sep 22, 2004
 */
package uk.org.ponder.saxalizer.mapping;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface SAXalizerMapperInferrer {
  public SAXalizerMapperEntry inferEntry(Class clazz);
  public void setDefaultInferrible(Class clazz);
  public boolean isDefaultInferrible(Class clazz);
  public void addCollectionType(Class collectiontype, Class containeetype);
  public Class getContaineeType(Class collectiontype);
}
