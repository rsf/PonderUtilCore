/*
 * Created on Dec 3, 2004
 */
package uk.org.ponder.messageutil;

import uk.org.ponder.beanutil.BeanLocator;
import uk.org.ponder.beanutil.BeanResolver;

/**
 * Core interface supporting lookup of localised messages. Very similar to
 * Spring's MessageSource, only with a greater flexibility of lookup, and the
 * dependency on the Locale has been factored off into a request-scope
 * dependency.
 * <p/>
 * MessageLocator also supports use as either a BeanLocator (hence messages may
 * be addressed directly via EL as <code>#{messageLocator.message-key}</code>)
 * or as BeanResolver (hence messages may be automatically fetched during
 * component fixup stage in RSF by declaring the <code>resolver</code> field
 * in a <code>UIBound</code> to be targetted at the EL
 * <code>#{messageLocator}</code>.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public abstract class MessageLocator implements BeanLocator, BeanResolver {
  /**
   * @see #getMessage(String, Object[])
   * @param code An array of potential message codes to be looked up, in order
   * of priority. Each code will be looked up in turn and the resolved message
   * from the first which is found will be returned, if any. This strategy 
   * matches that of Spring's MessageSourceResolvable.
   */
  public abstract String getMessage(String[] code, Object[] args);

  /** Resolve a defaultible message which takes no arguments 
   * @see #getMessage(String[], Object[]) **/
  public String getMessage(String[] code) {
    return getMessage(code, null);
  }

  public String getMessage(String code) {
    return getMessage(code, (Object[]) null);
  }

  /**
   * @param code the code to lookup up, such as 'calculator.noRateSet'
   * @param args Array of arguments that will be filled in for params within the
   *          message (params look like "{0}", "{1,date}", "{2,time}" within a
   *          message), or <code>null</code> if none.
   * @return the resolved message, or a default message if not found.
   */
  public String getMessage(String code, Object[] args) {
    return getMessage(new String[] { code }, args);
  }

  public String getMessage(String[] code, Object param) {
    return getMessage(code, new Object[] { param });
  }

  public String getMessage(String code, Object param) {
    return getMessage(new String[] { code }, new Object[] { param });
  }

  /** @see BeanLocator#locateBean(String)
   */
  public Object locateBean(String path) {
    return getMessage(new String[] { path }, null);
  }

  /** @see BeanResolver#resolveBean(Object)
   */
  
  public String resolveBean(Object bean) {
    return getMessage((String) bean, null);
  }

}