/*
 * Created on Aug 3, 2005
 */
package uk.org.ponder.beanutil;

import uk.org.ponder.stringutil.StringList;
import uk.org.ponder.stringutil.StringSet;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * A bean locator that is used to "censor" access to beans based on their 
 * entries in a list of "permitted roots".
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class BeanContainerWrapper implements WriteableBeanLocator {
  private WriteableBeanLocator factory;
  private StringSet permittedroots = new StringSet();
  
  public void setBeanLocator(WriteableBeanLocator beangetter) {
    factory = beangetter;
  }
  public void setPermittedBeanRoots(StringList permittedroots) {
    this.permittedroots.addAll(permittedroots);
    //this.permittedroots = permittedroots.toStringArray();
  }

  public Object locateBean(String path) {
    if (path.indexOf('.') != -1) {
      throw new UniversalRuntimeException("Root path of " + path
          + " is not valid");
    }
    checkRootPath(path);
    return factory.locateBean(path);
  }

  private void checkRootPath(String rootpath) {
    if (permittedroots == null || 
        !permittedroots.contains(rootpath)) {
      throw UniversalRuntimeException.accumulate(new SecurityException(), 
          "Impermissible bean path " + rootpath);
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
