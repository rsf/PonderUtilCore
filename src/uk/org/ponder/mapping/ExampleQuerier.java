/*
 * Created on Dec 14, 2004
 */
package uk.org.ponder.mapping;

import uk.org.ponder.saxalizer.SAXAccessMethod;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class ExampleQuerier {
  public static boolean match(Object query, Object totest, SAXAccessMethod[] fields) {
    for (int i = 0; i < fields.length; ++ i) {
      Object querychild = fields[i].getChildObject(query);
      if (query != null) {
        Object testchild = fields[i].getChildObject(totest);
        if (!querychild.equals(testchild)) return false;
      }
    }
    return true;
  }
}
