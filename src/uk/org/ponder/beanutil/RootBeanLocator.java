/*
 * Created on Nov 22, 2004
 */
package uk.org.ponder.mapping;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public interface RootBeanLocator {
  public Object locateRootBean(String path);
  public String stripPath(String valuebinding);
}
