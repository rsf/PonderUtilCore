/*
 * Created on 6 Aug 2006
 */
package uk.org.ponder.beanutil;

import uk.org.ponder.stringutil.StringList;
import uk.org.ponder.stringutil.StringSet;
import uk.org.ponder.util.UniversalRuntimeException;

public class SecuredBeanLocator implements WriteableBeanLocator {

  private StringSet permittedroots = new StringSet();
  private WriteableBeanLocator wbl;

  public void setPermittedBeanRoots(StringList permittedroots) {
    this.permittedroots.addAll(permittedroots);
    //this.permittedroots = permittedroots.toStringArray();
  }
  
  private boolean isPermitted(String rootpath) {
    return permittedroots != null && 
        permittedroots.contains(rootpath);
  }

  public void setBeanLocator(WriteableBeanLocator wbl) {
    this.wbl = wbl;
  }
  
  private static void reportImpermissiblePath(String rootpath) {
    throw UniversalRuntimeException.accumulate(new SecurityException(), 
        "Impermissible bean path " + rootpath);
  }
  
  public Object locateBean(String path) {
    if (isPermitted(path)) {
      return wbl.locateBean(path);
    }
    else {
      reportImpermissiblePath(path);
      return null;
    }
  }
  
  private void checkRootPath(String rootpath) {
   if (!isPermitted(rootpath)) {
       reportImpermissiblePath(rootpath);
    }
  }
  
  public boolean remove(String beanname) {
    checkRootPath(beanname);
    return wbl.remove(beanname);
  }
  
  public void set(String beanname, Object toset) {
    checkRootPath(beanname);
    wbl.set(beanname, toset);
  }
  
}
