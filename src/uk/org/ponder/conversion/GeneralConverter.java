/*
 * Created on Nov 17, 2005
 */
package uk.org.ponder.conversion;

import uk.org.ponder.mapping.DataAlterationRequest;
import uk.org.ponder.streamutil.read.StringRIS;

/**
 * Converts objects to and from strings using one of a repertoire of strategies (leaf
 * conversion, XML or JSON)
 * 
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 * 
 */
public class GeneralConverter {
  private SerializationProvider XMLProvider;
  private SerializationProvider JSONProvider;
  private GeneralLeafParser leafParser;

  /**
   * @param provider the XMLProvider to set
   */
  public void setXMLProvider(SerializationProvider provider) {
    XMLProvider = provider;
  }

  /**
   * @param provider the JSONProvider to set
   */
  public void setJSONProvider(SerializationProvider provider) {
    JSONProvider = provider;
  }

  /**
   * @param leafParser the leafParser to set
   */
  public void setLeafParser(GeneralLeafParser leafParser) {
    this.leafParser = leafParser;
  }

  public String render(Object torender, String encoding) {
    if (torender == null || leafParser.isLeafType(torender.getClass())) {
      return leafParser.render(torender);
    }
    if (encoding.equals(DataAlterationRequest.JSON_ENCODING)) {
      return JSONProvider.toString(torender);
    }
    else if (encoding.equals(DataAlterationRequest.XML_ENCODING)) {
      return XMLProvider.toString(torender);
    }
    else
      throw new IllegalArgumentException("Cannot convert non-leaf " + torender.getClass()
          + " using leaf encoding");
  }

  public Object parse(String toparse, Class targetclass, String encoding) {
    if (encoding == null && !leafParser.isLeafType(targetclass)) {
      return toparse;
    }
    if (leafParser.isLeafType(targetclass)) {
      return leafParser.parse(targetclass, toparse);
    }
    else {
      StringRIS ris = targetclass == null ? null
          : new StringRIS(toparse);
      if (DataAlterationRequest.JSON_ENCODING.equals(encoding)) {
        return ris == null ? JSONProvider.fromString(toparse)
            : JSONProvider.readObject(targetclass, ris);
      }
      else if (DataAlterationRequest.XML_ENCODING.equals(encoding)) {
        return ris == null? XMLProvider.fromString(toparse) : 
          XMLProvider.readObject(targetclass, ris);
      }

      else
        throw new IllegalArgumentException("Cannot convert non-leaf " + targetclass
            + " using leaf encoding");
    }
  }

}
