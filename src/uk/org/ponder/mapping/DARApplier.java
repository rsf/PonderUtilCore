/*
 * Created on Nov 22, 2004
 */
package uk.org.ponder.mapping;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

import uk.org.ponder.arrayutil.ArrayUtil;
import uk.org.ponder.beanutil.BeanModelAlterer;
import uk.org.ponder.beanutil.BeanResolver;
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
import uk.org.ponder.reflect.ReflectUtils;
import uk.org.ponder.reflect.ReflectiveCache;
import uk.org.ponder.saxalizer.MethodAnalyser;
import uk.org.ponder.saxalizer.SAXAccessMethod;
import uk.org.ponder.saxalizer.SAXalXMLProvider;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;
import uk.org.ponder.stringutil.StringList;
import uk.org.ponder.util.Denumeration;
import uk.org.ponder.util.EnumerationConverter;
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
  private boolean springmode;

  public void setSAXalXMLProvider(SAXalXMLProvider saxal) {
    xmlprovider = saxal;
  }

  public void setMappingContext(SAXalizerMappingContext mappingcontext) {
    this.mappingcontext = mappingcontext;
    vcp = new VectorCapableParser();
    vcp.setScalarParser(mappingcontext.saxleafparser);
  }

  public SAXalizerMappingContext getMappingContext() {
    return mappingcontext;
  }

  public void setReflectiveCache(ReflectiveCache reflectivecache) {
    this.reflectivecache = reflectivecache;
  }

  /**
   * Will enable more aggressive type conversions as appropriate for operating a
   * Spring-style container specified in XML. In particular will convert String
   * values into lists of Strings by splitting at commas, if they are applied to
   * vector-valued beans.
   */
  public void setSpringMode(boolean springmode) {
    this.springmode = springmode;
  }

  public Object getFlattenedValue(String fullpath, Object root,
      Class targetclass, BeanResolver resolver) {
    Object toconvert = getBeanValue(fullpath, root);
    if (toconvert == null)
      return null;
    if (targetclass == null) {
      targetclass = EnumerationConverter.isEnumerable(toconvert.getClass())?
          ArrayUtil.stringArrayClass : String.class;
    }
    if (targetclass == String.class || targetclass == Boolean.class) {
      // TODO: We need proper vector support
      if (toconvert instanceof String[]) {
        toconvert = ((String[]) toconvert)[0];
      }
      String rendered = resolver == null ? mappingcontext.saxleafparser
          .render(toconvert)
          : resolver.resolveBean(toconvert);
      return targetclass == String.class ? rendered
          : mappingcontext.saxleafparser.parse(Boolean.class, rendered);
    }
    else {
      // this is inverse to the "vector" setBeanValue branch below
      Object target = ReflectUtils.instantiateContainer(ArrayUtil.stringArrayClass,
          EnumerationConverter.getEnumerableSize(toconvert),
          reflectivecache);
      vcp.render(toconvert, target, resolver, reflectivecache);
      return target;
    }
  }

  public Object getBeanValue(String fullpath, Object rbl) {
    try {
      Object togo = BeanUtil.navigate(rbl, fullpath, mappingcontext);
      return togo;
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e,
          "Error getting bean value for path " + fullpath);
    }
  }

  // a convenience method to have the effect of a "set" ValueBinding,
  // constructs a mini-DAR just for setting. Errors will be accumulated
  // into the supplied error list.
  public void setBeanValue(String fullpath, Object root, Object value,
      TargettedMessageList messages) {
    DataAlterationRequest dar = new DataAlterationRequest(fullpath, value);
    // messages.pushNestedPath(headpath);
    // try {
    applyAlteration(root, dar, messages);
    // }
    // finally {
    // messages.popNestedPath();
    // }
  }

  public Object invokeBeanMethod(String fullpath, Object rbl) {
    String totail = PathUtil.getToTailPath(fullpath);
    String method = PathUtil.getTailPath(fullpath);
    try {
      Object bean = BeanUtil.navigate(rbl, totail, mappingcontext);
      return reflectivecache.invokeMethod(bean, method);
    }
    catch (Throwable t) { // Need to grab "NoSuchMethodError"
      throw UniversalRuntimeException.accumulate(t, "Error invoking method "
          + method + " in bean at path " + totail);
    }
  }
  
  private void applyAlteration(Object rootobj, DataAlterationRequest dar,
      TargettedMessageList messages) {
    Logger.log.debug("Applying DAR " + dar.type + " to path " + dar.path + ": "
        + dar.data);
    String oldpath = dar.path;
    try {
      Object moveobj = rootobj;
      String tail = null;
      
      while (true) {
        String headpath = PathUtil.getHeadPath(dar.path);
        if (headpath.equals(dar.path)) {
          tail = headpath;
          break;
        }
        moveobj = BeanUtil.navigate(moveobj, headpath, mappingcontext);
        dar.path = PathUtil.getFromHeadPath(dar.path);
        if (moveobj instanceof DARReceiver) {
          boolean accepted = ((DARReceiver) moveobj)
              .addDataAlterationRequest(dar);
          if (accepted) return;
        }
      }
      dar.path = oldpath;
      Object convert = dar.data;

      PropertyAccessor pa = MethodAnalyser.getPropertyAccessor(moveobj,
          mappingcontext);
      Class leaftype = pa.getPropertyType(tail);
      if (dar.type.equals(DataAlterationRequest.ADD)) {
        // If we got a list of Strings in from
        // the UI, they may be "cryptic" leaf types without proper packaging.
        // This
        // implies we MUST know the element type of the collection.
        // For now we must assume collection is of leaf types.
        if (pa.isMultiple(tail)) {
          Object lastobj = pa.getProperty(moveobj, tail);

          SAXAccessMethod sam = mappingcontext.getAnalyser(moveobj.getClass())
              .getAccessMethod(tail);
          if (convert instanceof String && springmode) {
            // deference to Spring "auto-convert from comma-separated list"
            // NB this is currently disused, RSACBeanLocator does not use
            // DARApplier yet.
            convert = StringList.fromString((String) convert);
          }
          int incomingsize = EnumerationConverter.getEnumerableSize(convert); 
          if (lastobj == null || lastobj.getClass().isArray() && 
              EnumerationConverter.getEnumerableSize(lastobj) != incomingsize) {
            lastobj = ReflectUtils.instantiateContainer(sam.getDeclaredType(),
                incomingsize,
                reflectivecache);
            pa.setProperty(moveobj, tail, lastobj);
          }
          if (VectorCapableParser.isLOSType(convert)) {
            if (lastobj instanceof Collection) {
              ((Collection) lastobj).clear();
            }
            // TODO: for JDK collections, "leaftype" will be equal to the
            // collection type unless we have got type info from elsewhere.
            // for now, use arrays.
            vcp.parse(convert, lastobj, leaftype, reflectivecache);
          }
          else { // must be a single item, or else a collection
            Denumeration den = EnumerationConverter.getDenumeration(lastobj,
                reflectivecache);
            // TODO: use CompletableDenumeration here to support extensible arrays.
            if (EnumerationConverter.isEnumerable(convert.getClass())) {
              for (Enumeration enumm = EnumerationConverter
                  .getEnumeration(convert); enumm.hasMoreElements();) {
                den.add(enumm.nextElement());
              }
            }
            else {
              den.add(convert);
            }
          }
        }
        else { // property is a scalar type, possibly composite.
          if (convert instanceof String[]) {
            convert = ((String[]) convert)[0];
          }
          // Step 1 - attempt to convert the dar value if it is still a String,
          // using our now knowledge of the target leaf type.
          if (convert instanceof String) {
            String string = (String) convert;
              convert = ConvertUtil.parse(string, xmlprovider, leaftype);
          }
          // this case also deals with Maps and WBLs.
          pa.setProperty(moveobj, tail, convert);
        }
      }
      // at this point, moveobj contains the object BEFORE the final path
      // section.

      else if (dar.type.equals(DataAlterationRequest.DELETE)) {
        try {
          boolean removed = false;
          // if we have data, we can try to remove it by value
          if (convert != null) {
            // copied code from "ADD" branch. Regularise this conversion at some point.
            if (convert instanceof String) {
              String string = (String) convert;
                convert = ConvertUtil.parse(string, xmlprovider, leaftype);
            }
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
          if (messages != null) {
            TargettedMessage message = new TargettedMessage(
                CoreMessages.MISSING_DATA_ERROR, dar.path);
            messages.addMessage(message);
          }
          Logger.log.warn("Couldn't remove object " + convert + " from path "
              + dar.path, e);
        }
      }
    }
    catch (Exception e) {
      if (messages != null) {
        TargettedMessage message = new TargettedMessage(e.getMessage(), e, oldpath);
        messages.addMessage(message);
      }
      Logger.log.warn("Error applying value " + dar.data + " to path "
          + dar.path, e);
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