/*
 * Created on Nov 4, 2005
 */
package uk.org.ponder.beanutil;

import uk.org.ponder.mapping.DAREnvironment;
import uk.org.ponder.mapping.DARList;
import uk.org.ponder.mapping.DataAlterationRequest;
import uk.org.ponder.mapping.ShellInfo;
import uk.org.ponder.messageutil.TargettedMessageList;

/** The base interface for RSF's expression language (EL) */

public interface BeanModelAlterer {

  /** Fetch a bean value by path from the supplied object root.
   * @param fullpath The path of the bean to fetch
   * @param root The root object which the path is relative to
   * @param addressibleModel A model expressing permissible paths. May be <code>null</code>
   * if the path requires no checking.
   */
  public Object getBeanValue(String fullpath, Object root, BeanPredicateModel addressibleModel);

  public void setBeanValue(String fullpath, Object root, Object value,
      TargettedMessageList messages, boolean applyconversion);

  /** Invoke a bean method from a path in the model. 
   * @param shells An encoding of the method to be invoked, assumed to be fetched from
   * {@link #fetchShells(String, Object)} -  the section of the path
   * referring to bean paths has terminated at the last bean present in the
   * {@link ShellInfo#shells} array. The next path segment is assumed to be
   * the method name, and the final section is filled with arguments.
   * @param addressibleModel A model for the beans which are permissible to be
   * addressed for this invocation. May be <code>null</code> if no security
   * checks are required. 
   */
  public Object invokeBeanMethod(ShellInfo shells, BeanPredicateModel addressibleModel);

  /** Invoke a bean method from a path in the model. */
  public Object invokeBeanMethod(String methodEL, Object rootobj);
  
  /**
   * Apply the alterations mentioned in the enclosed DARList to the supplied
   * bean. Note that this method assumes that the TargettedMessageList is
   * already navigated to the root path referred to by the bean, and that the
   * DARList mentions paths relative to that bean.
   * 
   * @param rootobj The object to which alterations are to be applied
   * @param toapply The list of alterations
   * @param messages The list to which error messages accreted during
   *          application are to be appended.
   */
  public void applyAlterations(Object rootobj, DARList toapply,
      DAREnvironment darenv);

  /** @see #applyAlterations(Object, DARList, TargettedMessageList) * */
  public void applyAlteration(Object rootobj, DataAlterationRequest dar,
      DAREnvironment darenv);
  
  /** Fetch the "shells" for the specified path through the bean model, which
   * is an array of Object, one for each path segment in the supplied path. The
   * array will quietly terminate at the first bean which is either <code>null</code> or
   * a DARApplier. If <code>expectMethod</code> is true, it will also stop at
   * any segment which is an exact match for a method name, or a MethodInvokingProxy.
   */
  public ShellInfo fetchShells(String fullpath, Object rootobj, boolean expectMethod);
  
  /**
   * Converts the object currently present at the supplied bean path into the
   * specified target class, which must be one of the classes handled by the
   * UIType framework.
   * 
   * @param fullpath The full EL path from the root object from which the
   *          flattened value is to be fetched.
   * @param root The root object
   * @param targetclazz A class of one of the UIType types, or else
   *          <code>null</code>. If <code>null</code>, the type of the
   *          returned object will be either String[] or String, depending on
   *          whether the path holds an enumerable (vectorial) value or not.
   * @param resolver A resolver which will be applied to each scalar value to
   *          convert it to String if necessary. If <code>null</code>,
   *          default leaf conversion will be applied.
   */

  public Object getFlattenedValue(String fullpath, Object root,
      Class targetclazz, BeanResolver resolver);
}