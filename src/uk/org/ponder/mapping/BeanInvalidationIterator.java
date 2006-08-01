/*
 * Created on 25 Jul 2006
 */
package uk.org.ponder.mapping;

public interface BeanInvalidationIterator {
  public void push(String path);
  public void pop();
  public void invalidate(String path);
}
