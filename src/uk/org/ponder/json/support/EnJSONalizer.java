/*
 * Created on 21 Feb 2008
 */
package uk.org.ponder.json.support;

import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import uk.org.ponder.conversion.GeneralLeafParser;
import uk.org.ponder.iterationutil.EnumerationConverter;
import uk.org.ponder.json.JSONWriter;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;
import uk.org.ponder.saxalizer.support.MethodAnalyser;
import uk.org.ponder.saxalizer.support.SAXAccessMethod;
import uk.org.ponder.streamutil.write.PrintOutputStream;

/** Convert any recognizable object tree into a textual JSON representation * */

public class EnJSONalizer {
  private JSONWriter writer;
  private GeneralLeafParser leafParser;
  private SAXalizerMappingContext mappingContext;

  public EnJSONalizer(SAXalizerMappingContext smc, OutputStream os) {
    this.mappingContext = smc;
    this.leafParser = smc.generalLeafParser;
    this.writer = new JSONWriter(os);
  }

  public EnJSONalizer(SAXalizerMappingContext smc, PrintOutputStream pos) {
    this.mappingContext = smc;
    this.leafParser = smc.generalLeafParser;
    this.writer = new JSONWriter(pos);
  }

  public void writeObject(Object towrite) {
    if (towrite == null) {
      writer.write(null);
      return;
    }
    Class clazz = towrite.getClass();
    if (leafParser.isLeafType(clazz)) {
      boolean quote = !(towrite instanceof Number);
      if (quote)
        writer.writeRaw("\"");
      writer.write(leafParser.render(towrite));
      if (quote)
        writer.writeRaw("\"");
    }
    else if (EnumerationConverter.isEnumerable(clazz)
        && !EnumerationConverter.isMappable(clazz)) {
      Enumeration enumm = EnumerationConverter.getEnumeration(towrite);
      writer.writeRaw("[");
      boolean first = true;
      while (enumm.hasMoreElements()) {
        Object obj = enumm.nextElement();
        if (!first) {
          writer.writeRaw(", ");
        }
        first = false;
        writeObject(obj);
      }
      writer.writeRaw("]");
    }
    else {
      writer.writeRaw("{");
      boolean first = true;
      if (towrite instanceof Map) {
        Map map = (Map) towrite;
        for (Iterator it = map.keySet().iterator(); it.hasNext();) {
          Object key = it.next();
          if (!first) {
            writer.writeRaw(", ");
          }
          first = false;
          writeObject(leafParser.render(key));
          writer.writeRaw(": ");
          writeObject(map.get(key));
        }
      }
      else {
        MethodAnalyser ma = mappingContext.getAnalyser(clazz);
        for (int i = 0; i < ma.allgetters.length; ++i) {
          SAXAccessMethod sam = ma.allgetters[i];
          if (!first) {
            writer.writeRaw(", ");
          }
          first = false;
          writeObject(sam.getPropertyName());
          writer.writeRaw(": ");
          writeObject(sam.getChildObject(towrite));
        }
      }
      writer.writeRaw("}");
    }
  }
}
