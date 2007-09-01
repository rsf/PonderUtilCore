/*
 * Created on 21 Aug 2007
 */
package uk.org.ponder.beanutil.entity.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import uk.org.ponder.beanutil.BeanLocator;
import uk.org.ponder.beanutil.BeanModelAlterer;
import uk.org.ponder.beanutil.PathUtil;
import uk.org.ponder.reflect.ReflectiveCache;

/**
 * An "automated" implementation of BeanLocator, useful for managing "entities", 
 * addressed by a unique ID convertible to String, which are already provided with
 * some form of (probably application-scope) "DAO"-type API. This implementation
 * allows the DAO API to be expressed by means of EL method bindings to its 
 * various methods, from which the managed entities are mapped into this 
 * this request-scope cache.
 * </p>
 * Typically the user will provide a <code>fetchMethod</code> which will fetch
 * entities by ID, and (at least one of) a <code>newMethod</code> or an
 * <code>entityClass</code> which is used to instantiate new entities which
 * are not persistent. <p/> At any time, the "cache" of currently addressed
 * entities within this request may be accessed through the map
 * <code>deliveredBeans</code> 
 * <p/> If the <code>saveEL</code> is also
 * supplied, this locator will also implement a <code>saveAll()</code> method
 * which will return all the delivered entities back to persistence. If this is
 * not supplied, they are assumed to be persisted by some other means (perhaps
 * an auto-commit of some kind).
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */

public class EntityBeanLocatorImpl implements BeanLocator {
  public static final String NEW_PREFIX = "new ";
  private String fetchEL;
  private String newEL;
  private Class entityClazz;
  private String saveEL;

  private Map delivered = new HashMap();
  private BeanModelAlterer bma;
  private BeanLocator beanlocator;
  private ReflectiveCache reflectivecache;

  public void setFetchMethod(String fetchEL) {
    this.fetchEL = fetchEL;
  }

  public void setNewMethod(String newEL) {
    this.newEL = newEL;
  }

  public void setEntityClass(Class entityClazz) {
    this.entityClazz = entityClazz;
  }

  public void setSaveMethod(String saveEL) {
    this.saveEL = saveEL;
  }

  public void setBeanModelAlterer(BeanModelAlterer bma) {
    this.bma = bma;
  }

  public void setBeanLocator(BeanLocator beanlocator) {
    this.beanlocator = beanlocator;
  }

  public void setReflectiveCache(ReflectiveCache reflectivecache) {
    this.reflectivecache = reflectivecache;
  }

  public void init() {
    if (entityClazz == null && newEL == null) {
      throw new IllegalArgumentException(
          "At least one of entityClass and newEL must be set");
    }
  }

  public Object locateBean(String name) {
    Object togo = delivered.get(name);
    if (togo == null) {
      if (name.startsWith(NEW_PREFIX)) {
        if (newEL != null) {
          togo = bma.invokeBeanMethod(newEL, beanlocator);
        }
        else {
          togo = reflectivecache.construct(entityClazz);
        }
      }
      else {
        togo = bma.invokeBeanMethod(PathUtil.composePath(fetchEL, '\'' + name + '\''),
            beanlocator);
      }
      delivered.put(name, togo);
    }
    return togo;
  }

  public void saveAll() {
    if (saveEL != null) {
      String penultimate = PathUtil.getToTailPath(saveEL);
      String savemethod = PathUtil.getTailPath(saveEL);
      Object saver = bma.getBeanValue(penultimate, beanlocator, null);

      for (Iterator it = delivered.entrySet().iterator(); it.hasNext();) {
        String key = (String) it.next();
        Object value = delivered.get(key);
        reflectivecache.invokeMethod(saver, savemethod, new Object[] { value });
      }
    }
  }

  public Map getDeliveredBeans() {
    return delivered;
  }
}
