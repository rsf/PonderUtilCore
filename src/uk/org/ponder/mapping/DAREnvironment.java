/*
 * Created on 2 Aug 2007
 */
package uk.org.ponder.mapping;

import uk.org.ponder.beanutil.BeanPredicateModel;
import uk.org.ponder.messageutil.TargettedMessageList;

public class DAREnvironment {
  public TargettedMessageList messages;
  public BeanInvalidationBracketer bib;
  public BeanPredicateModel addressibleModel;
  public DataConverterRegistry registry;
  
  public DAREnvironment(TargettedMessageList messages,
      BeanInvalidationBracketer bib, BeanPredicateModel addressibleModel, 
      DataConverterRegistry registry) {
    this.messages = messages;
    this.bib = bib;
    this.addressibleModel = addressibleModel;
    this.registry = registry;
  }
  
  public DAREnvironment(TargettedMessageList messages) {
    this.messages = messages;
  }
}
