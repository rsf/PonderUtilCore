/*
 * Created on Dec 14, 2004
 */
package uk.org.ponder.jsfutil;

import uk.org.ponder.util.FieldHash;

/**
 * A blank ViewParameters object for those who want to use only 
 * JSF-style navigation.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
public class BlankViewParameters extends ViewParameters {

  public FieldHash getFieldHash() {
    return new FieldHash(BlankViewParameters.class);
  }
  public void clearActionState() {
    // TODO Auto-generated method stub
  }

  public void clearParams() {
    // TODO Auto-generated method stub
  }

}