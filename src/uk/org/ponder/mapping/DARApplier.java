/*
 * Created on Nov 22, 2004
 */
package uk.org.ponder.mapping;

import java.util.logging.Level;

import uk.org.ponder.saxalizer.MethodAnalyser;
import uk.org.ponder.saxalizer.SAXAccessMethod;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;
import uk.org.ponder.saxalizer.XMLProvider;
import uk.org.ponder.stringutil.StringList;
import uk.org.ponder.util.Denumeration;
import uk.org.ponder.util.EnumerationConverter;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
public class DARApplier {

  public static StringList applyAlteration(Object rootobj, DataAlterationRequest dar,
      SAXalizerMappingContext mappingcontext, XMLProvider xmlprovider, StringList messages) {
    String[] components = dar.path.split("\\.");
    Object moveobj = rootobj;
    SAXAccessMethod am = null;
    for (int comp = 0; comp < components.length; ++comp) {
      MethodAnalyser ma = MethodAnalyser.getMethodAnalyser(moveobj,
          mappingcontext);
      am = ma.getAccessMethod(components[comp]);
      if (am == null) {
        throw new UniversalRuntimeException("Access method " + components[comp] + " not found in " + moveobj.getClass());
      }
      if (comp != components.length - 1) {
        moveobj = am.getChildObject(moveobj);
      }
    }
    Object convert = dar.data;
    if (convert instanceof String) {
      Class leaftype = am.getAccessedType();
      if (mappingcontext.saxleafparser.isLeafType(leaftype)) {
        convert = mappingcontext.saxleafparser.parse(leaftype,
            (String) dar.data);
      }
      else {
        convert = xmlprovider.fromString((String)convert);
      }
    }

    if (!am.canSet() && am.isEnumeration()) {
      throw new UniversalRuntimeException("Cannot set object for path "
          + dar.path);
    }
    // Think about interning at some point.
    // also, how to delete non-leaf objects? should probably only really
    // support Maps, otherwise info cannot be serialised well.
    if (dar.type.equals(DataAlterationRequest.DELETE)) {
      if (!am.isDenumerable()) {
        throw new UniversalRuntimeException(
            "Cannot delete from non-denumerable member " + dar.path);
      }
      else {
        Object lastobj = am.getChildObject(moveobj);
        Denumeration den = EnumerationConverter.getDenumeration(lastobj);
        boolean removed = den.remove(convert);
        if (!removed) {messages.add("Couldn't remove object " + convert 
            + " from path " + dar.path);
        }
      }
    }
    else { // it is an ADD or SET request.
      if (am.isDenumerable()) {
        Object lastobj = am.getChildObject(moveobj);
        Denumeration den = EnumerationConverter.getDenumeration(lastobj);
        den.add(convert);
      }
      else {
        am.setChildObject(moveobj, convert);
      }
    }
    return messages;
  }

  public static StringList applyAlterations(Object rootobj, DARList toapply,
      SAXalizerMappingContext mappingcontext, XMLProvider xmlprovider) {
    StringList messages = new StringList();
    for (int i = 0; i < toapply.size(); ++i) {
      DataAlterationRequest dar = toapply.DARAt(i);
      applyAlteration(rootobj, dar, mappingcontext, xmlprovider, messages);
    }
    return messages;
  }
}