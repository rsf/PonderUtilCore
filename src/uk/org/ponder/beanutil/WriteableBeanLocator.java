/*
 * Created on Oct 22, 2005
 */
package uk.org.ponder.beanutil;

/** A writeable extension to the BeanLocator interface, that has the power
 * to unlink a bean appearing at the supplied root path.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *
 */
public interface WriteableBeanLocator extends BeanLocator {
  public boolean remove(String beanname);
  public void set(String beanname, Object toset);
}
