/*
 * Created on May 17, 2006
 */
package uk.org.ponder.htmlutil;

import java.util.Map;

public class HTMLUtil {
  public static void appendStyle(String style, String value, Map attrmap) {
    String oldstyle = (String) attrmap.get("style");
    if (oldstyle == null) oldstyle = "";
    oldstyle = oldstyle + " " + style + ": " + value + ";";
    attrmap.put("style", oldstyle);
  }
}
