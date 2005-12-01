/*
 * Created on Nov 25, 2005
 */
package uk.org.ponder.arrayutil;

import java.util.ArrayList;
import java.util.List;

/** Converts a list of lists into a single list property * */

public class ListAggregator {
  private List combined;

  public void setLists(List lists) {
    combined = new ArrayList();
    for (int i = 0; i < lists.size(); ++i) {
      List listat = (List) lists.get(i);
      combined.addAll(listat);
    }
  }

  public List getList() {
    return combined;
  }
}
