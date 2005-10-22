/*
 * Created on Mar 14, 2005
 */
package uk.org.ponder.beanutil;

/**
 * An interface to a full bean container - this is distinguished from the
 * @see BeanLocator interface since it is capable to navigate a full dotted
 * bean path by evaluating properties.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface BeanGetter {
  public Object getBean(String beanpath);
}
