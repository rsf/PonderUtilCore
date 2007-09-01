/*
 * Created on 15-Jan-2006
 */
package uk.org.ponder.beanutil.support;

import uk.org.ponder.beanutil.BeanLocator;

/** A BeanLocator that returns no beans */

public class NullBeanLocator implements BeanLocator {
  // NullBeanLocators are stateless, use this instance globally.
  public static BeanLocator instance = new NullBeanLocator();
  public Object locateBean(String path) {
    return null;
  }

}
