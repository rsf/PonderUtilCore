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
 */
public class SAXAccessMethod {
  public static final Class[] emptyclazz = {};
  public static final Object[] emptyobj = {};

  Field field; // The Field object corresponding to the child field, if there is
               // one.
  Method getmethod; // The actual Method object to be invoked
  Method setmethod;
  Class clazz; // The return type (or argument type) of the method
  Class parentclazz; // The class that this is a method of, for convenience.
  String tagname;
  boolean ispolymorphic; // Uses the new "tag*" polymorphic nickname scheme
  boolean ismultiple; // A collection rather than a single object is being addressed.

  private SAXAccessMethod(Class parentclazz, String tagname) {
    this.parentclazz = parentclazz;
    if (tagname.endsWith("*")) {
      tagname = tagname.substring(0, tagname.length() - 1);
      ispolymorphic = true;
    }
    this.tagname = tagname;
  }

  // TODO: This is where we are now.
  // The problem is that for enumerable types, get and set methods have different
  // types, and they are provided separately in SAXalizable and DeSAXalizable.
  // Secondly, specs provided from MAPPING file MAY have the class specification missing,
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
  
  public SAXAccessMethod(SAXAccessMethodSpec m, Class parentclazz)  {
    this(parentclazz, m.xmlname);
    try {
    if (m.fieldname != null) {
      field = parentclazz.getField(m.fieldname);
      clazz = field.getClass();
    }
    else {
      if (m.getmethodname != null) {
        getmethod = parentclazz.getMethod(m.getmethodname, emptyclazz);
        Class actualreturntype = getmethod.getReturnType();
        if (EnumerationConverter.isEnumerable(actualreturntype)) {
          ismultiple = true;
        }
        else if (!m.clazz.isAssignableFrom(actualreturntype)) {
          throw new AssertionException("Actual return type of get method \""
              + getmethod + "\" is not assignable to advertised type of "
              + m.clazz);
        }
      }
      if (m.setmethodname != null) {
        setmethod = parentclazz.getMethod(m.setmethodname,
            new Class[] { m.clazz });
        clazz = m.clazz;
      }
    }
    }
    catch (Throwable t) {
      throw UniversalRuntimeException.accumulate(t, "Field or method specified by MethodSpec"
           + m + " could not be found in object of type " + parentclazz);
    }
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
          "Error acquiring child object of object " + parent);
    }
  }

  public void setChildObject(Object parent, Object newchild) {
    try {
      if (field != null) {
        field.set(parent, newchild);
      }
      else {
        setmethod.invoke(parent, new Object[] { newchild });
      }
    }
    catch (Throwable t) {
      throw UniversalRuntimeException.accumulate(t,
          "Error setting child object " + newchild + " of parent " + parent);
    }
  }

  /** Determines whether this method can be used for getting.
   * @return
   */
  public boolean canGet() {
    return (field != null || getmethod != null);
  }
}