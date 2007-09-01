/*
 * Created on Nov 16, 2005
 */
package uk.org.ponder.beanutil.support;

import java.util.HashMap;
import java.util.Map;

import uk.org.ponder.beanutil.WriteableBeanLocator;

/** A concrete manifestation of a WriteableBeanLocator, backed by a standard
 * {@link Map}.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 */

public class ConcreteWBL implements WriteableBeanLocator {
  private Map beans;
  
  public ConcreteWBL() {
    beans = new HashMap();
  }
  public ConcreteWBL(Map beans) {
    this.beans = beans;
  }
  public boolean remove(String beanname) {
    return beans.remove(beanname) != null;
  }

  public void set(String beanname, Object toset) {
    beans.put(beanname, toset);
  }

  public Object locateBean(String path) {
    return beans.get(path);
  }
  
  public void clear() {
    beans.clear();
  }
}
