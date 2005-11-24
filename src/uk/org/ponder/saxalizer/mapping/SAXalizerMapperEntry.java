/*
 * Created on Sep 22, 2004
 */
package uk.org.ponder.saxalizer.mapping;

import uk.org.ponder.saxalizer.DeSAXalizable;
import uk.org.ponder.saxalizer.SAMSList;
import uk.org.ponder.saxalizer.SAXAccessMethodSpec;
import uk.org.ponder.saxalizer.SAXalizable;
import uk.org.ponder.saxalizer.SAXalizableAttrs;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
public class SAXalizerMapperEntry implements SAXalizable, DeSAXalizable, 
SAXalizableAttrs {
  public Class targetclass;
  public boolean defaultible = false;
  private SAMSList subtagentries = new SAMSList();

  public SAXAccessMethodSpec[] getSAXSetMethods() {
    return new SAXAccessMethodSpec[] {
        new SAXAccessMethodSpec("taghandler", "addTagHandler",
            SAXAccessMethodSpec.class),
        new SAXAccessMethodSpec("defaultible", "defaultible", 
            Boolean.TYPE, SAXAccessMethodSpec.ACCESS_FIELD)};
  }

  public SAXAccessMethodSpec[] getSAXSetAttrMethods() {
    return new SAXAccessMethodSpec[] {
        new SAXAccessMethodSpec("targetclass", "targetclass", Class.class,
            SAXAccessMethodSpec.ACCESS_FIELD)
        };
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
  public void remove(int index) {
    subtagentries.remove(index);
  }
  public void addNonDuplicate(SAXAccessMethodSpec newspec) {
    for (int i = 0; i < size(); ++i) {
      SAXAccessMethodSpec thisspec = specAt(i);
      if (thisspec.isDuplicate(newspec)) {
        return;
      }
    }
    addTagHandler(newspec);
  }
  public SAMSList getSAMSList() {
    return subtagentries;
  }


}