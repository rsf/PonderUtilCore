/*
 * Created on Sep 29, 2005
 */
package uk.org.ponder.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import uk.org.ponder.arrayutil.ArrayUtil;
import uk.org.ponder.conversion.StaticLeafParser;
import uk.org.ponder.saxalizer.SAXAccessMethod;
import uk.org.ponder.util.Logger;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * A cache for simple no-arg methods and constructors, of the sort that are
 * typically used in bean environments, that are not suitable to be considered
 * as "bean properties" and hence handled by a SAXAccessMethod.
 *
 * The class is capable of resolving methods and constructors with arguments,
 * but accessors for these members are not cached or accelerated.
 * 
 * The intention is that **ALL** application-wide reflection will be done either
 * in this class, or in SAXAccessMethod.
 * 
 * 
 * This implementation will probably shortly be replaced by a FastClass variant.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public abstract class ReflectiveCache {
  private Map rootmap;
  private static Class concurrent1mapclass;
// The constructor for the oswego n-concurrent map class - we need to handle
// this explicitly, since the concurrency level has to be passed in as an argument.
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
      System.err.println("Fatal: Could not instantiate concurrent map class from either oswego or JDK 1.5 provider");
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
          return mapclass.getConstructor(new Class[] { Integer.TYPE, Float.TYPE,
              Integer.TYPE });  
        }
      }
      else {
        return mapclass.getConstructor(SAXAccessMethod.emptyclazz);
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

  /**
   * Get a no-argument constructor for a class
   * @param clazz the class
   * @return a no-arg constructor
   */
  public static Constructor getConstructor(Class clazz) {
    try {
      return clazz.getConstructor(SAXAccessMethod.emptyclazz);
    } catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e, "Error getting constructor for " + clazz);
    }
  }

  protected Map getClassMap(Class target) {
    Map classmap = (Map) rootmap.get(target);
    if (classmap == null) {
      classmap = getConcurrentMap(1);
      rootmap.put(target, classmap);
    }
    return classmap;
  }

  public static Method getMethod(Class clazz, String name) {
   return getMethod(clazz, name, SAXAccessMethod.emptyclazz);
  }
  
  public static Method getMethod(Class clazz, String name, Class[] argtypes) {
	try {
      return clazz.getMethod(name, argtypes);
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e,
          "Error reflecting for method " + name + " of " + clazz);
    } 
  }
  
  public abstract Object construct(Class clazz);
  public abstract Object invokeMethod(Object bean, String method);
  protected abstract Object invokeMethod(Object target, String name, Class[] infer, Object[] args);

  private static String getInvokeError(Object target, String name, Object[] args) {
    return name + " in object " + target.getClass() + " with " + args.length + " arguments: "
    + ArrayUtil.toString(args);
  }
  
  /**
   * Somewhat inefficient, but this scheme applies 
   */
  public Object invokeMethod(Object target, String name, Object[] args) {
    if (args == null || args.length == 0) {
      return invokeMethod(target, name);
    }
    if (target instanceof MethodInvokingProxy) {
      return ((MethodInvokingProxy)target).invokeMethod(name, args);
    }
    Method[] matching = ReflectUtils.getMatchingMethods(target.getClass(), name, args);
    if (matching.length == 0) {
      throw new IllegalArgumentException("Could not find method named " + 
          getInvokeError(target, name, args));
         
    }
    if (matching.length >= 2) {
      throw new IllegalArgumentException("Found ambiguous match with " + matching.length 
          + " equally good matches for " + getInvokeError(target, name, args));
    }
    Method match = matching[0];
    Class[] infer = new Class[args.length];
    for (int i = 0; i < args.length; ++ i) {
      Class argclazz = match.getParameterTypes()[i];
      // proved to be the only case, by definition of getMatchingMethods
      if (!ReflectUtils.isAssignable(argclazz, args[i])) {
        args[i] = StaticLeafParser.instance().parse(argclazz, (String) args[i]);
      }
      infer[i] = args[i].getClass();
    }
    return JDKReflectiveCache.invokeMethod(match, target, args);
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
  public Map getConcurrentMap(int concurrency) {
    // if map class is null, this is the first call for the entire system,
    // perhaps
    // from getClassMap. Initialise the root table, and put the constructor for
    // the concurrent map itself in it as its first entry, manually.
    // Uninteresting race condition here.
    // TODO: try to remove downcall to JDKReflector
    if (concurrency == 1) {
      // check both of these because of possible race condition.
      if (concurrent1mapclass == null || rootmap == null) {
        concurrent1mapclass = getConcurrent1MapClass();
        Constructor cons = getConstructor(concurrent1mapclass);
        rootmap = (Map) JDKReflectiveCache.invokeConstructor(cons);
        Map classmap = (Map) JDKReflectiveCache.invokeConstructor(cons);
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
        togo = JDKReflectiveCache.invokeConstructor(concurrentnmapcons, new Object[] {
            new Integer(INIT_MAP_SIZE), new Float(0.75f),
            new Integer(concurrency) });
      }
      else {
        togo = JDKReflectiveCache.invokeConstructor(concurrentnmapcons);
      }
      return (Map) togo;
    }

  }

}
