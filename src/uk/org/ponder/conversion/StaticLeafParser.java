package uk.org.ponder.conversion;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import uk.org.ponder.arrayutil.ArrayUtil;
import uk.org.ponder.matrix.Matrix;
import uk.org.ponder.matrix.MatrixParser;
import uk.org.ponder.util.AssertionException;

class BooleanParser implements LeafObjectParser {
  public Object parse(String string) {
    return (string.equals("yes") || string.equals("y") || string.equals("true")) ? Boolean.TRUE
        : Boolean.FALSE;
  }

  public String render(Object torender) {
    return torender.toString();
  }

  public Object copy(Object tocopy) {
    return tocopy;
  }
}

class DoubleParser implements LeafObjectParser {
  public Object parse(String string) {
    return Double.valueOf(string);
  }

  public String render(Object torender) {
    return torender.toString();
  }

  public Object copy(Object tocopy) {
    return tocopy;
  }
}

class IntegerParser implements LeafObjectParser {
  public Object parse(String string) {
    return Integer.valueOf(string);
  }

  public String render(Object torender) {
    return torender.toString();
  }

  public Object copy(Object tocopy) {
    return tocopy;
  }
}

class LongParser implements LeafObjectParser {
  public Object parse(String string) {
    return Long.valueOf(string);
  }

  public String render(Object torender) {
    return torender.toString();
  }

  public Object copy(Object tocopy) {
    return tocopy;
  }
}

/**
 * A SAXLeafParser holds a registry of classes capable of parsing Strings into
 * Java objects for the purposes of representing leaf nodes of the XML tag tree.
 */

public class StaticLeafParser {

  private static final Map primitiveWrapperTypeMap = new HashMap(16);

  static {
      primitiveWrapperTypeMap.put(Boolean.TYPE, Boolean.class);
      primitiveWrapperTypeMap.put(Byte.TYPE, Byte.class);
      primitiveWrapperTypeMap.put(Character.TYPE, Character.class);
      primitiveWrapperTypeMap.put(Double.TYPE, Double.class);
      primitiveWrapperTypeMap.put(Float.TYPE, Float.class);
      primitiveWrapperTypeMap.put(Integer.TYPE, Integer.class);
      primitiveWrapperTypeMap.put(Long.TYPE, Long.class);
      primitiveWrapperTypeMap.put(Short.TYPE, Short.class);
  }
  
  public static Class wrapClass(Class towrap) {
    Class wrapper = (Class) primitiveWrapperTypeMap.get(towrap);
    return wrapper == null? towrap : wrapper;
  }

  // This is a hashtable of classes to SAXLeafTypeParsers
  HashMap parseabletypes = new HashMap();

  private static StaticLeafParser instance = new StaticLeafParser();

  // cross-hatched square character in Unicode, should be entirely unused
  // in UTF-16, would appear as percent-sign copyright-symbol. 0010010111001001
  // in UTF-8, would appear as a-hat (unassigned) (unassigned) e29789. 11100010
  // 10010111 10001001
  // public static final char solidus = '\u25a9';
  //public static String NULL_STRING = "\u25a9null\u25a9";

  private void registerDefaultParsers() {
    registerParser(Boolean.class, new BooleanParser());
    registerParser(String.class, new StringParser());
    registerParser(Integer.class, new IntegerParser());
    registerParser(Double.class, new DoubleParser());
    registerParser(Long.class, new LongParser());
    registerParser(Date.class, new DateParser());
    registerParser(java.sql.Date.class, new DateParser());
    registerParser(DateParser.class, new DateParserParser());
    registerParser(Class.class, new ClassParser());
    // TODO: These are not suitable for some environments, for example Javascript
    registerParser(ArrayUtil.intArrayClass, new intArrayParser());
    registerParser(ArrayUtil.doubleArrayClass, new doubleArrayParser());
    registerParser(ArrayUtil.stringArrayClass, StringArrayParser.instance);
    registerParser(Matrix.class, new MatrixParser());
  }

  public StaticLeafParser() {
    registerDefaultParsers();
  }

  /**
   * Returns the global (singleton) instance of the SAXLeafParser.
   * 
   * @return The required SAXLeafParser.
   */
  public static StaticLeafParser instance() {
    return instance;
  }

  /**
   * Registers a SAXLeafTypeParser as the parser to be used wherever a
   * particular class occurs as a leaf type during SAXalization or
   * DeSAXalization.
   * 
   * @param toparse
   *          The class type parsed by the supplied parser object.
   * @param parser
   *          A SAXLeafTypeParser parsing objects of the supplied class.
   */

  public void registerParser(Class toparse, LeafObjectParser parser) {
    parseabletypes.put(toparse, parser);
  }

  // A HashSet of classes already tested and found not to be
  // default-convertible.
  // TODO: This is rubbish! replace with a concurrent version at least, or
  // possibly remove this standing memory leak completely.
  private HashSet nondefault = new HashSet();

  /**
   * This method determines whether the supplied Class object is registered with
   * the leaf parser.
   * 
   * @param totest
   *          The class type to look up.
   * @return <code>true</code> if the class type is registered.
   */
  public boolean isLeafType(Class totest) {
    if (totest.isPrimitive()) {
      totest = wrapClass(totest);
    }
    boolean registered = parseabletypes.get(totest) != null;
    if (!registered && !nondefault.contains(totest)) {
      StringableLeafTypeParser leafparser = StringableLeafTypeParser
          .checkClass(totest);
      if (leafparser != null) {
        registerParser(totest, leafparser);
        return true;
      }
      else {
        nondefault.add(totest);
      }
    }
    return registered;
  }

  /**
   * This method parses a <code>String</code> into any required object type,
   * by locating the required parsing class and calling any method discovered in
   * it called <code>parse</code> that takes an argument of type
   * <code>String</code>,
   * 
   * @param returntype
   *          The type of the required object.
   * @param bulk
   *          The data to be parsed.
   * @return the data parsed into an object of the required type.
   */

  public Object parse(Class returntype, String bulk) {
    if (returntype.isPrimitive()) {
      returntype = wrapClass(returntype);
    }
    LeafObjectParser parser = (LeafObjectParser) parseabletypes.get(returntype);
    return parser.parse(bulk);
  }

  /**
   * This method renders the supplied object into text by locating the required
   * rendering SAXLeafTypeParser class and calling its <code>render</code>
   * method.
   * 
   * @param torender
   *          The object to be rendered.
   * @param renderinto
   *          A vacant CharWrap object that the renderer may choose to use for
   *          its rendering.
   * @return a CharWrap object containing the rendered text.
   */
  public String render(Object torender) {
    Class objtype = torender.getClass();
    LeafObjectParser parser = (LeafObjectParser) parseabletypes.get(objtype);
    if (parser == null) {
      throw new AssertionException("LeafParser asked to render object of type "
          + objtype.getName() + " which has no registered parser");
    }
    return parser.render(torender);
  }

  public Object copy(Object tocopy) {
    Class objtype = tocopy.getClass();
    LeafObjectParser parser = (LeafObjectParser) parseabletypes.get(objtype);
    if (parser == null) {
      throw new AssertionException("LeafParser asked to render object of type "
          + objtype.getName() + " which has no registered parser");
    }
    return parser.copy(tocopy);
  }
}

class StringParser implements LeafObjectParser {
  public Object parse(String string) {
    return string;
  }

  public String render(Object torender) {
    return (String) torender;
  }

  public Object copy(Object tocopy) {
    return tocopy;
  }
}