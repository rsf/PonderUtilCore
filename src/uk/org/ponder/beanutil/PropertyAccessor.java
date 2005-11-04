/*
 * Created on Oct 22, 2005
 */
package uk.org.ponder.beanutil;

/** A slightly slimmer version of the Spring PropertyAccessor interface.
 * Wraps (probably reflective) operations performed on a single bean. Note that
 * these methods take the parent object as argument and hence PropertyAccessors
 * are generally immutable (per class), leading to great economies. 
 * @see BeanWrapperImpl for examples of incredible diseconomies.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *
 */
public interface PropertyAccessor {

  public boolean canSet(String name);

  public void setProperty(Object parent, String name, Object value);

  public void unlink(Object parent, String name);

  public boolean canGet(String name);

  public Object getProperty(Object parent, String name);
  
  public Class getPropertyType(String name);

}