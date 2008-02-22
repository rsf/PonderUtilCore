/*
 * Created on Nov 18, 2005
 */
package uk.org.ponder.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import uk.org.ponder.saxalizer.support.SAXAccessMethod;
import uk.org.ponder.util.UniversalRuntimeException;

public class JDKReflectiveCache extends ReflectiveCache {
  
  /** Invokes the supplied no-arg Method object on the supplied target */
  public static Object invokeMethod(Method method, Object target) {
    return invokeMethod(method, target, SAXAccessMethod.emptyobj);
  }
  
  /** Invokes the supplied Method object on the supplied target */
  public static Object invokeMethod(Method method, Object target, Object[] args) {
    try {
      return method.invoke(target, args);
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e);
    }
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
  
  public Object construct(Class clazz) {
    Map classmap = getClassMap(clazz);
    Constructor cons = (Constructor) classmap.get(CONSTRUCTOR_KEY);
    if (cons == null) {
      cons = getConstructor(clazz);
      classmap.put(CONSTRUCTOR_KEY, cons);
    }
    return invokeConstructor(cons);
  }

  public Object invokeMethod(Object target, String name) {
    if (target instanceof MethodInvokingProxy) {
      return ((MethodInvokingProxy)target).invokeMethod(name, null);
    }
    Class clazz = target.getClass();
    Map classmap = getClassMap(clazz);
    Method method = (Method) classmap.get(name);
    if (method == null) {
      method = ReflectiveCache.getMethod(clazz, name);
      classmap.put(name, method);
    }
    return invokeMethod(method, target);
  }
  
  // This method currently bypassed - lookup of multi-arg methods is assumed
  // slow and in ReflectiveCache
  protected Object invokeMethod(Object target, String name, Class[] argtypes, Object[] args) {
    Class clazz = target.getClass();
    Method toinvoke = ReflectiveCache.getMethod(clazz, name, argtypes);
    return invokeMethod(toinvoke, target, args);
  }
  
  
}
