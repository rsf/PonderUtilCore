/*
 * Created on Sep 23, 2004
 */
package uk.org.ponder.saxalizer;

import java.util.ArrayList;

/**
 * A typesafe wrapper for a list of SAXAccessMethodSpec objects
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class SAMSList extends ArrayList {
  public SAXAccessMethodSpec SAMSAt(int i) {
    return (SAXAccessMethodSpec)get(i);
  }
  /** Returns the first SAXAccessMethodSpec object that matches the
   * given XML name, or <code>null</code> if none is found. There 
   * should be at most one such entry at any time.
   * @param tagname
   * @return
   */
  public SAXAccessMethodSpec byXMLName(String tagname) {
    for (int i = 0; i < size(); ++ i) {
      SAXAccessMethodSpec thissams = SAMSAt(i);
      if (thissams.xmlname.equals(tagname)) return thissams;
    }
    return null;
  }
}
