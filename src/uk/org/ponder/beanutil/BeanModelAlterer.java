/*
 * Created on Nov 4, 2005
 */
package uk.org.ponder.beanutil;

import uk.org.ponder.errorutil.TargettedMessageList;
import uk.org.ponder.mapping.DARList;

/** The base interface for RSF's expression language (EL) */

public interface BeanModelAlterer {

  public Object getBeanValue(String fullpath, BeanLocator rbl);

  public void setBeanValue(String fullpath, BeanLocator rbl, Object value);

  public Object invokeBeanMethod(String fullpath, BeanLocator rbl);

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
      TargettedMessageList messages);

}