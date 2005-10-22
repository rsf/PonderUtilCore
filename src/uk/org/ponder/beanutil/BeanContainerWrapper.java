/*
 * Created on Aug 3, 2005
 */
package uk.org.ponder.beanutil;

import uk.org.ponder.arrayutil.ArrayUtil;
import uk.org.ponder.stringutil.StringList;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * A bean locator that is used to "censor" access to beans based on their 
 * entries in a list of "permitted roots".
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class BeanContainerWrapper implements BeanLocator {
  private BeanLocator factory;
  private String[] permittedroots;
  
  public void setBeanLocator(BeanLocator beangetter) {
    factory = beangetter;
  }
  public void setPermittedBeanRoots(StringList permittedroots) {
    this.permittedroots = permittedroots.toStringArray();
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
        ArrayUtil.indexOf(permittedroots, rootpath) == -1) {
      throw UniversalRuntimeException.accumulate(new SecurityException(), 
          "Impermissible bean path " + rootpath);
    }
  }

}
