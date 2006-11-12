/*
 * Created on 03-Feb-2006
 */
package uk.org.ponder.beanutil.entity;

import java.util.HashMap;
import java.util.Map;

import uk.org.ponder.beanutil.BeanLocator;
import uk.org.ponder.beanutil.ELEvaluator;
import uk.org.ponder.reflect.DeepBeanCloner;
import uk.org.ponder.util.ObjectFactory;

/**
 * Will "explode" a bean prototype into a "freely indexed" address space by
 * cloning or instantiation. Has four main instantiation strategies, to cope
 * with increasing orders of dependence:
 * <p>
 * 1. A simple classname - the class designates a default-constructible bean.
 * <p>
 * 2. An "exemplar" instance - the instance will be deep-cloned for each
 * required
 * <p>
 * 3. An EL path from which a prototype instance will be fetched (assumed to be
 * the path of a non-singleton bean)
 * <p>
 * 4. An instance of ObjectFactory delivered to setFactory - this will be done
 * automatically by RSAC on receiving an inner bean definition.
 * 
 * @author Antranig Basman (amb26@ponder.org.uk)
 * 
 */

public class BeanExploder implements BeanLocator {
  private Object exemplar;

  private Map beans = new HashMap();

  private DeepBeanCloner deepbeancloner;
  private Class beanclass;

  private String beanname;

  private ELEvaluator elEvaluator;

  private ObjectFactory factory;

  public void setBeanClass(Class beanclass) {
    this.beanclass = beanclass;
  }

  // Should be the path to a non-singleton Spring bean
  public void setBeanName(String beanname) {
    this.beanname = beanname;
  }

  public void setExemplar(Object exemplar) {
    this.exemplar = exemplar;
  }

  public void setDeepBeanCloner(DeepBeanCloner deepbeancloner) {
    this.deepbeancloner = deepbeancloner;
  }

  public void setELEvaluator(ELEvaluator elEvaluator) {
    this.elEvaluator = elEvaluator;
  }

  public void setFactory(ObjectFactory factory) {
    this.factory = factory;
  }

  public Object locateBean(String path) {
    Object togo = beans.get(path);
    if (togo == null) {
      if (factory != null) {
        togo = factory.getObject();
      }
      else if (beanname != null) {
        togo = elEvaluator.getBean(beanname);
      }
      else if (beanclass != null) {
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
