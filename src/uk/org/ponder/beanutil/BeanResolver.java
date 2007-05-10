/*
 * Created on 13-Jan-2006
 */
package uk.org.ponder.beanutil;

/** Resolves a bean (object) onto a textual representation.
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */

public interface BeanResolver {
  public String resolveBean(Object bean);
}
