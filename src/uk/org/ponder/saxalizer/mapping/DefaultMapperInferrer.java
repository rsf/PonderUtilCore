/*
 * Created on Sep 22, 2004
 */
package uk.org.ponder.saxalizer.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import uk.org.ponder.saxalizer.SAMSList;
import uk.org.ponder.saxalizer.SAXAccessMethodSpec;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
public class DefaultMapperInferrer implements SAXalizerMapperInferrer {

  public static int accessorType(String methodname) {
    if (methodname.startsWith("get")) {
      return SAXAccessMethodSpec.GET_METHOD;
    }
    if (methodname.startsWith("set") || methodname.startsWith("add")) {
      return SAXAccessMethodSpec.SET_METHOD;
    }
    return -1;
  }

  public static String deBean(String methodname) {
    return Character.toLowerCase(methodname.charAt(3))
        + methodname.substring(4);
  }

  public SAXalizerMapperEntry inferEntry(Class clazz) {
    SAXalizerMapperEntry togo = new SAXalizerMapperEntry();
    togo.targetclass = clazz;
    SAMSList sams = togo.getSAMSList();
    Method[] methods = clazz.getMethods();

    for (int i = 0; i < methods.length; ++i) {
      int modifiers = methods[i].getModifiers(); 
      if (Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) continue;
      String methodname = methods[i].getName();
      if (methodname.equals("getClass")) continue;
      int methodtype = accessorType(methodname);
      if (methodtype != -1) {
        String basename = deBean(methodname);
        SAXAccessMethodSpec spec = sams.byXMLNameSafe(basename);
        if (methodtype == SAXAccessMethodSpec.GET_METHOD) {
          spec.getmethodname = methodname;
        }
        else {
          spec.setmethodname = methodname;
        }
        togo.addTagHandler(spec);
      }
    }

    Field[] fields = clazz.getFields();
    for (int i = 0; i < fields.length; ++i) {
      String fieldname = fields[i].getName();
      SAXAccessMethodSpec spec = sams.byXMLNameSafe(fieldname);
      spec.fieldname = fieldname;
      togo.addTagHandler(spec);
    }

    return togo;
  }

}