/*
 * Created on 13 Feb 2008
 */
package uk.org.ponder.mapping.support;

import uk.org.ponder.beanutil.PropertyAccessor;
import uk.org.ponder.mapping.DAREnvironment;
import uk.org.ponder.mapping.DataAlterationRequest;

public class DARApplyEnvironment {
  public DataAlterationRequest dar;
  public DAREnvironment darenv;
  public Object convert;
  public Object moveobj;
  public String tail;
  public Class leaftype;
  public PropertyAccessor pa;
  
  public DARApplyEnvironment(DataAlterationRequest dar, DAREnvironment darenv, 
      Object moveobj, Object convert, String tail, PropertyAccessor pa, Class leaftype) {
    this.dar = dar;
    this.darenv = darenv;
    this.moveobj = moveobj;
    this.convert = convert;
    this.tail = tail;
    this.pa = pa;
    this.leaftype = leaftype;
  }
  
}
