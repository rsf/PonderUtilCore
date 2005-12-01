/*
 * Created on Nov 16, 2005
 */
package uk.org.ponder.beanutil;

import java.util.HashMap;

public class ConcreteWBL implements WriteableBeanLocator {
  private HashMap beans = new HashMap();
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
