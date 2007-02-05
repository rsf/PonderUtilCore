/*
 * Created on Aug 4, 2005
 */
package uk.org.ponder.beanutil;

import java.util.Iterator;
import java.util.Map;

import uk.org.ponder.saxalizer.MethodAnalyser;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;
import uk.org.ponder.stringutil.CharWrap;
import uk.org.ponder.stringutil.StringList;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class BeanUtil {
  /**
   * The String prefix used for the ID of a freshly created entity to an
   * "obstinate" BeanLocator following the standard OTP system. The text
   * following the prefix is arbitrary. Custom OTP systems might use a different
   * prefix.
   */
  public static String NEW_ENTITY_PREFIX = "new ";

  public static String[] splitEL(String path) {
    StringList togo = new StringList();
    CharWrap build = new CharWrap();
    int index = 0;
    while (index < path.length()) {
      index = PathUtil.getPathSegment(build, path, index) + 1;
      togo.add(build.toString());
      build.clear();
    }
    return togo.toStringArray();
  }

  /** Compose head and tail paths, where escaping is unnecessary * */
  public static String composeEL(String head, String tail) {
    return head + '.' + tail;
  }

  public static String composeEL(StringList tocompose) {
    CharWrap togo = new CharWrap();
    for (int i = 0; i < tocompose.size(); ++i) {
      PathUtil.composeSegment(togo, tocompose.stringAt(i));
      if (i != tocompose.size() - 1) {
        togo.append(".");
      }
    }
    return togo.toString();
  }

  public static void copyBeans(Map source, WriteableBeanLocator target) {
    for (Iterator sit = source.keySet().iterator(); sit.hasNext();) {
      String name = (String) sit.next();
      Object bean = source.get(name);
      target.set(name, bean);
    }
  }

  public static void censorNullBean(String beanname, Object beanvalue) {
    if (beanvalue == null) {
      throw UniversalRuntimeException.accumulate(new BeanNotFoundException(),
          "No bean with name " + beanname
              + " could be found in RSAC or application context");
    }
  }

  public static Object navigate(Object rootobj, String path,
      SAXalizerMappingContext mappingcontext) {
    if (path == null || path.equals("")) {
      return rootobj;
    }

    String[] components = splitEL(path);
    Object moveobj = rootobj;
    for (int comp = 0; comp < components.length; ++comp) {
      if (moveobj == null) {
        throw UniversalRuntimeException.accumulate(
            new IllegalArgumentException(),
            "Null value encounted in bean path at component "
                + (comp == 0 ? "<root>"
                    : components[comp - 1] + " while traversing for "
                        + components[comp]));
      }
      else {
        PropertyAccessor pa = MethodAnalyser.getPropertyAccessor(moveobj,
            mappingcontext);
        moveobj = pa.getProperty(moveobj, components[comp]);
      }
    }
    return moveobj;
  }

  /**
   * Given a string representing an EL expression beginning #{ and ending },
   * strip these off returning the bare expression. If the bracketing characters
   * are not present, return null.
   */
  public static String stripEL(String el) {
    if (el == null) {
      return null;
    }
    else if (el.startsWith("#{") && el.endsWith("}")) {
      return el.substring(2, el.length() - 1);
    }
    else
      return null;
  }

  public static String stripELNoisy(String el) {
    String stripped = stripEL(el);
    if (stripped == null) {
      throw new IllegalArgumentException("EL expression \"" + el
          + "\" is not bracketed with #{..}");
    }
    return stripped;
  }

}
