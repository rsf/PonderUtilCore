/*
 * Created on Aug 3, 2005
 */
package uk.org.ponder.beanutil;

import java.lang.reflect.Method;
import java.util.HashMap;

import uk.org.ponder.arrayutil.ArrayUtil;
import uk.org.ponder.saxalizer.SAXAccessMethod;
import uk.org.ponder.stringutil.StringList;
import uk.org.ponder.util.ReflectiveCache;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class BeanContainerWrapper implements RootBeanLocator {
  private BeanGetter factory;
  private String[] permittedroots;
  
  public void setBeanGetter(BeanGetter beangetter) {
    factory = beangetter;
  }
  public void setPermittedBeanRoots(StringList permittedroots) {
    this.permittedroots = permittedroots.toStringArray();
  }
 
  public Object locateRootBean(String path) {
    if (path.indexOf('.') != -1) {
      throw new UniversalRuntimeException("Root path of " + path
          + " is not valid");
    }
    return factory.getBean(path);
  }

  
  public Object getBean(String path) {
    String headpath = PathUtil.getHeadPath(path);
    if (permittedroots == null || 
        ArrayUtil.indexOf(permittedroots, headpath) == -1) {
      throw UniversalRuntimeException.accumulate(new SecurityException(), 
          "Impermissible bean path " + path);
    }
    
    Object bean = factory.getBean(path);
    return bean;
  }
  
  public void invokeBeanMethod(String pathwithmethod) {
    String totail = PathUtil.getToTailPath(pathwithmethod);
    String methodname = PathUtil.getTailPath(pathwithmethod);
    Object bean = getBean(totail);
    try {
      ReflectiveCache.invokeMethod(bean, methodname);
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e, "Error invoking method " + methodname + " in bean of " + 
          bean.getClass() + " at path " + totail);
      }
    
  }
 

}
