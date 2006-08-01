/*
 * Created on Nov 4, 2005
 */
package uk.org.ponder.beanutil;

import uk.org.ponder.errorutil.TargettedMessageList;
import uk.org.ponder.mapping.BeanInvalidationIterator;
import uk.org.ponder.mapping.DARList;
import uk.org.ponder.mapping.DataAlterationRequest;

/** The base interface for RSF's expression language (EL) */

public interface BeanModelAlterer {

  public Object getBeanValue(String fullpath, Object root);

  public void setBeanValue(String fullpath, Object root, Object value,
      TargettedMessageList messages);

  public Object invokeBeanMethod(String fullpath, Object root);

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
      TargettedMessageList messages, BeanInvalidationIterator bii);

  /** @see #applyAlterations(Object, DARList, TargettedMessageList) * */
  public void applyAlteration(Object rootobj, DataAlterationRequest dar,
      TargettedMessageList messages, BeanInvalidationIterator bii);

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