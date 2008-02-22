/*
 * Created on Nov 22, 2004
 */
package uk.org.ponder.mapping;

import java.util.ArrayList;

import uk.org.ponder.beanutil.BeanModelAlterer;
import uk.org.ponder.mapping.support.DARApplier;

/**
 * A list of {@link DataAlterationRequest} entries to be applied in sequence
 * to the model, via a {@link DARApplier} ( {@link BeanModelAlterer} ).
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class DARList extends ArrayList{
  public DataAlterationRequest DARAt(int i) {
    return (DataAlterationRequest)get(i);
  }
}
