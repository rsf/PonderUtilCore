/*
 * Created on Nov 22, 2004
 */
package uk.org.ponder.mapping;

import uk.org.ponder.saxalizer.MethodAnalyser;
import uk.org.ponder.saxalizer.SAXAccessMethod;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;
import uk.org.ponder.util.Denumeration;
import uk.org.ponder.util.EnumerationConverter;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class DARApplier {
  
  public static void applyAlteration(Object rootobj, DataAlterationRequest dar,
      SAXalizerMappingContext mappingcontext) {
    String[] components = dar.path.split("\\.");
    Object moveobj = rootobj;
    SAXAccessMethod am = null;
    for (int comp = 0; comp < components.length; ++comp) {
      MethodAnalyser ma = MethodAnalyser.getMethodAnalyser(moveobj, mappingcontext);
      am = ma.getAccessMethod(components[comp]);
      if (comp != components.length - 1) {
        moveobj = am.getChildObject(moveobj);
      }
    }
    Object convert = dar.data;
    if (convert instanceof String) {
      Class leaftype = am.getAccessedType();
      if (mappingcontext.saxleafparser.isLeafType(leaftype)) {
        convert = mappingcontext.saxleafparser.parse(leaftype, (String)dar.data);       
      }
      else {
        // This depends on factoring ResourceClassTable/SaxalXMLProvider.
        throw new UniversalRuntimeException("Have not yet implemented non-leaf setters!");
      }
    }
    
    if (!am.canSet() && am.isEnumeration()) {
      throw new UniversalRuntimeException("Cannot set object for path "+dar.path);
    }
    
    if (am.isDenumerable()) {
      Denumeration den = EnumerationConverter.getDenumeration(moveobj);
      den.add(convert);
    }
    else {
      am.setChildObject(moveobj, convert);
    }
  }
  
  public static void applyAlterations(Object rootobj, DARList toapply, 
      SAXalizerMappingContext mappingcontext) {
    for (int i = 0; i < toapply.size(); ++ i) {
      DataAlterationRequest dar = toapply.DARAt(i);
      applyAlteration(rootobj, dar, mappingcontext);
    }
  }
}
