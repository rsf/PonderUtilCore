package uk.org.ponder.saxalizer;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

import uk.org.ponder.arrayutil.ArrayEnumeration;
import uk.org.ponder.saxalizer.mapping.SAXalizerMapper;
import uk.org.ponder.saxalizer.mapping.SAXalizerMapperEntry;
import uk.org.ponder.util.Logger;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * each of the four types of SAXAccessMethods supporter, being get and set
 * methods for subtags and attributes.
 * <p>
 * One instance of a MethodAnalyser is stored for each SAXalizable class that
 * the SAXalizer discovers; this instance is returned when a call is made to
 * <code>getMethodAnalyser</code> with an object of the SAXalizable class as
 * argument. MethodAnalysers are cached in a static hashtable indexed by the
 * SAXalizable class.
 */
class MethodAnalyser {
  public SAXAccessMethodHash tagmethods;
  public SAXAccessMethodHash attrmethods;

  /**
   * This class analyses a supplied object by calling its getSAXxxxxMethods() to
   * return the names of methods suitable for setting and getting subobjects via
   * the SAXalizable and SAXalizableAttrs methods.
   */

  public static MethodAnalyser getMethodAnalyser(Object o,
      SAXalizerMappingContext context) {
    if (o == null)
      return null;
    Class objclass = o.getClass();
    MethodAnalyser stored = context.getAnalyser(objclass);
    if (stored != null)
      return stored;
    else {
      SAXalizerMapperEntry entry = context.mapper.byClass(objclass);
      stored = new MethodAnalyser(o, entry);
      context.putAnalyser(objclass, stored);
    }
    return stored;
  }

  void condenseMethods(SAMSList existingmethods, Enumeration newmethods, String xmlform) {
    while(newmethods.hasMoreElements()) {
      SAXAccessMethodSpec nextentry = (SAXAccessMethodSpec)newmethods.nextElement();
      if (nextentry.xmlform.equals(xmlform)) {
        SAXAccessMethodSpec previous = existingmethods.byXMLName(nextentry.xmlname);
        if (previous != null) {
          SAXAccessMethodSpec setmethod = null;
          if (previous.setmethodname != null) setmethod = previous;            
          if (nextentry.setmethodname != null) {
            if (setmethod != null) {
              throw new UniversalRuntimeException("Duplicate set method specification for tag "+
                  previous.xmlname + " with java type "+ previous.clazz);
            }
            setmethod = nextentry;
          }
          // The "set" method will in general have a more precise argument type.
          previous.clazz = setmethod.clazz;
          if (nextentry.getmethodname != null) {
            previous.getmethodname = nextentry.getmethodname;
          }
        }
        else {
          existingmethods.add(nextentry);
        }
      }
    }
  }
  
  private void absorb(SAXAccessMethodSpec[] setmethods, SAMSList tagMethods, SAMSList attrMethods) {
    condenseMethods(tagMethods, new ArrayEnumeration(setmethods), SAXAccessMethodSpec.XML_TAG);
    condenseMethods(attrMethods, new ArrayEnumeration(setmethods), SAXAccessMethodSpec.XML_ATTRIBUTE);
  }
  
  MethodAnalyser(Object o, SAXalizerMapperEntry entry) {
    SAMSList tagMethods = new SAMSList();
    SAMSList attrMethods = new SAMSList();
    if (entry != null) {
      // dynamic info takes priority over static info
      condenseMethods(tagMethods, Collections.enumeration(entry.getSAMSList()), SAXAccessMethodSpec.XML_TAG);
      condenseMethods(attrMethods, Collections.enumeration(entry.getSAMSList()), SAXAccessMethodSpec.XML_ATTRIBUTE);
    }
    else {
      //    System.out.println("MethodAnalyser called for object "+o);
      if (o instanceof SAXalizable) {
        SAXalizable so = (SAXalizable) o;
        SAXAccessMethodSpec[] setMethods = so.getSAXSetMethods();
        SAXAccessMethodSpec.convertToSetSpec(setMethods);
        absorb(setMethods, tagMethods, attrMethods);
      }
      if (o instanceof SAXalizableAttrs) { // now do the same for attributes
        SAXalizableAttrs sao = (SAXalizableAttrs) o;
        SAXAccessMethodSpec[] setAttrMethods = sao.getSAXSetAttrMethods();
        if (setAttrMethods != null) {
          Logger.println("MethodAnalyser found " + setAttrMethods.length
              + " setattr methods for " + o, Logger.DEBUG_INFORMATIONAL);
        }
        SAXAccessMethodSpec.convertToAttrSpec(setAttrMethods);
        SAXAccessMethodSpec.convertToSetSpec(setAttrMethods);
        absorb(setAttrMethods, tagMethods, attrMethods);
      }
      if (o instanceof DeSAXalizable) {
        // construct array of SAXAccessMethods for DeSAXalizable objects
        DeSAXalizable doz = (DeSAXalizable) o;
        SAXAccessMethodSpec[] getMethods = doz.getSAXGetMethods();
        absorb(getMethods, tagMethods, attrMethods); 
      }
      if (o instanceof DeSAXalizableAttrs) { // now do the same for attributes
        DeSAXalizableAttrs sao = (DeSAXalizableAttrs) o;
        SAXAccessMethodSpec[] getAttrMethods = sao.getSAXGetAttrMethods();
        if (getAttrMethods != null) {
          SAXAccessMethodSpec.convertToAttrSpec(getAttrMethods);
          Logger.println("MethodAnalyser found " + getAttrMethods.length
              + " getattr methods for " + o, Logger.DEBUG_INFORMATIONAL);
        }
        absorb(getAttrMethods, tagMethods, attrMethods); 
      }
    }
   
    Class objclass = o.getClass();
    tagmethods = new SAXAccessMethodHash(tagMethods, objclass);
    attrmethods = new SAXAccessMethodHash(attrMethods, objclass);
  }

}