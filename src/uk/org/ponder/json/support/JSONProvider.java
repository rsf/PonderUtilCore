/*
 * Created on 21 Feb 2008
 */
package uk.org.ponder.json.support;

import java.io.InputStream;
import java.io.OutputStream;

import uk.org.ponder.conversion.SerializationProvider;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;
import uk.org.ponder.streamutil.read.StringRIS;
import uk.org.ponder.streamutil.write.StringPOS;


public class JSONProvider implements SerializationProvider {

  private SAXalizerMappingContext mappingContext;
  
  /**
   * @param mappingContext the mappingContext to set
   */
  public void setMappingContext(SAXalizerMappingContext mappingContext) {
    this.mappingContext = mappingContext;
  }
  
  public Object fromString(String toread) {
    StringRIS ris = new StringRIS(toread);
    DeJSONalizer deJSONalizer = new DeJSONalizer(mappingContext, ris);
    return deJSONalizer.readObject(null, null);
  }

  public Object readObject(Object classorobject, InputStream is) {
    DeJSONalizer deJSONalizer = new DeJSONalizer(mappingContext, is);
    return deJSONalizer.readObject(classorobject instanceof Class? null : classorobject, 
        classorobject instanceof Class? (Class)classorobject : null);
  }
  
  public String toString(Object towrite) {
    return toString(towrite, true);
  }

  public String toString(Object towrite, boolean compact) {
    StringPOS stringPOS = new StringPOS();
    
    EnJSONalizer enJSONalizer = new EnJSONalizer(mappingContext, stringPOS);
    enJSONalizer.writeObject(towrite);
    return stringPOS.toString();
  }
 
  public void writeObject(Object towrite, OutputStream os) {
    EnJSONalizer enJSONalizer = new EnJSONalizer(mappingContext, os);
    enJSONalizer.writeObject(towrite);
  }

}
