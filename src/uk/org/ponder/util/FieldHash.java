/*
 * Created on Oct 25, 2004
 */
package uk.org.ponder.util;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import uk.org.ponder.stringutil.StringList;

/**
 * Automates the process of reflecting a value type full of Strings to and from
 * a Map of Strings. Useful for HTTP requests and the like.
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
public class FieldHash {
  public TreeMap fieldmap = new TreeMap();
  private Class target;

  public FieldHash(Class target) {
    this.target = target;
  }

  public void addField(String fieldname) {
    Field f = null;
    try {
      f = target.getField(fieldname);
    }
    catch (Throwable t) {
      throw UniversalRuntimeException.accumulate(t, "Unable to reflect field "
          + fieldname + " for class target");
    }
    if (f.getType() != String.class) {
      throw new UniversalRuntimeException(
          "Only String-typed fields are supported by FieldHash");
    }
    fieldmap.put(fieldname, f);
  }

  public void fromMap(Map from, Object targetobj) {

    for (Iterator keys = from.keySet().iterator(); keys.hasNext();) {
      String fieldname = (String) keys.next();
      Field field = (Field) fieldmap.get(fieldname);
      if (field != null) {
        Object valueo = from.get(fieldname);
        String value = valueo instanceof String? (String)valueo : ((String[])valueo)[0];
        try {
          field.set(targetobj, value);
        }
        catch (Throwable t) {
          throw UniversalRuntimeException.accumulate(t,
              "Unable to set value of field " + fieldname + " in object "
                  + target);
        }
      }
    }

  }
  /** Returns a 2-element array of StringLists, the first holding key names,
   * the second holding values.
   */
  public StringList[] fromObj(Object targetobj) {
    StringList[] togo = new StringList[2];
    togo[0] = new StringList();
    togo[1] = new StringList();
    try {
      for (Iterator keys = fieldmap.keySet().iterator(); keys.hasNext();) {
        String fieldname = (String) keys.next();
        Field field = (Field) fieldmap.get(fieldname);
        String value = (String) field.get(targetobj);
        if (value != null) {
          togo[0].add(fieldname);
          togo[1].add(value);
        }
      }
    }
    catch (Throwable t) {
      throw UniversalRuntimeException.accumulate(t,
          "Unable to get value of field");
    }
    return togo;
  }
}