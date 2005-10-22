/*
 * Created on Nov 22, 2004
 */
package uk.org.ponder.beanutil;

/**
 * A minimal interface to a bean container, that does not know how to 
 * interpret paths, i.e. container considered as raw HashTable. This is
 * particularly useful when we want to impose some kind of access control
 * censoring the root path taken to a derived bean.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface BeanLocator {
  public Object locateBean(String path);
}
