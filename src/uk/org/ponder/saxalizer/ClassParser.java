/*
 * Created on Sep 22, 2004
 */
package uk.org.ponder.saxalizer;

import org.xml.sax.SAXException;

import uk.org.ponder.stringutil.CharWrap;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class ClassParser implements SAXLeafTypeParser {
  public Object parse(String toparse) throws SAXException {
    Class togo;
    try {
      togo = Class.forName(toparse);
    }
    catch (ClassNotFoundException cnfe) {
      throw new SAXException("Class " + toparse + " could not be loaded");
    }
    return togo;
  }

  public CharWrap render(Object torendero, CharWrap renderinto) {
    Class torender = (Class)torendero;
    return renderinto.append(torender.getName());
  }

}
