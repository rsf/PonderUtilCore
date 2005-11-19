/*
 * Created on Nov 22, 2004
 */
package uk.org.ponder.mapping;

import java.util.Collection;
import java.util.Map;

import uk.org.ponder.beanutil.BeanLocator;
import uk.org.ponder.beanutil.BeanModelAlterer;
import uk.org.ponder.beanutil.BeanUtil;
import uk.org.ponder.beanutil.PathUtil;
import uk.org.ponder.beanutil.PropertyAccessor;
import uk.org.ponder.beanutil.WriteableBeanLocator;
import uk.org.ponder.conversion.ConvertUtil;
import uk.org.ponder.conversion.VectorCapableParser;
import uk.org.ponder.errorutil.CoreMessages;
import uk.org.ponder.errorutil.PropertyException;
import uk.org.ponder.errorutil.TargettedMessage;
import uk.org.ponder.errorutil.TargettedMessageList;
import uk.org.ponder.errorutil.ThreadErrorState;
import uk.org.ponder.reflect.ReflectiveCache;
import uk.org.ponder.saxalizer.MethodAnalyser;
import uk.org.ponder.saxalizer.SAXAccessMethod;
import uk.org.ponder.saxalizer.SAXalXMLProvider;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;
import uk.org.ponder.util.Logger;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class DARApplier implements BeanModelAlterer {
  private SAXalXMLProvider xmlprovider;
  private SAXalizerMappingContext mappingcontext;
  private VectorCapableParser vcp;
  private ReflectiveCache reflectivecache;

  public void setSAXalXMLProvider(SAXalXMLProvider saxal) {
    xmlprovider = saxal;
    mappingcontext = saxal.getMappingContext();
    vcp = new VectorCapableParser();
    vcp.setScalarParser(mappingcontext.saxleafparser);
  }
  
  public void setReflectiveCache(ReflectiveCache reflectivecache) {
    this.reflectivecache = reflectivecache;
  }

  public Object getBeanValue(String fullpath, BeanLocator rbl) {
    Object togo = BeanUtil.navigate(rbl, fullpath, mappingcontext);
    return togo;
  }
  
  public Object getFlattenedValue(String fullpath, BeanLocator rbl, Class targetclass) {
    Object toconvert = getBeanValue(fullpath, rbl);
    if (targetclass == String.class || targetclass == Boolean.class) {
      String rendered = mappingcontext.saxleafparser.render(toconvert);
      return targetclass == String.class? rendered : mappingcontext.saxleafparser.parse(Boolean.class, rendered);
    }
    // It MUST be String[], AND the value must be a container.
    // this had BETTER be a Container otherwise we will fail to update the value
    // in setBeanValue below.
    Collection collection = (Collection) toconvert;
    String[] target = new String[collection.size()];
    vcp.render(collection, target);
    return target;
  }
  
  // a convenience method to have the effect of a "set" ValueBinding,
  // constructs a mini-DAR just for setting. Errors will be accumulated
  // into ThreadErrorState
  // NB there are two calls in the workspace, both from PostHandler.applyValues.
  // Should we really try to do away with the ThreadErrorState?
  public void setBeanValue(String fullpath, BeanLocator rbl, Object value) {
    // String restpath = PathUtil.getFromHeadPath(fullpath);
    // String headpath = PathUtil.getHeadPath(fullpath);
    // Object rootbean = rbl.locateBean(headpath);
    DataAlterationRequest dar = new DataAlterationRequest(fullpath, value);
    TargettedMessageList messages = ThreadErrorState.getErrorState().errors;
    // messages.pushNestedPath(headpath);
    try {
      applyAlteration(rbl, dar, messages);
    }
    finally {
      messages.popNestedPath();
    }
  }

  public Object invokeBeanMethod(String fullpath, BeanLocator rbl) {
    String totail = PathUtil.getToTailPath(fullpath);
    String method = PathUtil.getTailPath(fullpath);
    try {
      Object bean = BeanUtil.navigate(rbl, totail, mappingcontext);
      return reflectivecache.invokeMethod(bean, method);
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e, "Error invoking method "
          + method + " in bean at path " + totail);
    }
  }

  private void applyAlteration(Object rootobj, DataAlterationRequest dar,
      TargettedMessageList messages) {
    String totail = PathUtil.getToTailPath(dar.path);
    // TODO: Pause at any DARReceiver we discover in the model and instead
    // queue the requests there.
    Object moveobj = BeanUtil.navigate(rootobj, totail, mappingcontext);
    Object convert = dar.data;
    String tail = PathUtil.getTailPath(dar.path);
    PropertyAccessor pa = MethodAnalyser.getPropertyAccessor(moveobj,
        mappingcontext);
    Class leaftype = pa.getPropertyType(tail);
    // Step 1 - attempt to convert the dar value if it is still a String, using
    // our now knowledge of the target leaf type.
    if (convert instanceof String) {
      String string = (String) convert;
      try {
        messages.pushNestedPath(totail);
        ConvertUtil.parse(string, xmlprovider, leaftype);
      }
      finally {
        messages.popNestedPath();
      }
    }
    // at this point, moveobj contains the object BEFORE the final path
    // section.

    if (dar.type.equals(DataAlterationRequest.DELETE)) {
      try {
        boolean removed = false;
        // if we have data, we can try to remove it by value
        if (convert != null) {
          Object lastobj = pa.getProperty(moveobj, tail);
          if (lastobj instanceof Collection) {
            removed = ((Collection) lastobj).remove(convert);
          }
          else if (lastobj instanceof Map) {
            removed = ((Map) moveobj).remove(convert) != null;
          }
        }
        else { // there is no data, so it must be a Map, concrete or
          // WriteableBeanLocator.
          if (moveobj instanceof Map) {
            removed = ((Map) moveobj).remove(tail) != null;
          }
          else if (moveobj instanceof WriteableBeanLocator) {
            removed = ((WriteableBeanLocator) moveobj).remove(tail);
          }
          else {
            pa.setProperty(moveobj, tail, null);
          }
        }
        if (!removed) {
          throw UniversalRuntimeException.accumulate(new PropertyException());
        }
      }
      catch (Exception e) {
        TargettedMessage message = new TargettedMessage(
            CoreMessages.MISSING_DATA_ERROR, dar.path);
        messages.addMessage(message);
        Logger.log.warn("Couldn't remove object " + convert + " from path "
            + dar.path);
      }

    }
    else { // it is an ADD or SET request.
      // If we got a list of Strings in from
      // the UI, they may be "cryptic" leaf types without proper packaging. This
      // implies we MUST know the element type of the collection.
      if (pa.isMultiple(tail)) {
        // it had BETTER be a collection otherwise delivery semantics will be
        // incoherent - we HAVE to be able to clear it.
        Collection lastobj = (Collection) pa.getProperty(moveobj, tail);
        SAXAccessMethod sam = MethodAnalyser.getMethodAnalyser(moveobj, mappingcontext).getAccessMethod(tail);
        if (VectorCapableParser.isLOSType(convert)) {
          lastobj.clear();
          vcp.parse(convert, lastobj, sam.getAccessedType());
        }
        lastobj.add(convert);
      }
      else {
        // this case also deals with Maps and WBLs.
        pa.setProperty(moveobj, tail, convert);
      }
    }
  }

  /**
   * Apply the alterations mentioned in the enclosed DARList to the supplied
   * bean. Note that this method assumes that the TargettedMessageList is
   * already navigated to the root path referred to by the bean, and that the
   * DARList mentions paths relative to that bean.
   * 
   * @param rootobj
   *          The object to which alterations are to be applied
   * @param toapply
   *          The list of alterations
   * @param messages
   *          The list to which error messages accreted during application are
   *          to be appended. This is probably the same as that in the
   *          ThreadErrorState, but is supplied as an argument to reduce costs
   *          of ThreadLocal gets.
   */
  public void applyAlterations(Object rootobj, DARList toapply,
      TargettedMessageList messages) {
    for (int i = 0; i < toapply.size(); ++i) {
      DataAlterationRequest dar = toapply.DARAt(i);
      applyAlteration(rootobj, dar, messages);
    }
  }

}