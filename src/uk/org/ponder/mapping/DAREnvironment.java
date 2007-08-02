/*
 * Created on 2 Aug 2007
 */
package uk.org.ponder.mapping;

import uk.org.ponder.messageutil.TargettedMessageList;

public class DAREnvironment {
  public TargettedMessageList messages;
  public BeanInvalidationBracketer bib;
  public DataConverterRegistry registry;
  
  public DAREnvironment(TargettedMessageList messages,
      BeanInvalidationBracketer bib, DataConverterRegistry registry) {
    this.messages = messages;
    this.bib = bib;
    this.registry = registry;
  }
  
  public DAREnvironment(TargettedMessageList messages) {
    this.messages = messages;
  }
}
