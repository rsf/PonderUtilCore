/*
 * Created on Nov 23, 2004
 */
package uk.org.ponder.errorutil;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class SubmittedValueEntry {
  public static final String FOSSIL_SUFFIX = "-fossil";
  public static final String DELETION_BINDING = "deletion"+FOSSIL_SUFFIX;
  // key = componentid-fossil, value = {value-binding}oldvalue
  // deletion - first id character is *, does not enter rsvc.
  public String valuebinding;
  public String componentid;
  public String oldvalue;
  public boolean isdeletion = false;

  public static boolean isFossilisedBinding(String key) {
    return key.endsWith(FOSSIL_SUFFIX);
  }
  
  public SubmittedValueEntry(String key, String value) {
    int endcurly = value.indexOf('}');
    valuebinding = value.substring(2, endcurly);
    oldvalue = value.substring(endcurly + 1);
    if (key.equals(DELETION_BINDING)) {
      isdeletion = true;
    }
    else {
      componentid = key.substring(0, key.length() - FOSSIL_SUFFIX.length());
    }
  }
}
