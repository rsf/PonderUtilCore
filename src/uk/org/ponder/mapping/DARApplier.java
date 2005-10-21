/*
 * Created on Nov 22, 2004
 */
package uk.org.ponder.mapping;

import uk.org.ponder.beanutil.BeanUtil;
import uk.org.ponder.beanutil.PathUtil;
import uk.org.ponder.beanutil.RootBeanLocator;
import uk.org.ponder.errorutil.CoreMessages;
import uk.org.ponder.errorutil.TargettedMessage;
import uk.org.ponder.errorutil.TargettedMessageList;
import uk.org.ponder.errorutil.ThreadErrorState;
import uk.org.ponder.saxalizer.MethodAnalyser;
import uk.org.ponder.saxalizer.SAXAccessMethod;
import uk.org.ponder.saxalizer.SAXalXMLProvider;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;
import uk.org.ponder.saxalizer.XMLProvider;
import uk.org.ponder.util.Denumeration;
import uk.org.ponder.util.EnumerationConverter;
import uk.org.ponder.util.Logger;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
public class DARApplier {
  private XMLProvider xmlprovider;
  private SAXalizerMappingContext mappingcontext;

  public void setSAXalXMLProvider(SAXalXMLProvider saxal) {
    xmlprovider = saxal;
    mappingcontext = saxal.getMappingContext();
  }

  public static SAXAccessMethod getAMExpected(Object target, String methodname,
      SAXalizerMappingContext mappingcontext) {
    MethodAnalyser ma = MethodAnalyser
        .getMethodAnalyser(target, mappingcontext);
    SAXAccessMethod am = ma.getAccessMethod(methodname);
    if (am == null) {
      throw new UniversalRuntimeException("Access method " + methodname
          + " not found in " + target.getClass());
    }
    return am;
  }

  // a convenience method to have the effect of a "set" ValueBinding,
  // constructs a mini-DAR just for setting. Errors will be accumulated
  // into ThreadErrorState
  // NB there are two calls in the workspace, both from PostHandler.applyValues.
  // Should we really try to do away with the ThreadErrorState?
  public void setBeanValue(String fullpath, RootBeanLocator rbl, Object value) {
    String restpath = PathUtil.getFromHeadPath(fullpath);
    String headpath = PathUtil.getHeadPath(fullpath);
    Object rootbean = rbl.locateRootBean(headpath);
    DataAlterationRequest dar = new DataAlterationRequest(restpath, value);
    TargettedMessageList messages = ThreadErrorState.getErrorState().errors;
    messages.pushNestedPath(headpath);
    try {
      applyAlteration(rootbean, dar, messages);
    }
    finally {
      messages.popNestedPath();
    }
  }

  private void applyAlteration(Object rootobj,
      DataAlterationRequest dar, TargettedMessageList messages) {
    String totail = PathUtil.getToTailPath(dar.path);
    Object moveobj = BeanUtil.navigate(rootobj, totail, mappingcontext);
    Object convert = dar.data;
    String tail = PathUtil.getTailPath(dar.path);
    SAXAccessMethod am = getAMExpected(moveobj, tail, mappingcontext);
    if (convert instanceof String) {
      try {
        messages.pushNestedPath(totail);
        Class leaftype = am.getAccessedType();
        if (mappingcontext.saxleafparser.isLeafType(leaftype)) {
          convert = mappingcontext.saxleafparser.parse(leaftype,
              (String) dar.data);
        }
        // TODO: parse EL references and resolve them here.
        else {
          // TODO: catch conversion errors by putting messages into TES.
          convert = xmlprovider.fromString((String) convert);
        }
      }
      finally {
        messages.popNestedPath();
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
        if (!removed) {
          TargettedMessage message = new TargettedMessage(
              CoreMessages.MISSING_DATA_ERROR, dar.path);
          messages.addMessage(message);
          Logger.log.warn("Couldn't remove object " + convert + " from path "
              + dar.path);
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
  }

  /** Apply the alterations mentioned in the enclosed DARList to the supplied
   * bean. Note that this method assumes that the TargettedMessageList is
   *  already navigated to the root path referred to by the
   * bean, and that the DARList mentions paths relative to that bean.
   * @param rootobj The object to which alterations are to be applied
   * @param toapply The list of alterations
   * @param messages The list to which error messages accreted during application
   * are to be appended. This is probably the same as that in the ThreadErrorState,
   * but is supplied as an argument to reduce costs of ThreadLocal gets.
   */
  public void applyAlterations(Object rootobj, DARList toapply, 
      TargettedMessageList messages) {
    for (int i = 0; i < toapply.size(); ++i) {
      DataAlterationRequest dar = toapply.DARAt(i);
      applyAlteration(rootobj, dar, messages);
    }
  }
}