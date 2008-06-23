/*
 * Created on Nov 22, 2004
 */
package uk.org.ponder.mapping.support;

import java.util.ArrayList;
import java.util.List;

import uk.org.ponder.arrayutil.ArrayUtil;
import uk.org.ponder.beanutil.BeanModelAlterer;
import uk.org.ponder.beanutil.BeanPredicateModel;
import uk.org.ponder.beanutil.BeanResolver;
import uk.org.ponder.beanutil.BeanUtil;
import uk.org.ponder.beanutil.CoreELReference;
import uk.org.ponder.beanutil.PathUtil;
import uk.org.ponder.beanutil.PropertyAccessor;
import uk.org.ponder.conversion.GeneralConverter;
import uk.org.ponder.conversion.VectorCapableParser;
import uk.org.ponder.errorutil.CoreMessages;
import uk.org.ponder.iterationutil.EnumerationConverter;
import uk.org.ponder.mapping.BeanInvalidationBracketer;
import uk.org.ponder.mapping.DAREnvironment;
import uk.org.ponder.mapping.DARList;
import uk.org.ponder.mapping.DARReceiver;
import uk.org.ponder.mapping.DataAlterationRequest;
import uk.org.ponder.mapping.ShellInfo;
import uk.org.ponder.messageutil.TargettedMessage;
import uk.org.ponder.messageutil.TargettedMessageException;
import uk.org.ponder.messageutil.TargettedMessageList;
import uk.org.ponder.reflect.ReflectUtils;
import uk.org.ponder.reflect.ReflectiveCache;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;
import uk.org.ponder.saxalizer.support.MethodAnalyser;
import uk.org.ponder.util.Logger;
import uk.org.ponder.util.ObjectFactory;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * The core "EL engine". Will apply a "DataAlterationRequest" to an arbitrary bean target.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class DARApplier implements BeanModelAlterer {
  private SAXalizerMappingContext mappingcontext;
  private VectorCapableParser vcp;
  private ReflectiveCache reflectivecache;
  private DARApplierImpl impl = new DARApplierImpl();

  public void setGeneralConverter(GeneralConverter generalConverter) {
    impl.setGeneralConverter(generalConverter);
  }

  public void setMappingContext(SAXalizerMappingContext mappingcontext) {
    this.mappingcontext = mappingcontext;
    impl.setMappingContext(mappingcontext);
  }

  public SAXalizerMappingContext getMappingContext() {
    return mappingcontext;
  }

  public void setReflectiveCache(ReflectiveCache reflectivecache) {
    this.reflectivecache = reflectivecache;
    impl.setReflectiveCache(reflectivecache);
  }

  public void setVectorCapableParser(VectorCapableParser vcp) {
    this.vcp = vcp;
    impl.setVectorCapableParser(vcp);
  }

  /**
   * Will enable more aggressive type conversions as appropriate for operating a
   * Spring-style container specified in XML. In particular will convert String values
   * into lists of Strings by splitting at commas, if they are applied to vector-valued
   * beans.
   */
  public void setSpringMode(boolean springmode) {
    impl.setSpringMode(springmode);
  }

  public Object getFlattenedValue(String fullpath, Object root, Class targetclass,
      BeanResolver resolver) {
    Object toconvert = getBeanValue(fullpath, root, null);
    if (toconvert == null)
      return null;
    if (targetclass == null) {
      targetclass = EnumerationConverter.isEnumerable(toconvert.getClass()) ? ArrayUtil.stringArrayClass
          : String.class;
    }
    if (targetclass == String.class || targetclass == Boolean.class) {
      // TODO: We need proper vector support
      if (toconvert instanceof String[]) {
        toconvert = ((String[]) toconvert)[0];
      }
      String rendered = resolver == null ? mappingcontext.generalLeafParser
          .render(toconvert)
          : resolver.resolveBean(toconvert);
      return targetclass == String.class ? rendered
          : mappingcontext.generalLeafParser.parse(Boolean.class, rendered);
    }
    else {
      // this is inverse to the "vector" setBeanValue branch below
      Object target = ReflectUtils.instantiateContainer(ArrayUtil.stringArrayClass,
          EnumerationConverter.getEnumerableSize(toconvert), reflectivecache);
      vcp.render(toconvert, target, resolver);
      return target;
    }
  }

  private void checkAccess(String fullpath, BeanPredicateModel addressibleModel,
      String key) {
    if (addressibleModel != null && !addressibleModel.isMatch(fullpath)) {
      throw UniversalRuntimeException
          .accumulate(
              new SecurityException(),
              key
                  + " path "
                  + fullpath
                  + " is not permissible - make sure to mark this path as request addressible - http://www2.caret.cam.ac.uk/rsfwiki/Wiki.jsp?page=RequestWriteableBean");
    }
  }

  public Object getBeanValue(String fullpath, Object rbl,
      BeanPredicateModel addressibleModel) {
    try {
      checkAccess(fullpath, addressibleModel, "Reading from");
      Object togo = BeanUtil.navigate(rbl, fullpath, mappingcontext);
      return togo;
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e, "Error getting bean value for path "
          + fullpath);
    }
  }

  // a convenience method to have the effect of a "set" ValueBinding,
  // constructs a mini-DAR just for setting. Errors will be accumulated
  // into the supplied error list.
  public void setBeanValue(String fullpath, Object root, Object value,
      TargettedMessageList messages, boolean applyconversions) {
    DataAlterationRequest dar = new DataAlterationRequest(fullpath, value);
    dar.applyconversions = applyconversions;
    // messages.pushNestedPath(headpath);
    // try {
    DAREnvironment darenv = messages == null ? null
        : new DAREnvironment(messages);
    applyAlteration(root, dar, darenv);
    // }
    // finally {
    // messages.popNestedPath();
    // }
  }

  private Object fetchArgument(Object root, String string,
      BeanPredicateModel addressibleModel) {
    int len = string.length();
    if (len >= 2 && string.charAt(0) == '\'' && string.charAt(len - 1) == '\'') {
      return string.substring(1, len - 1);
    }
    return len == 0 ? null
        : getBeanValue(string, root, addressibleModel);
  }

  // 0 1 2
  // segments: bean.method.arg = 3
  // shells: rbl (bean) = 2 = lastshell
  public Object invokeBeanMethod(ShellInfo shells, BeanPredicateModel addressibleModel) {
    int lastshell = shells.shells.length;
    Object[] args = new Object[shells.segments.length - lastshell];
    for (int i = 0; i < args.length; ++i) {
      args[i] = fetchArgument(shells.shells[0], shells.segments[i + lastshell],
          addressibleModel);
    }
    Object bean = shells.shells[lastshell - 1];
    String methodname = shells.segments[lastshell - 1];
    try {
      return reflectivecache.invokeMethod(bean, methodname, args);
    }
    catch (Throwable t) { // Need to grab "NoSuchMethodError"
      throw UniversalRuntimeException.accumulate(t, "Error invoking method " + methodname
          + " in bean at path " + PathUtil.buildPath(shells.segments, 0, lastshell));
    }
  }

  private void applyAlterationImpl(final Object moveobj, final String tail,
      final DataAlterationRequest dar, final DAREnvironment darenv) {
    final PropertyAccessor pa = MethodAnalyser.getPropertyAccessor(moveobj,
        mappingcontext);
    BeanInvalidationBracketer bib = darenv == null || darenv.bib == null ? NullBeanInvalidationBracketer.instance
        : darenv.bib;

    bib.invalidate(dar.path, new Runnable() {
      public void run() {
        Object convert = dar.data;
        if (convert == DataAlterationRequest.INAPPLICABLE_VALUE)
          return;
        while (convert instanceof ObjectFactory) {
          convert = ((ObjectFactory) convert).getObject();
        }
        Class leaftype = pa.getPropertyType(moveobj, tail);

        DARApplyEnvironment daraenv = new DARApplyEnvironment(dar, darenv, moveobj,
            convert, tail, pa, leaftype);

        if (dar.type.equals(DataAlterationRequest.ADD)) {
          impl.processAddition(daraenv);
        }
        else if (dar.type.equals(DataAlterationRequest.DELETE)) {
          impl.processDeletion(daraenv);
        }
      }
    });
  }

  public ShellInfo fetchShells(String fullpath, Object rootobj, boolean expectMethod) {
    Object moveobj = rootobj;
    List shells = new ArrayList();
    shells.add(rootobj);
    String[] segments = PathUtil.splitPath(fullpath);
    for (int i = 0; i < segments.length; ++i) {
      if (expectMethod) {
        if (ReflectUtils.hasMethod(moveobj, segments[i]))
          break;
      }
      moveobj = BeanUtil.navigateOne(moveobj, segments[i], mappingcontext);
      if (moveobj == null || moveobj == BeanUtil.UNREADABLE_PROPERTY) {
        break;
      }
      shells.add(moveobj);
      if (moveobj instanceof DARReceiver) {
        break;
      }
    }
    ShellInfo togo = new ShellInfo();
    togo.segments = segments;
    togo.shells = shells.toArray();
    return togo;
  }

  public void applyAlteration(final Object rootobj, final DataAlterationRequest dar,
      final DAREnvironment darenv) {
    Logger.log.debug("Applying DAR " + dar.type + " to path " + dar.path + ": "
        + dar.data);
    checkAccess(dar.path, darenv == null ? null
        : darenv.addressibleModel, "Writing to");
    if (dar.data instanceof CoreELReference) {
      final CoreELReference elref = (CoreELReference) dar.data;
      dar.data = new ObjectFactory() {
        public Object getObject() {
          return getBeanValue(elref.value, rootobj, darenv.addressibleModel);
        }
      };
    }
    String oldpath = dar.path;
    try {
      // Do not check for receivers if this is an interceptor-only trigger
      if (dar.data != DataAlterationRequest.INAPPLICABLE_VALUE) {
        Object moveobj = rootobj;
        String[] segments = PathUtil.splitPath(oldpath);
        for (int i = 0; i < segments.length - 1; ++i) {
          moveobj = BeanUtil.navigateOne(moveobj, segments[i], mappingcontext);
          if (moveobj == null) {
            throw new NullPointerException("Null value in EL path at path '"
                + PathUtil.buildPath(segments, 0, i + 1) + "'");
          }
          if (moveobj instanceof DARReceiver) {
            dar.path = PathUtil.buildPath(segments, i + 1, segments.length);
            boolean accepted = ((DARReceiver) moveobj).addDataAlterationRequest(dar);
            if (accepted)
              return;
            else
              dar.path = oldpath;
          }
        }
        applyAlterationImpl(moveobj, segments[segments.length - 1], dar, darenv);
      }
      else {
        applyAlterationImpl(rootobj, dar.path, dar, darenv);
      }

    }
    catch (Exception e) {
      String emessage = "Error applying value " + dar.data + " to path " + dar.path;
      if (darenv != null) {
        if (darenv.writeDepends != null) {
          List depends = (List) darenv.writeDepends.get(dar.path);
          if (depends != null) {
            for (int i = 0; i < depends.size(); ++i) {
              darenv.cancelSet.add(depends.get(i));
            }
          }
        }
        Throwable wrapped = e;
        if (e instanceof UniversalRuntimeException) {
          Throwable target = ((UniversalRuntimeException) e).getTargetException();
          if (target != null)
            wrapped = target;
        }
        if (darenv != null && darenv.messages != null) {
          TargettedMessage message;
          if (wrapped instanceof TargettedMessageException) {
            message = ((TargettedMessageException) wrapped).getTargettedMessage();
            if (message.targetid == null
                || message.targetid == TargettedMessage.TARGET_NONE) {
              message.targetid = oldpath;
            }
          }
          else {
            message = new TargettedMessage(CoreMessages.RAW_EXCEPTION_PLACEHOLDER,
                new Object[] { dar.data }, e, oldpath);
          }
          darenv.messages.addMessage(message);
        }
        Logger.log.info(emessage, e);
      }
      else
        throw UniversalRuntimeException.accumulate(e, emessage);
    }
  }

  /**
   * Apply the alterations mentioned in the enclosed DARList to the supplied bean. Note
   * that this method assumes that the TargettedMessageList is already navigated to the
   * root path referred to by the bean, and that the DARList mentions paths relative to
   * that bean.
   * 
   * @param rootobj The object to which alterations are to be applied
   * @param toapply The list of alterations
   * @param messages The list to which error messages accreted during application are to
   *            be appended. This is probably the same as that in the ThreadErrorState,
   *            but is supplied as an argument to reduce costs of ThreadLocal gets.
   */
  public void applyAlterations(Object rootobj, DARList toapply, DAREnvironment darenv) {
    for (int i = 0; i < toapply.size(); ++i) {
      DataAlterationRequest dar = toapply.DARAt(i);
      if (darenv == null || !darenv.cancelSet.contains(dar.path)) {
        applyAlteration(rootobj, dar, darenv);
      }
    }

  }

  public Object invokeBeanMethod(String methodEL, Object rootobj) {
    ShellInfo shells = fetchShells(methodEL, rootobj, true);
    return invokeBeanMethod(shells, null);
  }

}