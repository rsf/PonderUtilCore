/*
 * Created on Aug 4, 2005
 */
package uk.org.ponder.beanutil;

import uk.org.ponder.saxalizer.MethodAnalyser;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;
import uk.org.ponder.stringutil.CharWrap;
import uk.org.ponder.stringutil.StringList;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class BeanUtil {
//  public static final String ELQUOTSTART = "['";
//  public static final String ELQUOTEND = "]'";
//TODO: parse the special .['thing with.dots']. form of property names.
  public static String[] splitEL(String path) {
//    StringList components = new StringList();
//    boolean wassquare = true;
//    int end = path.length();
//    for (int i = 0; i < end; ++ i) {
//      if (path.startsWith(ELQUOTSTART, i)) {
//        int quotend = path.indexOf(ELQUOTEND, i + 2);
//        if (quotend == -1) {
//          throw new IllegalArgumentException("EL quoted string opened at index " + i + " in "
//              + path + " was not closed");
//        }
//        
//      char c = path.charAt(i);
//      if (c == '[' && i < end - 1 && path.charAt(i + 1) == '')
//    }
    return path.split("\\.");
  }
  
  public static String getContainingPath(String path) {
    int dotpos = path.lastIndexOf(path);
    return dotpos == -1? null : path.substring(0, dotpos);
  }
  
  public static String composeEL(StringList tocompose) {
    CharWrap togo = new CharWrap();
    for (int i = 0; i < tocompose.size(); ++ i) {
      togo.append(tocompose.stringAt(i));
      if (i != tocompose.size() - 1) {
        togo.append(".");
      }
    }
    return togo.toString();
  }
  
  public static Object navigate(Object rootobj, String path, SAXalizerMappingContext mappingcontext) {
    if (path == null) return rootobj;
    
    String[] components = splitEL(path); 
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
