/*
 * Created on Aug 4, 2005
 */
package uk.org.ponder.beanutil;

import uk.org.ponder.saxalizer.MethodAnalyser;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class BeanUtil {

  public static Object navigate(Object rootobj, String path, SAXalizerMappingContext mappingcontext) {
    if (path == null) return rootobj;
    //TODO: parse the special .['thing with.dots']. form of property names.
    String[] components = path.split("\\.");
    Object moveobj = rootobj;
    for (int comp = 0; comp < components.length; ++comp) {
      PropertyAccessor pa = MethodAnalyser.getPropertyAccessor(moveobj, mappingcontext);
      moveobj = pa.getProperty(moveobj, components[comp]);
      //AccessMethod am = DARApplier.getAMExpected(moveobj, components[comp], mappingcontext);
      //moveobj = am.getChildObject(moveobj);
    }
    return moveobj;
  }

  /** Given a string representing an EL expression beginning #{ and ending }, 
   * strip these off returning the bare expression. If the bracketing characters
   * are not present, return null.
   */
  public static String stripEL(String el) {
    if (el.startsWith("#{") && el.endsWith("}")) {
      return el.substring(2, el.length() - 1);
    }
    else
      return null;
  }

}
