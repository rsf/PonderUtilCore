/*
 * Created on 28-Jan-2006
 */
package uk.org.ponder.htmlutil;

public class HTMLConstants {

  public static String[][] tagtoURL = {
    {"href", "<a ", "<link "},
    {"src", "<img ", "<frame ", "<script ", "<iframe "},
    {"action", "<form "}
  };
// Every tag may have a "background" attribute holding a URL
  public static String[] ubiquitousURL = {"background"};
}
