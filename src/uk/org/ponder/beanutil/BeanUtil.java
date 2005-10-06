/*
 * Created on Aug 4, 2005
 */
package uk.org.ponder.beanutil;

import uk.org.ponder.mapping.DARApplier;
import uk.org.ponder.saxalizer.SAXAccessMethod;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class BeanUtil {

  public static Object navigate(Object rootobj, String path, SAXalizerMappingContext mappingcontext) {
    if (path == null) return rootobj;
    String[] components = path.split("\\.");
    Object moveobj = rootobj;
    for (int comp = 0; comp < components.length; ++comp) {
      SAXAccessMethod am = DARApplier.getAMExpected(moveobj, components[comp], mappingcontext);
      moveobj = am.getChildObject(moveobj);
    }
    return moveobj;
  }

}
