/*
 * Created on 6 Aug 2006
 */
package uk.org.ponder.beanutil;

import java.util.ArrayList;

import uk.org.ponder.stringutil.StringList;

/**
 * A BeanLocator which accepts a set of "Fallback Locators", which are request
 * beans of type {@link FallbackBeanLocator}, whose contained beans will be
 * exported up to the root level - such paths are automatically permitted.
 * Fallback locators are only initialised on the first query to this locator
 * (probably via an EL expression) in order to minimise the possibility
 * construct-time cycles.
 */

public class FallbackCapableBeanLocator implements WriteableBeanLocator {

  private WriteableBeanLocator wbl;
  private StringList fallbacks;
  private ArrayList fallbackbeans;

  public void setBeanLocator(WriteableBeanLocator wbl) {
    this.wbl = wbl;
  }

  public void setFallbackBeans(StringList fallbacks) {
    this.fallbacks = fallbacks;
  }

  public Object locateBean(String path) {
    Object fetch = wbl.locateBean(path);
    if (fetch != null) {
      return fetch;
    }
    else {
      if (fallbackbeans == null) {
        fetchFallbacks();
      }
      Object togo = tryLocateFallback(path);
      return togo;
    }
  }

  private Object tryLocateFallback(String path) {
    for (int i = 0; i < fallbackbeans.size(); ++i) {
      FallbackBeanLocator fbl = (FallbackBeanLocator) fallbackbeans.get(i);
      Object togo = fbl.locateBean(path);
      if (togo != null)
        return togo;
    }
    return null;
  }

  private void fetchFallbacks() {
    fallbackbeans = new ArrayList();
    for (int i = 0; i < fallbacks.size(); ++i) {
      String fallback = fallbacks.stringAt(i);
      FallbackBeanLocator fbl = (FallbackBeanLocator) wbl.locateBean(fallback);
      fallbackbeans.add(fbl);
    }
  }

  public boolean remove(String beanname) {
    return wbl.remove(beanname);
  }

  public void set(String beanname, Object toset) {
    wbl.set(beanname, toset);
  }

}
