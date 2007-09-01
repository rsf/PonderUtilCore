/*
 * Created on 13-Jan-2006
 */
package uk.org.ponder.beanutil.support;

import uk.org.ponder.beanutil.BeanLocator;
import uk.org.ponder.beanutil.BeanResolver;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;

/** Will return a named field of any bean, rendered as a String */

public class BeanFieldResolver implements BeanLocator {

  private SAXalizerMappingContext mappingcontext;

  public void setMappingContext(SAXalizerMappingContext mappingcontext) {
    this.mappingcontext = mappingcontext;
  }

  public Object locateBean(final String path) {
    return new BeanResolver() {
      public String resolveBean(Object bean) {
        // A line of code only Andy could love.
        return mappingcontext.saxleafparser.render(mappingcontext.getAnalyser(bean.getClass()).getAccessMethod(path).getChildObject(bean));
      }
    };
  }

}
