/*
 * Created on 7 Feb 2008
 */
package uk.org.ponder.iterationutil;

import java.util.Collection;
import java.util.Iterator;

/** Static utilities for dealing with iteration processes **/

public class Iteration {
  public static final void addAll(Iterator i, Collection target) {
    while (i.hasNext()) {
      target.add(i.next());
    }
  }
}
