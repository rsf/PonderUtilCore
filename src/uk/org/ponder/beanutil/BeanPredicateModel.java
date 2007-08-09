/*
 * Created on 9 Aug 2007
 */
package uk.org.ponder.beanutil;

public interface BeanPredicateModel {
  public void clear();
  public boolean isMatch(String path);
  public void addPath(String spec);
}