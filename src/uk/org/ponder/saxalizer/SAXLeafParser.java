package uk.org.ponder.saxalizer;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.Hashtable;

import org.xml.sax.SAXException;

import uk.org.ponder.util.AssertionException;
import uk.org.ponder.util.LexReader;
import uk.org.ponder.util.LexUtil;
import uk.org.ponder.util.UniversalRuntimeException;

import uk.org.ponder.arrayutil.ArrayUtil;
import uk.org.ponder.saxalizer.leafparsers.intArrayParser;
import uk.org.ponder.stringutil.CharWrap;

// QQQQQ diabolically inefficient. Need to replace parse method with reader
// from CharWrap directly. 
class doubleArrayParser implements SAXLeafTypeParser {
  public Object parse(String string) {
    try {
    LexReader lr = new LexReader(new StringReader(string));
    int size = LexUtil.readInt(lr);
    double[] togo = new double[size];
    LexUtil.expect(lr, ":");
    for (int i = 0; i < size; ++ i) {
      LexUtil.skipWhite(lr);
      try {
      togo[i] = LexUtil.readDouble(lr);
      }
      catch (Exception e) {
            UniversalRuntimeException.accumulate(e, "Error reading double vector at position " + i +
             " of expected " + size);
          }
    }
    LexUtil.skipWhite(lr);
    LexUtil.expectEmpty(lr);
    return togo;
    }
    catch (IOException ioe) {
      throw UniversalRuntimeException.accumulate(ioe, "Error reading double vector");
    }
  }
  public CharWrap render(Object torendero, CharWrap renderinto) {
    double[] torender = (double[])torendero;
    renderinto.append(Integer.toString(torender.length) + ": ");
    for (int i = 0; i < torender.length; ++ i) {
      renderinto.append(Double.toString(torender[i]) + " ");
    }
    return renderinto;
  }
}

class StringParser implements SAXLeafTypeParser {
  public Object parse(String string) {
    return string;
    }
  public CharWrap render(Object torender, CharWrap renderinto) {
    return renderinto.append((String) torender);
    }
  }


class BooleanParser implements SAXLeafTypeParser {
  public Object parse(String string) {
    return (string.equals("yes") || string.equals("y") || string.equals("true"))?
      Boolean.TRUE: Boolean.FALSE;
    }
  public CharWrap render(Object torender, CharWrap renderinto) {
    return renderinto.append(torender.toString());
    }
  }

class IntegerParser implements SAXLeafTypeParser {
  public Object parse(String string) {
    return Integer.valueOf(string);
    }
  public CharWrap render(Object torender, CharWrap renderinto) {
    return renderinto.append(torender.toString());
    }
  }

class DoubleParser implements SAXLeafTypeParser {
  public Object parse(String string) {
    return Double.valueOf(string);
    }
  public CharWrap render(Object torender, CharWrap renderinto) {
    return renderinto.append(torender.toString());
    }
  }

class LongParser implements SAXLeafTypeParser {
  public Object parse(String string) {
    return Long.valueOf(string);
    }
  public CharWrap render(Object torender, CharWrap renderinto) {
    return renderinto.append(torender.toString());
    }
  }


/** A SAXLeafParser holds a registry of classes capable of parsing Strings
 * into Java objects for the purposes of representing leaf nodes of the XML
 * tag tree. 
 */
public class SAXLeafParser {
  
  public Class wrapClass(Class towrap) {
    if (towrap.equals(Double.TYPE))
    return Double.class;
    else if (towrap.equals(Long.TYPE))
    return Long.class;
    else if (towrap.equals(Integer.TYPE))
    return Integer.class;
    else if (towrap.equals(Boolean.TYPE)) 
    return Boolean.class;
    else return towrap;
  }
  // This is a hashtable of classes to SAXLeafTypeParsers
  Hashtable parseabletypes = new Hashtable();

  private static SAXLeafParser instance = new SAXLeafParser();
  // cross-hatched square character in Unicode, should be entirely unused
  // in UTF-16, would appear as percent-sign copyright-symbol. 0010010111001001
  // in UTF-8, would appear as a-hat (unassigned) (unassigned) e29789. 11100010 10010111 10001001
  //  public static final char solidus = '\u25a9'; 

  private static String NULL_STRING = "\u25a9null\u25a9";

  private void registerDefaultParsers() {
    registerParser(Boolean.class, new BooleanParser());
    registerParser(String.class, new StringParser());
    registerParser(Integer.class, new IntegerParser());
    registerParser(Double.class, new DoubleParser());
    registerParser(Long.class, new LongParser());
    registerParser(Date.class, new DateParser());
    registerParser(Class.class, new ClassParser());
    registerParser(ArrayUtil.intArrayClass, new intArrayParser());
    registerParser(ArrayUtil.doubleArrayClass, new doubleArrayParser()); 
  }
  
  public SAXLeafParser() {
    registerDefaultParsers();
  }
  
  /** Returns the global (singleton) instance of the SAXLeafParser.
   * @return The required SAXLeafParser.
   */
  public static SAXLeafParser instance() {
    return instance;
    }

  /** Registers a SAXLeafTypeParser as the parser to be used wherever a particular class occurs
   * as a leaf type during SAXalization or DeSAXalization.
   * @param toparse The class type parsed by the supplied parser object.
   * @param parser A SAXLeafTypeParser parsing objects of the supplied class.
   */

  public void registerParser(Class toparse, SAXLeafTypeParser parser) {
    parseabletypes.put(toparse, parser);
    }

  /** This method determines whether the supplied Class object is registered with
   * the leaf parser.
   * @param totest The class type to look up.
   * @return <code>true</code> if the class type is registered.
   */
  public boolean isLeafType(Class totest) {
    if (totest.isPrimitive()) {
      totest = wrapClass(totest);
    }
    return parseabletypes.get(totest) != null;
    }
  /** This method parses a <code>String</code> into any required object type, by locating the
   * required parsing class and calling any method discovered in it called <code>parse</code> that 
   * takes an argument of type <code>String</code>,
   * @param returntype The type of the required object.
   * @param bulk The data to be parsed.
   * @exception SAXException if the supplied text cannot be parsed into an object of the
   * type.
   * @return the data parsed into an object of the required type.
   */

  public Object parse(Class returntype, String bulk) throws SAXException {
    if (bulk.equals(NULL_STRING)) return null;
    SAXLeafTypeParser parser = (SAXLeafTypeParser)parseabletypes.get(returntype);
    return parser.parse(bulk);
    }

  /** This method renders the supplied object into text by locating the
   * required rendering SAXLeafTypeParser class and calling its <code>render</code> method.
   * @param torender The object to be rendered.
   * @param renderinto A vacant CharWrap object that the renderer may choose to use for its
   * rendering. 
   * @return a CharWrap object containing the rendered text.
   */
// QQQQQ Is this ever called for null objects? It should not be.
  public CharWrap render(Object torender, CharWrap renderinto) {
    if (torender == null) return renderinto.append(NULL_STRING);
    Class objtype = torender.getClass();
    SAXLeafTypeParser parser = (SAXLeafTypeParser)parseabletypes.get(objtype);
    if (parser == null) {
      throw new AssertionException("LeafParser asked to render object of type "+
				   objtype.getName()+" which has no registered parser");
      }
    return parser.render(torender, renderinto);
    }
  }
