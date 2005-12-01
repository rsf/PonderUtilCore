/*
 * Created on Oct 26, 2004
 */
package uk.org.ponder.conversion;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import uk.org.ponder.saxalizer.SAXAccessMethod;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * A default SAXLeafTypeParser capable of dealing with any object declaring
 * a toString() method and a constructor accepting a single String argument.
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 */
// TODO: Is this ancient and inefficient piece of cruft used anywhere?
// Why precisely did we invent it?
public class StringableLeafTypeParser implements LeafObjectParser {
  private Constructor stringcons;
  private Method tostring;
  public StringableLeafTypeParser(Constructor stringcons, Method tostring) {
    this.stringcons = stringcons;
    this.tostring = tostring;
  }
  
  public static StringableLeafTypeParser checkClass(Class clazz) {
    StringableLeafTypeParser togo = null;
    Constructor[] constructors = clazz.getConstructors();
    Constructor stringcons = null;
    for (int i = 0; i < constructors.length; ++ i) {
      Constructor cons = constructors[i];
      Class[] params = cons.getParameterTypes();
      if (params.length == 1 && params[0] == String.class) {
        stringcons = cons;
      }
    }
    Method tostring = null;
    try {
      tostring = clazz.getMethod("toString", SAXAccessMethod.emptyclazz);
      if (tostring.getDeclaringClass() == Object.class) {
        tostring = null;
      }
    }
    catch (Throwable t) {}
    if (stringcons != null && tostring != null) {
      togo = new StringableLeafTypeParser(stringcons, tostring);
    }
    return togo;
  }
  
  public Object parse(String toparse) {
    try {
      return stringcons.newInstance(new Object[] {toparse});
    }
    catch (Throwable t) {
      throw UniversalRuntimeException.accumulate(t, "Error invoking default constructor for object of type "+
          stringcons.getDeclaringClass());
    }
  }

  public String render(Object torender) {
    try {
      return tostring.invoke(torender, SAXAccessMethod.emptyobj).toString();
    }
    catch (Throwable t) {
      throw UniversalRuntimeException.accumulate(t, "Error invoking toString method for object of type "+
          tostring.getDeclaringClass());
    }
  }
  
  //NB - probably wrong.
  public Object copy(Object tocopy) {
    return tocopy;
  }

}
