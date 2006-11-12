/*
 * Created on 25 Jul 2006
 */
package uk.org.ponder.mapping;

public interface BeanInvalidationBracketer {
  public void invalidate(String path, Runnable toinvoke);
}
