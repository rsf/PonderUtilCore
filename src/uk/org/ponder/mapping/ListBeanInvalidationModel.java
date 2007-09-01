/*
 * Created on 25 Jul 2006
 */
package uk.org.ponder.mapping;

import uk.org.ponder.beanutil.support.ListBeanPredicateModel;

/** A BeanInvalidationModel implemented as a list of predicates **/

public class ListBeanInvalidationModel implements BeanInvalidationModel {
  private ListBeanPredicateModel model = new ListBeanPredicateModel();

  public void clear() {
    model.clear();
  }

  public String invalidPathMatch(String spec) {
    return model.findMatch(spec, true);
  }

  public void invalidate(String path) {
    model.addPath(path);
  }
 
}
