/*
 * Created on Mar 1, 2005
 */
package uk.org.ponder.mapping;

import java.util.logging.Level;

import uk.org.ponder.saxalizer.MethodAnalyser;
import uk.org.ponder.saxalizer.SAXAccessMethod;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;
import uk.org.ponder.util.Logger;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
// WARNING - DARCopier currently ONLY PERFORMS A SHALLOW COPY in the
// style of Object.clone. Deep copy implementation awaits generalisation
// of SAXalizer and DeSAXalizer architecture for general object delivery/
// iteration.
public class DARCopier {
  private SAXalizerMappingContext mappingcontext;

  public void setMappingContext(SAXalizerMappingContext mappingcontext) {
    this.mappingcontext = mappingcontext;
  }

  public Object copy(Object tocopy) {
    Class objclass = tocopy.getClass();
    Object togo;
    try {
      togo = objclass.newInstance();
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e, "Object of " + objclass
          + " is not default constructible");
    }
    MethodAnalyser ma = MethodAnalyser
        .getMethodAnalyser(tocopy, mappingcontext);
    for (int i = 0; i < ma.allgetters.length; ++i) {
      SAXAccessMethod accessmethod = ma.allgetters[i];
      if (!accessmethod.canSet()) {
        Logger.log.warn("Access method " + accessmethod.tagname
            + " could not be used for setting");
      }
      Object got = accessmethod.getChildObject(tocopy);
      accessmethod.setChildObject(togo, got);

    }
    return togo;
  }
}