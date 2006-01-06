/*
 * Created on 06-Jan-2006
 */
package uk.org.ponder.beanutil;

import java.util.Iterator;

public interface IterableBeanLocator extends BeanLocator {
  /** Returns an iteration of the *names* of the beans in this BeanLocator */
  public Iterator iterator();
}
