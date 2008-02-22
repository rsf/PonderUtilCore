/*
 * Created on 15 Dec 2006
 */
package uk.org.ponder.mapping.support;

import uk.org.ponder.mapping.BeanInvalidationBracketer;

public class NullBeanInvalidationBracketer implements BeanInvalidationBracketer {
  public static final BeanInvalidationBracketer instance = 
    new NullBeanInvalidationBracketer();
  
  public void invalidate(String path, Runnable toinvoke) {
    toinvoke.run();
  }

}
