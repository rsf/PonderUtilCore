/*
 * Created on 2 Aug 2007
 */
package uk.org.ponder.mapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.org.ponder.beanutil.BeanLocator;
import uk.org.ponder.beanutil.BeanResolver;

/** 
 * A registry of {@link DataConverter} elements, capable of resolving them
 * into the canonical forms of {@link DARReshaper}s and {@link BeanResolver}s,
 * and assessing for any proposed operation which of the converters are 
 * applicable. 
 * </p>
 * The registry of converters is assumed static and will be fully evaluated
 * when this bean starts up.  
 * 
 * @author Antranig Basman (amb26@ponder.org.uk)
 *
 */
public class DataConverterRegistry {
  private List converters;
  
  public void setBeanLocator(BeanLocator beanLocator) {
    this.beanLocator = beanLocator;
  }

  public void setConverters(List converters) {
    this.converters = converters;
  }

  private BeanLocator beanLocator;
    
  private Map byClass = new HashMap();
  
  public void init() {
    for (int i = 0; i < converters.size(); ++ i) {
      DataConverter converter = (DataConverter) converters.get(i);
      if (converter.getConverterEL() != null) {
        Object conv = beanLocator.locateBean(converter.getConverterEL());
        converter.setConverter(conv);
      }
      if (converter.getTargetClass() != null) {
        byClass.put(converter.getTargetClass(), converter);
      }
    }
  }
  
  // If there is a DARReceiver in the way, we will not be able to process any 
  // by-Class matches - however, we *will* be able to process by-Path matches.
  // the list of shells will therefore be incomplete.
  public DataAlterationRequest reshapeDAR(DataAlterationRequest toshape, List shells) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
