/*
 * Created on Sep 22, 2004
 */
package uk.org.ponder.conversion;

import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class ClassParser implements LeafObjectParser {
  public Object parse(String toparse) {
    Class togo;
    try {
      togo = Class.forName(toparse);
    }
    catch (ClassNotFoundException cnfe) {
      throw UniversalRuntimeException.accumulate(cnfe, "Class " + toparse + " could not be loaded");
    }
    return togo;
  }

  public String render(Object torendero) {
    return ((Class)torendero).getName();
  }

  public Object copy(Object tocopy) {
    return tocopy;
  }

}
