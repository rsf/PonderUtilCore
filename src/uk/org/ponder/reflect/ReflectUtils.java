/*
 * Created on Nov 26, 2004
 */
package uk.org.ponder.reflect;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.org.ponder.stringutil.StringList;
import uk.org.ponder.util.Logger;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class ReflectUtils {

  public static boolean isPublicStatic(int modifiers) {
    return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers);
  }
  
  /** Returns a list of all superclasses and implemented interfaces by the
   * supplied class, recursively to the base, up to but excluding Object.class.
   * These will be listed in order from the supplied class, all concrete 
   * superclasses in ascending order, and then finally all interfaces in
   * recursive ascending order.
   */
  
  public static List getSuperclasses(Class clazz) {
    List togo = new ArrayList();
    while (clazz != Object.class) {
      togo.add(clazz);
      clazz = clazz.getSuperclass();
    }
    int supers = togo.size();
    for (int i = 0; i < supers; ++ i) {
      appendSuperclasses((Class) togo.get(i), togo);
    }
    return togo;
  }
  
  private static void appendSuperclasses(Class clazz, List accrete) {
    Class[] interfaces = clazz.getInterfaces();
    for (int i = 0; i < interfaces.length; ++ i) {
      accrete.add(interfaces[i]);
    }
    for (int i = 0; i < interfaces.length; ++ i) {
      appendSuperclasses(interfaces[i], accrete);
    }
  }
  
  public static final int UNKNOWN_SIZE = -1;
  /** Instantiates a "default" type of container conforming to a given interface
   * and of a given size. If asked to create an array of unknown size, will return
   * an zero-element array instead, signalling a "placeholder".
   */
  public static Object instantiateContainer(Class declaredtype, int size, ReflectiveCache cache) {
    // only missing case is if someone madly declares an argument of type
    // "AbstractList" or similar...
    if (declaredtype == List.class || declaredtype == Collection.class) {
      return size == UNKNOWN_SIZE? new ArrayList() : new ArrayList(size);
    }
    else if (declaredtype == Set.class) {
      return size == UNKNOWN_SIZE? new HashSet() : new HashSet(size);
    }
    else if (declaredtype == Map.class) {
      return size == UNKNOWN_SIZE? new HashMap() : new HashMap(size);
    }
    else if (declaredtype.isArray()) {
      if (size == UNKNOWN_SIZE) size = 0;
      // erm, this is a native method! How long does it take exactly!
      Class component = declaredtype.getComponentType();
      if (component == Object.class) {
        // the most common case. 
        return new Object[size];
      }
      else {
        return Array.newInstance(component, size);
      }
    }
    // else it had better be something default-constructible.
    else return cache.construct(declaredtype);
  }
  
  public static final boolean PREFIX = true;
  public static final boolean SUFFIX = false;
  
  public static void addStaticStrings(Object provider, String xfix, boolean prefix, StringList strings) {
    Class provclass = provider instanceof Class ? (Class) provider
        : provider.getClass();
    Field[] fields = provclass.getFields();
    for (int j = 0; j < fields.length; ++j) {
      Field field = fields[j];
      try {
        int modifiers = field.getModifiers();
        if (!isPublicStatic(modifiers))
          continue;
        String fieldname = field.getName();
        if ((prefix? fieldname.startsWith(xfix) : 
          fieldname.endsWith(xfix)) && field.getType() == String.class) {
          strings.add(field.get(provider));
        }
      }
      catch (Throwable t) {
        Logger.log.fatal("Error reflecting for static names ", t);
      }
    }
  }
}
