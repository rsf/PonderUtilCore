/*
 * Created on 07-Jan-2006
 */
package uk.org.ponder.beanutil;

/** Allows a bean to specify a (more) serializable representation of itself
 * for preservation in a less live context.
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */
public interface PreservingBean {
  /** Return a (more) serializable representation of this bean */
  public Object getPreservable();
  /** Re-imbue this bean with state that has been preserved **/
  public void setPreserved(Object preserved);
}
