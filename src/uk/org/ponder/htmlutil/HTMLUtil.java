/*
 * Created on May 17, 2006
 */
package uk.org.ponder.htmlutil;

import java.util.Map;

import uk.org.ponder.stringutil.CharWrap;

public class HTMLUtil {
  public static void appendStyle(String style, String value, Map attrmap) {
    String oldstyle = (String) attrmap.get("style");
    if (oldstyle == null) oldstyle = "";
    oldstyle = oldstyle + " " + style + ": " + value + ";";
    attrmap.put("style", oldstyle);
  }
  
  public static String emitJavascriptArray(String name, String[] elements) {
    CharWrap togo = new CharWrap();
    togo.append("  ").append(name).append(" = ").append("[\"");
    
    for (int i = 0; i < elements.length; ++ i) {
      togo.append(elements[i]);
      if (i != elements.length - 1) {
        togo.append("\", \"");
      }
    }
    togo.append("\"];\n");
    
    return togo.toString();
  }
  
  public static String emitJavascriptCall(String name, String[] arguments) {
    CharWrap togo = new CharWrap();
    togo.append("  ").append(name).append("(\"");
    for (int i = 0; i < arguments.length; ++ i) {
      togo.append(arguments[i]);
      if (i != arguments.length - 1) {
        togo.append("\", \"");
      }
    }
    togo.append("\");\n");
    
    return togo.toString();
    
  }

  public static String emitJavascriptVar(String name, String value) {
    return "  " + name + " = \"" + value + "\";\n"; 
  }
  
}
