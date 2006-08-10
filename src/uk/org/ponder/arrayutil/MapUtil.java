/*
 * Created on 7 Aug 2006
 */
package uk.org.ponder.arrayutil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapUtil {
  /** I have finally had *enough* of writing this code! */
  public static void putMultiMap(Map target, Object key, Object value) {
    List oldlist = (List) target.get(key);
    if (oldlist == null) {
      oldlist = new ArrayList();
      target.put(key, oldlist);
    }
    oldlist.add(value);
  }
}
