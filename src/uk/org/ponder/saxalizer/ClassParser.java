/*
 * Created on Sep 22, 2004
 */
package uk.org.ponder.saxalizer;

import uk.org.ponder.stringutil.CharWrap;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class ClassParser implements SAXLeafTypeParser {
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

  public CharWrap render(Object torendero, CharWrap renderinto) {
    Class torender = (Class)torendero;
    return renderinto.append(torender.getName());
  }

}
