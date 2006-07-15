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
//TODO This is still somewhat odd since all these take property name 
// as argument, but I guess this is unavoidable if we want a 1st-class 
// abstraction equivalent to a map. For efficiency, we probably want a 
// mid-level class delivering AccessMethods so we can avoid the hashtable
// hit on each call.
public interface PropertyAccessor {

  public boolean canSet(String name);

  public void setProperty(Object parent, String name, Object value);

  public void unlink(Object parent, String name);

  public boolean canGet(String name);

  public Object getProperty(Object parent, String name);
  
  /**
   * Returns the type of this property. If it is a collection or other
   * denumerable type for which we have a mapping or inference, this will be the
   * contained type of the collection rather than the collection type itself.
   * In this case, getProperty will return an object some enumerable or denumerable
   * object rather than one of this type.
   */
  public Class getPropertyType(Object parent, String name);

  public boolean isMultiple(Object parent, String name);
}