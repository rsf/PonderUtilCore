/*
 * Created on Sep 29, 2005
 */
package uk.org.ponder.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import uk.org.ponder.saxalizer.SAXAccessMethod;

/**
 * A cache for simple no-arg methods and constructors, of the sort that are
 * typically used in bean environments, that are not suitable to be considered
 * as "bean properties" and hence handled by a SAXAccessMethod.
 * 
 * The intention is that **ALL** application-wide reflection will be done either
 * in this class, or in SAXAccessMethod.
 * 
 * This class is full of static state and methods, in the belief that all 
 * reflective objects will have the same lifetime as their parent ClassLoader.
 * 
 * This implementation will probably shortly be replaced by a FastClass variant.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class ReflectiveCache {
  private static Map rootmap;
  private static Class concurrent1mapclass;

  private static Constructor concurrentnmapcons;
  private static boolean nmapisJSR166 = false;

  public static Class getConcurrent1MapClass() {
    // OSwego is preferable to JSR166 since our desired concurrency level is
    // pretty much 1. CRHM is i) optimised for this case, and ii) will be
    // cheaper in construction since we can use the no-args constructor.
    Class mapclass = ClassGetter
        .forName("EDU.oswego.cs.dl.util.concurrent.ConcurrentReaderHashMap");
    if (mapclass == null) {
      mapclass = ClassGetter.forName("java.util.concurrent.ConcurrentHashMap");

    }
    if (mapclass == null) {
      Logger.log
          .fatal("Could not instantiate concurrent map class from either oswego or JDK 1.5 provider");
    }
    return mapclass;
  }

  /**
   * Returns a Constructor capable of constructing a multiple-concurrent-reader
   * HashMap.
   */
  public static Constructor getConcurrentNMapConstructor() {
    Class mapclass = ClassGetter
        .forName("EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap");
    try {
      if (mapclass == null) {
        mapclass = ClassGetter
            .forName("java.util.concurrent.ConcurrentHashMap");
        if (mapclass != null) {
          nmapisJSR166 = true;
          return mapclass.getConstructor(SAXAccessMethod.emptyclazz);
        }
      }
      else {
        return mapclass.getConstructor(new Class[] { Integer.TYPE, Float.TYPE,
            Integer.TYPE });
      }
      if (mapclass == null) {
        Logger.log
            .fatal("Could not instantiate concurrent map class from either oswego or JDK 1.5 provider");
      }
    }
    catch (Exception e) {
      Logger.log.fatal("Exception finding constructor for " + mapclass, e);
    }
    return null;
  }

  /** Returns a no-arg constructor for the given class */
  public static Constructor getConstructor(Class clazz) {
    Constructor togo = null;
    try {
      togo = clazz.getConstructor(SAXAccessMethod.emptyclazz);
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e);
    }
    return togo;
  }

  /** Invokes the supplied no-arg constructor to create a new object */
  public static Object invokeConstructor(Constructor cons) {
    Object togo = null;
    try {
      togo = cons.newInstance(SAXAccessMethod.emptyobj);
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e,
          "Error constructing instance of " + cons.getDeclaringClass());
    }
    return togo;
  }

  public static Object invokeConstructor(Constructor cons, Object[] args) {
    Object togo = null;
    try {
      togo = cons.newInstance(args);
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e,
          "Error constructing instance of " + cons.getDeclaringClass());
    }
    return togo;
  }

  
  /** Invokes the supplied no-arg Method object on the supplied target */
  private static Object invokeMethod(Method method, Object target) {
    try {
      return method.invoke(target, SAXAccessMethod.emptyobj);
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e);
    }
  }

  private static Map getClassMap(Class target) {
    Map classmap = (Map) rootmap.get(target);
    if (classmap == null) {
      classmap = getConcurrentMap(1);
      rootmap.put(target, classmap);
    }
    return classmap;
  }

  /**
   * The key used into per-class reflective caches representing the no-arg
   * constructor.
   */
  public static final String CONSTRUCTOR_KEY = "<init>";

  /**
   * The initial size of JSR-166 multi-concurrency hashes (all others are
   * defaults)
   */
  public static final int INIT_MAP_SIZE = 1024;

  /**
   * Returns a new concurrent map object with the desired level of concurrency.
   * If the oswego package is available, any concurrency level other than 1 will
   * return the ConcurrentHashMap implementation with its hardwired level of 32.
   * Concurrency level 1 hashes are suitable for static application-wide caches,
   * e.g. for internal use of this class, to be either the root map or the map
   * for the default constructors of a particular class.
   */
  public static Map getConcurrentMap(int concurrency) {
    // if map class is null, this is the first call for the entire system,
    // perhaps
    // from getClassMap. Initialise the root table, and put the constructor for
    // the concurrent map itself in it as its first entry, manually.
    // Uninteresting race condition here.

    if (concurrency == 1) {
      if (concurrent1mapclass == null) {
        concurrent1mapclass = getConcurrent1MapClass();
        Constructor cons = getConstructor(concurrent1mapclass);
        rootmap = (Map) invokeConstructor(cons);
        Map classmap = (Map) invokeConstructor(cons);
        classmap.put(CONSTRUCTOR_KEY, cons);
        rootmap.put(concurrent1mapclass, classmap);
      }
      return (Map) construct(concurrent1mapclass);
    }
    else {
      if (concurrentnmapcons == null) {
        concurrentnmapcons = getConcurrentNMapConstructor();
      }
      Object togo = null;
      if (nmapisJSR166) {
        togo = invokeConstructor(concurrentnmapcons, new Object[] {
            new Integer(INIT_MAP_SIZE), new Float(0.75f),
            new Integer(concurrency) });
      }
      else {
        togo = invokeConstructor(concurrentnmapcons);
      }
      return (Map) togo;
    }

  }

  public static Object construct(Class clazz) {
    Map classmap = getClassMap(clazz);
    Constructor cons = (Constructor) classmap.get(CONSTRUCTOR_KEY);
    if (cons == null) {
      cons = getConstructor(clazz);
      classmap.put(CONSTRUCTOR_KEY, cons);
    }
    return invokeConstructor(cons);
  }

  public static Object invokeMethod(Object target, String name) {
    Class clazz = target.getClass();
    Map classmap = getClassMap(clazz.getClass());
    Method method = (Method) classmap.get(name);
    if (method == null) {
      method = getMethod(clazz, name);
      classmap.put(name, method);
    }
    return invokeMethod(method, target);
  }

  private static Method getMethod(Class clazz, String name) {
    try {
      return clazz.getMethod(name, SAXAccessMethod.emptyclazz);
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e,
          "Error reflecting for method " + name + " of " + clazz);
    }
  }
}
