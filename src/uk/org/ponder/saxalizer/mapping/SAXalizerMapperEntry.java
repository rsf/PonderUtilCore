/*
 * Created on Sep 22, 2004
 */
package uk.org.ponder.saxalizer.mapping;

import java.util.ArrayList;

import uk.org.ponder.saxalizer.DeSAXalizable;
import uk.org.ponder.saxalizer.SAMSList;
import uk.org.ponder.saxalizer.SAXAccessMethod;
import uk.org.ponder.saxalizer.SAXAccessMethodSpec;
import uk.org.ponder.saxalizer.SAXalizable;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
public class SAXalizerMapperEntry implements SAXalizable, DeSAXalizable {
  public Class targetclass;
  // this is an list of SAXAccessMethodSpecs.
  private SAMSList subtagentries = new SAMSList();

  public SAXAccessMethodSpec[] getSAXSetMethods() {
    return new SAXAccessMethodSpec[] {
        new SAXAccessMethodSpec("targetclass", "targetclass", Class.class,
            SAXAccessMethodSpec.ACCESS_FIELD),
        new SAXAccessMethodSpec("tag", "addTagHandler",
            SAXAccessMethodSpec.class) };
  }
  
  public SAXAccessMethodSpec[] getSAXGetMethods() {
    // TODO Auto-generated method stub
    return null;
  }
  public void addTagHandler(SAXAccessMethodSpec toadd) {
    subtagentries.add(toadd);
  }
  public int size() {
    return subtagentries.size();
  }
  public SAXAccessMethodSpec specAt(int i) {
    return (SAXAccessMethodSpec) subtagentries.get(i);
  }
  
  public SAMSList getSAMSList() {
    return subtagentries;
  }
// Method must match in XML type (attribute or tag),
// a FIELD method matches either a set or a get method.
  private static boolean matchMethod(SAXAccessMethodSpec tomatch, 
      int accesstype, String xmlform) {
    return tomatch.xmlform.equals(xmlform)
         && tomatch.accesstype.equals(SAXAccessMethodSpec.ACCESS_FIELD)
             || (accesstype == SAXAccessMethodSpec.GET_METHOD && 
                 tomatch.getmethodname != null
                 || accesstype == SAXAccessMethodSpec.SET_METHOD &&
                 tomatch.setmethodname != null);
      
  }
  /** Assemble an array of the tag handlers in this entry that match the
   * supplied specification. 
   * @return
   */
  public SAXAccessMethodSpec[] assembleMethods(int getorset, String xmlform) {
    int count = 0;
    for (int i = 0; i < subtagentries.size(); ++ i) {
      if (matchMethod(specAt(i), getorset, xmlform)) ++ count;
    }
    if (count == 0) return null;
    SAXAccessMethodSpec[] togo = new SAXAccessMethodSpec[count];
    count = 0;
    for (int i = 0; i < subtagentries.size(); ++ i) {
      SAXAccessMethodSpec thisspec = specAt(i);
      if (matchMethod(thisspec, getorset, xmlform)) {
        togo[count++] = thisspec;
      }
    }
    return togo;
  }
}