/*
 * Created on 10-Jan-2006
 */
package uk.org.ponder.beanutil.entity;

import uk.org.ponder.beanutil.BeanLocator;

public class NewEntityEntry {
  public Object newent;
  public String tempid;
  public BeanLocator holder;
  public NewEntityEntry(Object newent, String tempid, BeanLocator holder) {
    this.newent = newent;
    this.tempid = tempid;
    this.holder = holder;
  }
}
