/*
 * Created on 2 Aug 2007
 */
package uk.org.ponder.mapping;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.org.ponder.beanutil.BeanModelAlterer;
import uk.org.ponder.beanutil.BeanPredicateModel;
import uk.org.ponder.mapping.support.DARApplier;
import uk.org.ponder.mapping.support.DataConverterRegistry;
import uk.org.ponder.messageutil.TargettedMessageList;

/** An environment record setting up configuration for a particular invocation
 * of the {@link DARApplier} ({@link BeanModelAlterer}).
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 */

public class DAREnvironment {
  public TargettedMessageList messages;
  public BeanInvalidationBracketer bib;
  public BeanPredicateModel addressibleModel;
  public DataConverterRegistry registry;
  public Map writeDepends;
  public Set cancelSet = new HashSet();
  
  public DAREnvironment(TargettedMessageList messages,
      BeanInvalidationBracketer bib, BeanPredicateModel addressibleModel, 
      DataConverterRegistry registry, Map writeDepends) {
    this.messages = messages;
    this.bib = bib;
    this.addressibleModel = addressibleModel;
    this.registry = registry;
    this.writeDepends = writeDepends;
  }
  
  public DAREnvironment(TargettedMessageList messages) {
    this.messages = messages;
  }
}
