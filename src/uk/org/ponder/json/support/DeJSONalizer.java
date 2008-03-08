/*
 * Created on 21 Feb 2008
 */
package uk.org.ponder.json.support;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.org.ponder.beanutil.PropertyAccessor;
import uk.org.ponder.conversion.GeneralLeafParser;
import uk.org.ponder.iterationutil.Denumeration;
import uk.org.ponder.iterationutil.EnumerationConverter;
import uk.org.ponder.reflect.ReflectUtils;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;
import uk.org.ponder.saxalizer.support.MethodAnalyser;
import uk.org.ponder.streamutil.DirectInputStreamReader;
import uk.org.ponder.streamutil.read.LexUtil;
import uk.org.ponder.streamutil.read.PushbackRIS;
import uk.org.ponder.streamutil.read.ReadInputStream;
import uk.org.ponder.streamutil.read.ReaderRIS;
import uk.org.ponder.stringutil.CharParser;
import uk.org.ponder.stringutil.CharWrap;
import uk.org.ponder.util.UniversalRuntimeException;

/** Converts a stream holding JSON data into a recognizable Java object tree * */

public class DeJSONalizer {
  private GeneralLeafParser leafParser = new GeneralLeafParser();
  private SAXalizerMappingContext mappingContext;
  private PushbackRIS lr;

  public DeJSONalizer(SAXalizerMappingContext smc, InputStream is) {
    this.mappingContext = smc;
    lr = new PushbackRIS(new ReaderRIS(new DirectInputStreamReader(is)));
  }

  public DeJSONalizer(SAXalizerMappingContext smc, ReadInputStream ris) {
    this.mappingContext = smc;
    lr = ris instanceof PushbackRIS ? (PushbackRIS) ris
        : new PushbackRIS(ris);
  }

  public Object readObject(Object base, Class clazz) {
    try {
      LexUtil.skipWhite(lr);
      char c = lr.get();
      if (c == '[') {
        return readArray(base == null ? clazz
            : base);
      }
      else if (c == '{') {
        return readHash(base, clazz);
      }
      else {
        lr.unread(c);
        return readLeaf(clazz);
      }
    }
    catch (Exception e) {
      throw UniversalRuntimeException.accumulate(e,
          "Error reading JSON-encoded data - still to read: " + LexUtil.getPending(lr));
    }
  }

  private Object readHash(Object base, Class clazz) {
    if (base == null) {
      if (clazz == null) {
        clazz = HashMap.class;
      }
      base = mappingContext.getReflectiveCache().construct(clazz);
    }
    PropertyAccessor pa = MethodAnalyser.getPropertyAccessor(base, mappingContext);
    while (true) {
      String key = (String) readLeaf(String.class);
      LexUtil.skipWhite(lr);
      LexUtil.expect(lr, ":");
      Class type = pa.getPropertyType(base, key);
      Object object = pa.getProperty(base, key);
      if (object != null) {
        type = object.getClass();
      }
      Object newobject = readObject(object, type);
      if (newobject != object) {
        pa.setProperty(base, key, newobject);
      }
      LexUtil.skipWhite(lr);
      char c = lr.get();
      if (c == '}')
        break;
      LexUtil.skipWhite(lr);
      LexUtil.unexpectEmpty(lr, "hash");
    }
    return base;
  }

  private char[] chars = new char[4];

  private Object readLeaf(Class clazz) {
    if (clazz == null || clazz == Object.class || !leafParser.isLeafType(clazz)) {
      clazz = String.class;
    }

    char c = lr.get();
    CharWrap cw = new CharWrap();
    boolean quoted = (c == '\"');
    if (!quoted)
      lr.unread(c);
    boolean escape = false;
    while (true) {
      char c2 = lr.get();
      if (escape) {
        if (c2 == 'b')
          cw.append('\b');
        else if (c2 == 't')
          cw.append('t');
        else if (c2 == 'n')
          cw.append('n');
        else if (c2 == 'r')
          cw.append('r');
        else if (c2 == 'u') {
          for (int i = 0; i < 4; ++i) {
            chars[i] = lr.get();
          }
          cw.append(CharParser.parseHexInt(chars, 0, 4));
        }
        else {
          cw.append(c2);
        }
        escape = false;
      }
      else {
        if (c2 == '\\') {
          escape = true;
        }
        else if (quoted && c2 == '"') {
          break;
        }
        else if (!quoted
            && (Character.isWhitespace(c2) || c2 == ':' || c2 == ',' || c2 == ']' || c2 == '}' || c2 == ReadInputStream.EOF)) {
          lr.unread(c2);
          break;
        }
        else if (c2 == ReadInputStream.EOF) {
          LexUtil.unexpectEmpty(lr, "leaf node");
        }
        else
          cw.append(c2);
      }
    }
    String leaf = cw.toString();
    Object togo = (leaf.equals("null") && !quoted) ? null
        : leafParser.parse(clazz, leaf);
    return togo;
  }

  private Object readArray(Object objorclass) {
    Class clazz = objorclass instanceof Class ? (Class) objorclass
        : objorclass == null ? null
            : objorclass.getClass();
    Class comptype = null;
    if (clazz != null && clazz.isArray()) {
      comptype = clazz.getComponentType();
    }
    Class infertype = null;
    List accrete;
    if (objorclass instanceof List) {
      accrete = (List) objorclass;
    }
    else
      accrete = new ArrayList();
    while (true) {
      LexUtil.skipWhite(lr);
      char c = lr.get();
      if (c == ReadInputStream.EOF)
        LexUtil.unexpectEmpty(lr, "array");
      if (c == ']')
        break;
      lr.unread(c);
      Object element = readObject(null, comptype);
      if (comptype == null && element != null) {
        infertype = inferBase(element, infertype);
      }
      accrete.add(element);
      LexUtil.skipWhite(lr);
      char c2 = lr.get();
      if (c2 != ',') {
        lr.unread(c2);
      }
    }
    if (accrete != objorclass) {
      Class eltype = comptype == null ? infertype
          : comptype;
      if (eltype == null)
        eltype = String.class; // empty array default
      Object togo = Array.newInstance(eltype, accrete.size());
      Denumeration den = EnumerationConverter.getDenumeration(togo, mappingContext
          .getReflectiveCache());
      for (int i = 0; i < accrete.size(); ++i) {
        den.add(accrete.get(i));
      }
      return togo;
    }
    return accrete;
  }

  private Class inferBase(Object element, Class comptype) {
    if (comptype == null) {
      return element.getClass();
    }
    if (comptype.isInstance(element)) {
      return comptype;
    }
    List supers = ReflectUtils.getSuperclasses(comptype);
    List elsupers = ReflectUtils.getSuperclasses(element.getClass());

    for (int i = 0; i < supers.size(); ++i) {
      Class superi = (Class) supers.get(i);
      if (elsupers.contains(superi))
        return superi;
    }
    return Object.class;
  }

}
