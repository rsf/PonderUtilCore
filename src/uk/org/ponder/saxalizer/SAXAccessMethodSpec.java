package uk.org.ponder.saxalizer;

/**
 * A SAXAccessMethodSpec identifies a mapping from an XML tag name to a Java
 * method name that is capable of either setting or getting an XML-serialised
 * subobject (the <it>target object </it). If it is a get method, the signature
 * of the method must be <code>
 * &lt;clazz&gt; &lt;methodname&gt;() </code>. If
 * it is a set method, the signature of the method must be <code>void
 * &lt;methodname&gt;(&lt;clazz&gt;)</code>.
 * <p>
 * Thus we must be clear that <code>clazz</code> is the type returned or taken
 * from the access method, i.e. the type of the subobject and NOT the type of
 * the object that the method is a member of.
 * <p>
 * The value "*" is allowed for the parameter <code>clazz</code>, which
 * indicates that the XML tagname will be interpreted as a fully-qualified Java
 * classname. This class must exist and be a subclass of the actual argument
 * type of the method identified.
 */

public class SAXAccessMethodSpec implements SAXalizable, SAXalizableAttrs {
  public static final int GET_METHOD = 0;
  public static final int SET_METHOD = 1;
  public static final int FIELD_METHOD = 2;
  public static final String ACCESS_METHOD = "method";
  public static final String ACCESS_FIELD = "field";
  public static final String ACCESS_IGNORE = "ignore";
  public static final String XML_TAG = "tag";
  public static final String XML_ATTRIBUTE = "attribute";
  public static final String XML_BODY = "body";

  public static final String DEFAULT_ACCESS = ACCESS_METHOD;
  public static final String DEFAULT_XML_FORM = XML_TAG;
  // either the tag or attribute name, depending on the value of xmlform.
  public String xmlname;
  // if either getmethodname or setmethodname is set, fieldname will not be
  // set. At least one out of the following three will be.
  public String getmethodname;
  public String setmethodname;
  public String fieldname;
  // QQQQQ this field need not exist, although the ACCESS strings do.
  public String accesstype = DEFAULT_ACCESS;
  public String xmlform = DEFAULT_XML_FORM;
  public Class clazz;

  /**
   * @param tag The tagname that will be used when the target object is
   *          serialised, or "*" if the tag is not known.
   * @param methodname The method to be called in order to deliver or retrieve
   *          the target object.
   * @param clazz The class (or a superclass) of the target object
   */
  public SAXAccessMethodSpec(String tag, String methodname, Class clazz) {
    this.xmlname = tag;
    this.getmethodname = methodname;
    this.clazz = clazz;
  }

  public SAXAccessMethodSpec(String tag, String methodorfieldname, Class clazz,
      String accesstype) {
    this.xmlname = tag;
    this.accesstype = accesstype;
    if (accesstype.equals(ACCESS_METHOD)) {
      this.getmethodname = methodorfieldname;
    }
    else if (accesstype.equals(ACCESS_FIELD)) {
      this.fieldname = methodorfieldname;
    }
    this.clazz = clazz;
  }

  public SAXAccessMethodSpec() {

  }

  public boolean isDuplicate(SAXAccessMethodSpec newentry) {
    if (newentry.setmethodname != null && newentry.setmethodname.equals(setmethodname))
      return true;
    if (newentry.getmethodname != null && newentry.getmethodname.equals(getmethodname))
      return true;
    if (newentry.fieldname != null && newentry.fieldname.equals(fieldname))
      return true;
    return false;
  }
  /**
   * This is a utility method, currently called by MethodAnalyser, for SAMS
   * returned from getSAXSetQqqMethod() in order to swap the name supplied for
   * "methodname" which defaults to referring to a get method into a set method.
   * 
   * @param toconvert
   */
  public static void convertToSetSpec(SAXAccessMethodSpec[] toconvert) {
    for (int i = 0; i < toconvert.length; ++i) {
      // Bail out if the swapping has already been performed!!
      if (toconvert[i].getmethodname != null) {
        toconvert[i].setmethodname = toconvert[i].getmethodname;
        toconvert[i].getmethodname = null;
      }
    }
  }

  /**
   * This is a utility method, currently called by MethodAnalyser, for SAMS
   * returned from getSAXQqqAttrMethod() in order to convert swap the name
   * supplied for "tagname" which defaults to referring to a tag method into an
   * attribute.
   * 
   * @param toconvert
   */
  public static void convertToAttrSpec(SAXAccessMethodSpec[] toconvert) {
    for (int i = 0; i < toconvert.length; ++i) {
      toconvert[i].xmlform = XML_ATTRIBUTE;
    }
  }

  // Yes, SAXAccessMethodSpecs are themselves SAXalizable.
  public SAXAccessMethodSpec[] getSAXSetMethods() {
    return new SAXAccessMethodSpec[] {
        new SAXAccessMethodSpec("getmethod", "getmethodname", String.class,
            ACCESS_FIELD),
        new SAXAccessMethodSpec("setmethod", "setmethodname", String.class,
            ACCESS_FIELD),
        new SAXAccessMethodSpec("fieldname", "fieldname", String.class,
            ACCESS_FIELD),
        new SAXAccessMethodSpec("javaclass", "clazz", Class.class, ACCESS_FIELD) };
  }

  public SAXAccessMethodSpec[] getSAXSetAttrMethods() {
    return new SAXAccessMethodSpec[] {
        new SAXAccessMethodSpec("xmlname", "xmlname", String.class,
            ACCESS_FIELD),
        new SAXAccessMethodSpec("accesstype", "accesstype", String.class,
            ACCESS_FIELD),
        new SAXAccessMethodSpec("xml-form", "xmlform", String.class,
            ACCESS_FIELD) };
  }

  public String toString() {
    return "SAXAccessMethodSpec for " + xmlform + " " + xmlname + ", getmethod: "
        + getmethodname + " setmethod: " + setmethodname + " fieldname: "
        + fieldname;
  }
}