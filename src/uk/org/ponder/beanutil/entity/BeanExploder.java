/*
 * Created on 03-Feb-2006
 */
package uk.org.ponder.beanutil.entity;

import java.util.HashMap;
import java.util.Map;

import uk.org.ponder.beanutil.BeanLocator;
import uk.org.ponder.reflect.DeepBeanCloner;

/** Will "explode" a bean prototype into a "freely indexed" address space by
 * cloning or instantiation. Has three main instantiation strategies, to cope
 * with increasing orders of dependence:
 * <p>1. A simple classname - the class designates a default-constructible bean.
 * <p>2. An "exemplar" instance - the instance will be deep-cloned for each required 
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public class BeanExploder implements BeanLocator {
  private Object exemplar;
  
  private Map beans = new HashMap();

  private DeepBeanCloner deepbeancloner;
  private Class beanclass;
  
  public void setBeanClass(Class beanclass) {
    this.beanclass = beanclass;
  }
  
  public void setExemplar(Object exemplar) {
    this.exemplar = exemplar;
  }
  
  public void setDeepBeanCloner(DeepBeanCloner deepbeancloner) {
    this.deepbeancloner = deepbeancloner;
  }
  
  public Object locateBean(String path) {
    Object togo = beans.get(path);
    if (togo == null) {
      if (beanclass != null) {
        togo = deepbeancloner.getReflectiveCache().construct(beanclass);
      }
      else if (exemplar != null) {
        togo = deepbeancloner.cloneBean(exemplar);
      }
      beans.put(path, togo);
    }
    return togo;
  }

}