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
 * This implementation will probably shortly be replaced by a FastClass variant.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class ReflectiveCache {
  private static Map rootmap;
  private static Class concurrentmapclass;

  public static Class getConcurrentMapClass() {
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
  

  private static void invokeMethod(Method method, Object target) {
    try {
      method.invoke(target, SAXAccessMethod.emptyobj);
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e);
    }
  }

  public static Map getClassMap(Class target) {
    Map classmap = (Map) rootmap.get(target);
    if (classmap == null) {
      classmap = getConcurrentMap();
      rootmap.put(target, classmap);
    }
    return classmap;
  }

  public static final String CONSTRUCTOR_KEY = "<init>";

  /**
   * Returns a new concurrent map object, suitable to be either the root map or
   * the map for elements of a particular class.
   */
  public static Map getConcurrentMap() {
    // if map class is null, this is the first call for the entire system, perhaps
    // from getClassMap. Initialise the root table, and put the constructor for
    // the concurrent map itself in it as its first entry, manually.
    // Uninteresting race condition here.
    if (concurrentmapclass == null) {
      concurrentmapclass = getConcurrentMapClass();
      Constructor cons = getConstructor(concurrentmapclass);
      rootmap = (Map) invokeConstructor(cons);
      Map classmap = (Map) invokeConstructor(cons);
      classmap.put(CONSTRUCTOR_KEY, cons);
      rootmap.put(concurrentmapclass, classmap);
    }
    return (Map) construct(concurrentmapclass);
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

  public static void invokeMethod(Object target, String name) {
    Class clazz = target.getClass();
    Map classmap = getClassMap(clazz.getClass());
    Method method = (Method) classmap.get(name);
    if (method == null) {
      method = getMethod(clazz, name);
      classmap.put(name, method);
    }
    invokeMethod(method, target);
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
