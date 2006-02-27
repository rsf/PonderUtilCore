/*
 * Created on 27-Feb-2006
 */
package uk.org.ponder.beanutil;

/** Can fetch an EL value from a given (root) BeanLocator */

public class ELEvaluator implements BeanGetter {
  private BeanModelAlterer bma;
  private BeanLocator beanlocator;
  public void setBeanModelAlterer(BeanModelAlterer bma) {
    this.bma = bma;
  }
  
  public void setBeanLocator(BeanLocator beanlocator) {
    this.beanlocator = beanlocator;
  }
  
  public Object getBean(String beanpath) {
    return bma.getBeanValue(beanpath, beanlocator);
  }
  
}
