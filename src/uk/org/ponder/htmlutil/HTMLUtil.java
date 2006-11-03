/*
 * Created on May 17, 2006
 */
package uk.org.ponder.htmlutil;

import java.util.Iterator;
import java.util.Map;

import uk.org.ponder.stringutil.CharWrap;

public class HTMLUtil {
  public static void appendStyle(String style, String value, Map attrmap) {
    String oldstyle = (String) attrmap.get("style");
    if (oldstyle == null)
      oldstyle = "";
    oldstyle = oldstyle + " " + style + ": " + value + ";";
    attrmap.put("style", oldstyle);
  }

  /**
   * Parses a CSS string (separated by ; with keys named with :) into a
   * key/value map of Strings to Strings
   */
  public static void parseStyle(String style, Map toreceive) {
    String[] styles = style.split(";");
    for (int i = 0; i < styles.length; ++i) {
      int colpos = styles[i].indexOf(':');
      if (colpos == -1) {
        throw new IllegalArgumentException("Style string " + styles[i]
            + " does not contain a colon character");
      }
      String key = styles[i].substring(0, colpos).trim();
      String value = styles[i].substring(colpos + 1).trim();
      toreceive.put(key, value);
    }
  }

  public static String renderStyle(Map torender) {
    CharWrap togo = new CharWrap();
    for (Iterator sit = torender.keySet().iterator(); sit.hasNext();) {
      String key = (String) sit.next();
      String value = (String) torender.get(key);
      if (togo.size != 0) {
        togo.append(' ');
      }
      togo.append(key).append(": ").append(value).append(';');
    }
    return togo.toString();
  }

  /**
   * Constructs the String representing a Javascript array declaration.
   * 
   * @param name The Javascript name of the required array
   * @param elements The values of the elements to be rendered.
   */
  public static String emitJavascriptArray(String name, String[] elements) {
    CharWrap togo = new CharWrap();
    togo.append("  ").append(name).append(" = ").append("[\"");

    for (int i = 0; i < elements.length; ++i) {
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
    for (int i = 0; i < arguments.length; ++i) {
      togo.append(arguments[i]);
      if (i != arguments.length - 1) {
        togo.append("\", \"");
      }
    }
    togo.append("\");\n");

    return togo.toString();
  }
  
  /** The "natural" format accepted by Javascript's Date.parse() method **/
  public static String JS_DATE_FORMAT = "MMMM, d yyyy HH:mm:ss";

  public static String emitJavascriptVar(String name, String value) {
    return "  " + name + " = \"" + value + "\";\n";
  }

}
