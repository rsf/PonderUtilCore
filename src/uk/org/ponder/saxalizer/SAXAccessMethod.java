package uk.org.ponder.saxalizer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import uk.org.ponder.util.AssertionException;
import uk.org.ponder.util.EnumerationConverter;
import uk.org.ponder.util.UniversalRuntimeException;

/*
 * SAXAccessMethod is a package private class that represents a
 * SAXAccessMethodSpec that the SAXalizer has resolved to an actual method by
 * means of reflection. For a set method, when the correct XML closing tag is
 * seen, the method will be invoked with the just-constructed tag as argument.
 * All construction of reflectors is done during the construction of this class.
 */
public class SAXAccessMethod implements AccessMethod {
  public static final Class[] emptyclazz = {};
  public static final Object[] emptyobj = {};

  Field field; // The Field object corresponding to the child field, if there
                // is
  // one.
  Method getmethod; // The actual Method object to be invoked
  Method setmethod;
  Class clazz; // The type of subobject (or superclass thereof) handled by this
                // method
  Class accessclazz; // The actual (declared) return or field type in code (may
                      // be container)
  Class parentclazz; // The class that this is a method of, for convenience.
  public String tagname;
  boolean ispolymorphic; // Uses the new "tag*" polymorphic nickname scheme
  public boolean ismultiple; // A collection rather than a single object is
                              // being addressed
  public boolean isexactsetter; // A more specific set method has been supplied
                                // than get method.
  boolean isenumeration; // if "ismultiple" is this delivered via an
                          // enumeration?
  boolean ismappable;
  boolean isdevnull; // if this is a "black hole" setter for ignoring properties

  // Note that enumerations are the only things which are enumerable but not
  // denumerable.

  private SAXAccessMethod(Class parentclazz, String tagname) {
    this.parentclazz = parentclazz;
    if (tagname != null && tagname.endsWith("*")) {
      tagname = tagname.substring(0, tagname.length() - 1);
      ispolymorphic = true;
    }
    this.tagname = tagname;
  }

  // returns any (one-argument) method with the required name in the specified
  // class, regardless of type. This is called by the constructor when it
  // is trying to automatically infer set types.
  private static Method findSetMethod(Class tosearch, String methodname) {
    Method[] methods = tosearch.getMethods();
    for (int i = 0; i < methods.length; ++i) {
      Method thismethod = methods[i];
      if (thismethod.getName().equals(methodname)
          && thismethod.getParameterTypes().length == 1
          && thismethod.getReturnType().equals(Void.TYPE)) {
        return thismethod;
      }
    }
    return null;
  }

  // TODO: This is where we are now.
  // The problem is that for enumerable types, get and set methods have
  // different
  // types, and they are provided separately in SAXalizable and DeSAXalizable.
  // Secondly, specs provided from MAPPING file MAY have the class specification
  // missing,
  // in the hopes that it could be determined HERE now we actually have the
  // relevant method in our hands.
  // If a thing is an enumeration, we still insisted it had a typesafe
  // SET method which would tell us what type would be provided to ADD.
  // NOW, of course, there may be NO information in the class from
  // reflection, and the type of the object would have to be
  // specified in the MAPPING file.
  // First we provided a global "*" which insisted that subtags
  // were named after the relevant java class.
  // Then we provided "tag*" which indicated that a nickname
  // was supplied in a "type=nickname" attribute.

  // A) the parent method may fuse these two into a single get/set
  // spec before delivery, if their tag matches.
  // B) What the hell to do about Enumeration tags!
  // They come as a set/add pair.... we must simply record in the
  // AccessMethod itself that it is an enumeration. With a flag!
  // Then the concrete type itself (so far as it is known) can be
  // safely put in there.
  // the relevant call to SET is in SAXalizer.endElement.
  // it tries to look up the tag by name

  public SAXAccessMethod(SAXAccessMethodSpec m, Class parentclazz) {
    this(parentclazz, m.xmlname);

    if (m.fieldname != null) {
      try {
        field = parentclazz.getField(m.fieldname);
      }
      catch (Throwable t) {
        throw UniversalRuntimeException.accumulate(t,
            "Unable to find field with name " + m.fieldname + " in class "
                + parentclazz);
      }
      // record the specified class name if there was one.
      // TODO: for containers like StringSet we should try to look up
      // container/containee information from DefaultInferrer. Semantics
      // of this are slightly unclear... this is not a "default" mapping, BUT
      // ERM! byXMLNameSafe appears to try to fill in m.clazz by itself, does
      // this work?
      accessclazz = field.getType();
      clazz = m.clazz == null ? accessclazz
          : m.clazz;
      checkEnumerable(accessclazz);
    }
    else {
      if (m.getmethodname != null) {
        try {
          getmethod = parentclazz.getMethod(m.getmethodname, emptyclazz);
        }
        catch (Throwable t) {
          throw UniversalRuntimeException.accumulate(t,
              "Unable to find GET method with name " + m.getmethodname
                  + " in class " + parentclazz);
        }
        accessclazz = getmethod.getReturnType();
        if (!checkEnumerable(accessclazz) && m.clazz != null
            && !m.clazz.isAssignableFrom(accessclazz)) {
          throw new AssertionException("Actual return type of get method \""
              + getmethod + "\" is not assignable to advertised type of "
              + m.clazz);
        }
      }
      if (m.setmethodname != null) {
        if (m.clazz == null) {
          // infer the type of a set method by looking for a method of the
          // same name. If you have created two such, you should be destroyed
          // anyway.
          setmethod = findSetMethod(parentclazz, m.setmethodname);
          if (setmethod == null) {
            throw new UniversalRuntimeException(
                "Unable to find a SET method with name " + m.setmethodname
                    + " in class " + parentclazz);
          }
          m.clazz = setmethod.getParameterTypes()[0];
        }
        else { // the spec specifies the actual object type
          try {
            setmethod = parentclazz.getMethod(m.setmethodname,
                new Class[] { m.clazz });
          }
          catch (Throwable t) {
            setmethod = findSetMethod(parentclazz, m.setmethodname);
            // if it didn't have exactly the right type, last-ditch search for a compatible setter.
            if (setmethod == null
                || !setmethod.getParameterTypes()[0].isAssignableFrom(m.clazz)) {
              throw (UniversalRuntimeException.accumulate(t,
                  "Unable to find SET method with name " + m.setmethodname
                      + " accepting argument " + m.clazz + " in class "
                      + parentclazz));
            }
          }
        }
        clazz = m.clazz;
        if (accessclazz == null) {
          // avoid overwriting any container type discovered from field or get
          accessclazz = clazz;
        }
      } // end if there is a set method
      if (m.accesstype.equals(SAXAccessMethodSpec.ACCESS_IGNORE)) {
        isdevnull = true;
      }
      // if there is only a get method (as for maps) the actual type will not
      // be determined yet
    } // end if not a fieldname
    if (setmethod != null && getmethod != null) {
      isexactsetter = !setmethod.getParameterTypes()[0].equals(getmethod.getReturnType());
    }
    // TODO: some weird code here for "default" tags - what on earth does this do?
    if (tagname != null && tagname.equals("")) {
      if (!canGet()) {
        throw new UniversalRuntimeException(
            "No GET scheme supplied for mapped default tag");
      }
      if (!EnumerationConverter.isMappable(accessclazz)) {
        throw new UniversalRuntimeException(
            "Default mapped type does not map onto a mappable type");
      }
      // unless specified by now, remain mapped values as strings.
      // QQQQQ make sure defaultinferring happens before now.
      clazz = m.clazz == null ? String.class
          : m.clazz;
    }
  }

  /**
   * Determines whether the supplied class, the actualtype of a field or a get
   * method, is enumerable. If it is, the ismultiple field is set, and the
   * isenumeration field is conditionally set.
   * 
   * @param clazz2
   */
  private boolean checkEnumerable(Class clazz) {
    ismultiple = EnumerationConverter.isEnumerable(clazz);
    if (ismultiple) {
      isenumeration = !EnumerationConverter.isDenumerable(clazz);
    }
    ismappable = EnumerationConverter.isMappable(clazz);
    return ismultiple;
  }

  public String toString() {
    return "SAXAccessMethod name " + getmethod.getName() + " parent "
        + parentclazz + " tagname " + tagname;
  }

  public Object getChildObject(Object parent) {
    try {
      if (field != null) {
        return field.get(parent);
      }
      else
        return getmethod.invoke(parent, emptyobj);
    }
    catch (Throwable t) {
      throw UniversalRuntimeException.accumulate(t,
          "Error acquiring child object " + tagname + " of object " + parent + " "
              + parent.getClass());
    }
  }

  public void setChildObject(Object parent, Object newchild) {
    try {
      if (field != null) {
        field.set(parent, newchild);
      }
      else if (setmethod != null) {
        setmethod.invoke(parent, new Object[] { newchild });
      }
    }
    catch (Throwable t) {
      throw UniversalRuntimeException.accumulate(t,
          "Error setting child " + tagname + " of parent " + parent + " to value " + newchild);
    }
  }

  /**
   * Determines whether this method can be used for getting.
   * 
   * @return
   */
  public boolean canGet() {
    return (field != null || getmethod != null);
  }

  /**
   * Determines whether this method can be used for setting.
   * 
   * @return
   */
  public boolean canSet() {
    return (field != null || setmethod != null || isdevnull);
  }

  /**
   * Determines whether the return type of this method is assignable to
   * Enumeration, which is interpreted as indicating a non-settable multiple
   * value, in the case that there is no individual set method.
   */
  public boolean isEnumeration() {
    return isenumeration;
  }

  /**
   * Determines whether this GET method result may be used for the delivery of multiple
   * subobjects. If it is, the object delivered by Get may be converted into a
   * receiver by EnumerationConverter.getDenumeration(oldinstance).
   */
  public boolean isDenumerable() {
    return ismultiple && !isenumeration && !isexactsetter;
  }

  /** The type of subobject that this method deals in * */
  public Class getAccessedType() {
    return clazz;
  }

  public Class getDeclaredType() {
    return accessclazz;
  }
}