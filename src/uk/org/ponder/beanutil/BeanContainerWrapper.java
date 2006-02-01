/*
 * Created on Aug 3, 2005
 */
package uk.org.ponder.beanutil;

import java.util.ArrayList;
import java.util.List;

import uk.org.ponder.stringutil.StringList;
import uk.org.ponder.stringutil.StringSet;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * A bean locator that is used to "censor" access to beans based on their 
 * entries in a list of "permitted roots". In addition, it accepts a set of
 * "Fallback Locators", which are request beans of type FallbackBeanLocator,
 * whose contained beans will be exported up to the root level - such paths
 * are automatically permitted. Fallback locators are only initialised on the
 * first query to this locator (probably via an EL expression) in order to
 * minimise the possibility construct-time cycles.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
// Unlikely that Writeable functionality will actually be used.
public class BeanContainerWrapper implements WriteableBeanLocator {
  private WriteableBeanLocator factory;
  private StringSet permittedroots = new StringSet();
  private StringList fallbacks;
  private List fallbackbeans = null;
  
  public void setBeanLocator(WriteableBeanLocator beangetter) {
    factory = beangetter;
  }
  
  public void setPermittedBeanRoots(StringList permittedroots) {
    this.permittedroots.addAll(permittedroots);
    //this.permittedroots = permittedroots.toStringArray();
  }

  public void setFallbackBeans(StringList fallbacks) {
    this.fallbacks = fallbacks;
  }
  
  private boolean isPermitted(String rootpath) {
    return permittedroots != null && 
        permittedroots.contains(rootpath);
  }
  
  public Object locateBean(String path) {
    if (isPermitted(path)) {
      return factory.locateBean(path);
    }
    else {
      if (fallbackbeans == null) {
        fetchFallbacks();
      }
      Object togo = tryLocateFallback(path);
      if (togo == null) {
        reportImpermissiblePath(path);
      }
      return togo;
    }
  }
  
  private Object tryLocateFallback(String path) {
    for (int i = 0; i < fallbackbeans.size(); ++ i) {
      FallbackBeanLocator fbl = (FallbackBeanLocator) fallbackbeans.get(i);
      Object togo = fbl.locateBean(path);
      if (togo != null) return togo;
    }
    return null;
  }
  
  private void fetchFallbacks() {
    fallbackbeans = new ArrayList();
    for (int i = 0; i < fallbacks.size(); ++ i) {
      String fallback = fallbacks.stringAt(i);
      FallbackBeanLocator fbl = (FallbackBeanLocator) factory.locateBean(fallback);
      fallbackbeans.add(fbl);
    }
  }
  private static void reportImpermissiblePath(String rootpath) {
    throw UniversalRuntimeException.accumulate(new SecurityException(), 
        "Impermissible bean path " + rootpath);
  }

  private void checkRootPath(String rootpath) {
   if (!isPermitted(rootpath)) {
       reportImpermissiblePath(rootpath);
    }
  }
  
  public boolean remove(String beanname) {
    checkRootPath(beanname);
    return factory.remove(beanname);
  }
  
  public void set(String beanname, Object toset) {
    checkRootPath(beanname);
    factory.set(beanname, toset);
  }

}
