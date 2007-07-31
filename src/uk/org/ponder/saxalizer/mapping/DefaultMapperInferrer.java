/*
 * Created on Sep 22, 2004
 */
package uk.org.ponder.saxalizer.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;

import uk.org.ponder.saxalizer.DefaultInferrible;
import uk.org.ponder.saxalizer.SAMSList;
import uk.org.ponder.saxalizer.SAXAccessMethodSpec;
import uk.org.ponder.stringutil.Pluralizer;
import uk.org.ponder.stringutil.StringList;
import uk.org.ponder.stringutil.StringSet;
import uk.org.ponder.util.EnumerationConverter;
import uk.org.ponder.util.Logger;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
public class DefaultMapperInferrer implements SAXalizerMapperInferrer {
  public void setChainedInferrer(SAXalizerMapperInferrer target) {
    throw new UnsupportedOperationException("Cannot chain from default inferrer");
    // We expect to be the head of the chain
  }
  private boolean depluralize = true;
  private ContainerTypeRegistry containertyperegistry;
  public void setDepluralize(boolean depluralize) {
    this.depluralize = depluralize;
  }
 
  public void setContainerTypeRegistry(ContainerTypeRegistry containertyperegistry) {
    this.containertyperegistry = containertyperegistry;
  }
    
  private HashSet defaultiblemap = new HashSet();
  

  public void init() {
    containertyperegistry.addCollectionType(StringList.class, String.class);
    containertyperegistry.addCollectionType(StringSet.class, String.class);
  }
  
  public void setDefaultInferrible(Class clazz) {
    defaultiblemap.add(clazz);
  }

  public boolean isDefaultInferrible(Class clazz) {
    return DefaultInferrible.class.isAssignableFrom(clazz)
    || defaultiblemap.contains(clazz);
  }
  
  public static int accessorType(Method method) {
    String methodname = method.getName();
    if (methodname.length() <= 3) return -1;
    Class returntype = method.getReturnType();
    int paramlen = method.getParameterTypes().length;
    if (methodname.startsWith("get")) {
      if (paramlen == 0) {
        return SAXAccessMethodSpec.GET_METHOD;
      }
    }
    if (methodname.startsWith("is")) {
      if (paramlen == 0 && (returntype.equals(Boolean.TYPE) || 
          returntype.equals(Boolean.class))) {
        return SAXAccessMethodSpec.GET_METHOD;
      }
    }
    if (methodname.startsWith("set") || methodname.startsWith("add")) {
      if (paramlen == 1 && returntype.equals(Void.TYPE))
      return SAXAccessMethodSpec.SET_METHOD;
    }
    return -1;
  }
  
// TODO: check that this actually agrees with beans spec!
  public static String deBean(String methodname) {
    int plen = methodname.startsWith("is")? 2 : 3;
    boolean isupperstart = Character.isUpperCase(methodname.charAt(plen + 1));
    return (isupperstart? methodname.charAt(plen) : Character.toLowerCase(methodname.charAt(plen)))
        + methodname.substring(plen + 1);
  }

  public static final String dePluralize(String accessname, Class returntype) {
    String togo = accessname;
    if (EnumerationConverter.isEnumerable(returntype)) {
      return Pluralizer.singularize(accessname);
    }  
    return togo;
  }
  
  /** Returns an new SAMS object with the given name.
   * @param tagname
   * @param clazz The *return* type of the method, hence empty for a set method.
   * @return
   */
  private SAXAccessMethodSpec byXMLNameSafe(SAMSList samslist, String tagname, Class clazz) {
    // must not fuse get and set at this point! Otherwise there will be
    // duplicate.
    SAXAccessMethodSpec spec = new SAXAccessMethodSpec();
    // depluralise if it is a get method 
    spec.xmlname = depluralize? dePluralize(tagname, clazz) : tagname;
  
    Class containeetype = containertyperegistry.getContaineeType(clazz);
    if (containeetype != null) {
      spec.clazz = containeetype;
    }
    return spec;
  }
  
  private static boolean isPublicNonStatic(int modifiers) {
    return Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers);
  }
  
  public SAXalizerMapperEntry inferEntry(Class clazz, 
      SAXalizerMapperEntry preventry) {
    SAXalizerMapperEntry togo = preventry == null? new SAXalizerMapperEntry() : preventry;
    togo.targetclass = clazz;
    SAMSList sams = togo.getSAMSList();
    Method[] methods = clazz.getMethods();
//    if (Logger.log.isDebugEnabled()) {
//      Logger.log.debug("Inferring default mapping for " + clazz);
//    }
    for (int i = 0; i < methods.length; ++i) {
      int modifiers = methods[i].getModifiers(); 
      if (!isPublicNonStatic(modifiers)) continue;
      String methodname = methods[i].getName();
      if (methodname.equals("getClass")) continue;
      int methodtype = accessorType(methods[i]);
//      if (Logger.log.isDebugEnabled()) {
//        Logger.log.debug("Method " + methodname + " access type " + methodtype);
//      }
      if (methodtype != -1) {
        String basename = deBean(methodname);
        SAXAccessMethodSpec spec = byXMLNameSafe(sams, basename, methods[i].getReturnType());
        if (methodtype == SAXAccessMethodSpec.GET_METHOD) {
          spec.getmethodname = methodname;
        }
        else {
          spec.setmethodname = methodname;
        }
//        if (Logger.log.isDebugEnabled()) {
//          Logger.log.debug("Method gave access method " + spec);
//        }
        spec.xmlname += "*"; // better make everything polymorphic
        togo.addNonDuplicate(spec);
      }
    }

    Field[] fields = clazz.getFields();
    for (int i = 0; i < fields.length; ++i) {
      String fieldname = fields[i].getName();
      int modifiers = fields[i].getModifiers();
      if (!isPublicNonStatic(modifiers)) continue;
      SAXAccessMethodSpec spec = byXMLNameSafe(sams, fieldname, fields[i].getType());
      spec.accesstype = SAXAccessMethodSpec.ACCESS_FIELD;
      spec.fieldname = fieldname;
//      if (Logger.log.isDebugEnabled()) {
//        Logger.log.debug("Field gave access method " + spec);
//      }
      spec.xmlname += "*"; // better make everything polymorphic
      togo.addNonDuplicate(spec);
    }
    
    return togo;
  }


}