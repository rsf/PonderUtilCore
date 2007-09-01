/*
 * Created on 21 Aug 2007
 */
package uk.org.ponder.beanutil.entity;

import java.util.Map;

import uk.org.ponder.beanutil.WriteableBeanLocator;

/**
 * The common interface to the "in code" functionality of an EntityBeanLocator.
 * See @link {@link uk.org.ponder.rsf.state.entity.EntityBeanLocatorImpl}
 * for implementation details.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */

public interface EntityBeanLocator extends WriteableBeanLocator {
  /** A prefix used in for the key of the cached entities, representing a
   * non-persistent entity, freshly created on this cycle.
   */
  public static final String NEW_PREFIX = "new ";
 
  public Map getDeliveredBeans();
}
