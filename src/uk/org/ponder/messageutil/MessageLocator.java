/*
 * Created on Dec 3, 2004
 */
package uk.org.ponder.messageutil;

import uk.org.ponder.beanutil.BeanLocator;
import uk.org.ponder.beanutil.BeanResolver;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public abstract class MessageLocator implements BeanLocator, BeanResolver {
  public abstract String getMessage(String[] bean, Object[] args);
  public String getMessage(String[] code) {
    return getMessage(code, null);
  }
  public String getMessage(String code) {
    return getMessage(code, (Object[])null);
  }
  public String getMessage(String code, Object[] args) {
    return getMessage(new String[] {code}, args);
  }
  public String getMessage(String[] code, Object param) {
    return getMessage(code, new Object[] {param});
  }
  public String getMessage(String code, Object param) {
    return getMessage(new String[] {code}, new Object[] {param});
  }
  public Object locateBean(String path) {
    return getMessage(new String[] {path}, null);
  }
  public String resolveBean(Object bean) {
    return getMessage((String)bean, null);
  }
  
}