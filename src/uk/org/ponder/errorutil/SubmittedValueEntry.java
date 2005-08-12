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
  public static final String DELETION_BINDING = "*"+FOSSIL_SUFFIX;
  public static final String COMMAND_LINK_PARAMETERS = "command link parameters";
  // key = {value-binding}componentid-fossil, value = oldvalue
  // deletion - id is *, does not enter rsvc.
  public String valuebinding;
  public String componentid;
  // this is either a String or a String[]
  public Object oldvalue;
  public boolean isdeletion = false;

  public static boolean isFossilisedBinding(String key) {
    return key.endsWith(FOSSIL_SUFFIX);
  }
  
  public SubmittedValueEntry(String key, Object value) {
    int endcurly = key.indexOf('}');
    valuebinding = key.substring(2, endcurly);
    oldvalue = value;
    componentid = key.substring(endcurly + 1, key.length() - 
        FOSSIL_SUFFIX.length());
    if (componentid.equals(DELETION_BINDING)) {
      isdeletion = true;
      componentid = null;
    }
  }
}
