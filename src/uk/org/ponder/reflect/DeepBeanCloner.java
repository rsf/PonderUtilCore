/*
 * Created on Nov 25, 2005
 */
package uk.org.ponder.reflect;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import uk.org.ponder.saxalizer.MethodAnalyser;
import uk.org.ponder.saxalizer.SAXAccessMethod;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;
import uk.org.ponder.stringutil.StringList;
import uk.org.ponder.util.EnumerationConverter;

/**
 * A moderately capable deep cloner of beans. Will not cope with anything
 * unusual like multidimensional arrays, and assumes that every non-leaf object
 * encountered in the tree is default-constructible (including collections).
 * Also assumes that the object graph supplied is a tree.
 * <p>
 * Never uses Object.clone() but rather direct introspection and type inference,
 * so should run relatively like a rocket compared to most things out there
 * (once FastClass is integrated) - 60ns*nprops per bean rather than
 * 900ns+16ns*nprops. (Cost per property - 1xFastClass, probably no constructs
 * (since immutable), perhaps 3 function calls, a loop it and a hashmap lookup).
 * <p>
 * Anyone capable of rewriting this code into the corresponding BCEL visitor is
 * strongly invited to do so.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */

public class DeepBeanCloner {
  private SAXalizerMappingContext mappingcontext;
  private ReflectiveCache reflectivecache;

  public void setMappingContext(SAXalizerMappingContext mappingcontext) {
    this.mappingcontext = mappingcontext;
  }

  public void setReflectiveCache(ReflectiveCache reflectivecache) {
    this.reflectivecache = reflectivecache;
  }

  /** Simply produce another object of the same type as the argument * */
  public Object emptyClone(Object toclone) {
    return reflectivecache.construct(toclone.getClass());
  }

  public Object cloneBean(Object toclone) {
    return cloneBean(toclone, null);
  }

  /**
   * Produce a deep clone of the supplied object
   * 
   * @param toclone
   *          The object to be cloned
   * @param A
   *          list of property names to be excluded from the top-level bean.
   */
  public Object cloneBean(Object toclone, StringList exceptions) {
    if (toclone == null)
      return null;
    Class objclass = toclone.getClass();
    Object cloned = null;
    if (mappingcontext.saxleafparser.isLeafType(objclass)) {
      cloned = mappingcontext.saxleafparser.copy(toclone);
    }
    else if (toclone instanceof Collection) {
      Collection coll = (Collection) toclone;
      Collection togo = (Collection) reflectivecache.construct(objclass);
      for (Iterator colit = coll.iterator(); colit.hasNext();) {
        Object next = colit.next();
        cloned = cloneBean(next, null);
        togo.add(cloned);
      }
      cloned = togo;
    }
    else if (toclone instanceof Map) {
      Map map = (Map) toclone;
      Map togo = (Map) reflectivecache.construct(objclass);
      for (Iterator keyit = map.values().iterator(); keyit.hasNext();) {
        Object key = keyit.next();
        Object value = map.get(key);
        Object clonekey = cloneBean(key, null);
        Object clonevalue = cloneBean(value, null);
        togo.put(clonekey, clonevalue);
      }
      cloned = togo;
    }
    else if (toclone.getClass().isArray()) {
      Object[] array = (Object[]) toclone;
      Object[] clonedarr = (Object[]) ReflectUtils.instantiateContainer(
          objclass, array.length, reflectivecache);
      for (int i = 0; i < array.length; ++i) {
        clonedarr[i] = cloneBean(array[i], null);
      }
      cloned = clonedarr;
    }
    else {
      // The general case. A trunk object with a good lot of properties.
      MethodAnalyser ma = mappingcontext.getAnalyser(objclass);
      cloned = reflectivecache.construct(objclass);
      for (int i = 0; i < ma.allgetters.length; ++i) {
        SAXAccessMethod sam = ma.allgetters[i];
        if (!sam.canGet() || !sam.canSet())
          continue;
        if (exceptions != null && exceptions.contains(sam.tagname))
          continue;
        if (sam.isexactsetter) {
          Enumeration childenum = EnumerationConverter.getEnumeration(sam
              .getChildObject(toclone));
          while (childenum.hasMoreElements()) {
            Object child = childenum.nextElement();
            Object clonechild = cloneBean(child, null);
            sam.setChildObject(cloned, clonechild);
          }
        }
        else {
          Object child = sam.getChildObject(toclone);
          if (child != null) {
            Object clonechild = cloneBean(child, null);
            sam.setChildObject(cloned, clonechild);
          }
        }
      }

    }
    return cloned;
  }
}
