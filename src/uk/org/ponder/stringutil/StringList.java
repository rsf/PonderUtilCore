/*
 * Created on Sep 16, 2004
 */
package uk.org.ponder.stringutil;

import java.util.ArrayList;

/**
 * A convenience wrapper for a type-safe list of String objects.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class StringList extends ArrayList {
  public String stringAt(int i) {
    return (String)get(i);
  }
  /** Appends the members of the supplied list to this list.
   * @param recparents The list of Strings to be appended, which may be 
   * <code>null</code>.
   */
  public void append(StringList recparents) {
    if (recparents != null) {
    for(int i = 0; i < recparents.size(); ++ i) {
      add(recparents.get(i));
    }
  }
    }
}
