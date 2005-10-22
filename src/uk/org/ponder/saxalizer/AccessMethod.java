/*
 * Created on Oct 22, 2005
 */
package uk.org.ponder.saxalizer;

public interface AccessMethod {

  public Object getChildObject(Object parent);

  public void setChildObject(Object parent, Object newchild);

  /**
   * Determines whether this method can be used for getting.
   * 
   * @return
   */
  public boolean canGet();

  /**
   * Determines whether this method can be used for setting.
   * 
   * @return
   */
  public boolean canSet();

  /** Determines whether the return type of this method is assignable
   * to Enumeration, which is interpreted as indicating a non-settable
   * multiple value, in the case that there is no individual set method.
   */
  public boolean isEnumeration();

  /** Determines whether this set method may be used for the delivery
   * of multiple subobjects. If it is, the object delivered by Get
   * may be converted into a receiver by EnumerationConverter.getDenumeration(oldinstance).
   */
  public boolean isDenumerable();

  public Class getAccessedType();

}