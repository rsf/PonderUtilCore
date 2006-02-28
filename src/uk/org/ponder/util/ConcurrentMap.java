/*
 * Created on 28-Feb-2006
 */
package uk.org.ponder.util;

import java.util.Map;

/** Retrofit part of the 1.5/emory concurrent map interface to JDK 1.4 */

public interface ConcurrentMap extends Map {
  public Object putIfAbsent(Object key, Object value);
}
