/*
 * Created on Oct 6, 2004
 */
package uk.org.ponder.saxalizer;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import uk.org.ponder.saxalizer.mapping.MappableXMLProvider;
import uk.org.ponder.saxalizer.mapping.SAXalizerMapperEntry;
import uk.org.ponder.util.UniversalRuntimeException;
//QQQQQ This should go into saxalizer together with half of
// ResourceClassTable

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
public class SAXalXMLProvider implements MappableXMLProvider {
  SAXalizerMappingContext mappingcontext;

  private ThreadLocal saxalizergetter = new ThreadLocal() {
    public Object initialValue() {
      return new SAXalizerHelper(mappingcontext);
    }
  };

  private ThreadLocal desaxalizergetter = new ThreadLocal() {
    public Object initialValue() {
      return new DeSAXalizer(mappingcontext);
    }
  };

  public SAXalXMLProvider(SAXalizerMappingContext mappingcontext) {
    this.mappingcontext = mappingcontext;
  }

  public void writeXML(Object towrite, OutputStream os) {
    DeSAXalizer desaxalizer = (DeSAXalizer) desaxalizergetter.get();
    String resourcename = mappingcontext.classnamemanager.getClassName(towrite.getClass());
    if (resourcename == null) {
      throw new UniversalRuntimeException("Object of unknown type " 
          + towrite.getClass() + " supplied to writeXML");
    }
    try {
      desaxalizer.serializeSubtree(towrite, resourcename, os);
    }
    catch (Throwable t) {
      throw UniversalRuntimeException.accumulate(t,
          "Error writing object of class " + towrite.getClass());
    }
  }

  public Object readXML(Object classorobject, InputStream is) {
    SAXalizerHelper saxalizer = (SAXalizerHelper) saxalizergetter.get();
    Class objclass = classorobject == null? null : classorobject instanceof Class? (Class)classorobject : classorobject.getClass();
    try {
      Object toread = classorobject == null? null : classorobject == objclass? objclass.newInstance() : classorobject;
      return saxalizer.produceSubtree(toread, is);
    }
    catch (Throwable t) {
      // Xerces appears to be crap and will not clear its parsing condition once it has 
      // been set - throw the whole helper away and start again.
      saxalizergetter.set(new SAXalizerHelper(mappingcontext));
      throw UniversalRuntimeException.accumulate(t,
          "Error reading object of class " + objclass);
    }
  }

  public void loadMapping(InputStream is) {
    SAXalizerMapperEntry entry = (SAXalizerMapperEntry)readXML(SAXalizerMapperEntry.class, is);
    mappingcontext.mapper.addEntry(entry);
  }

  public void registerClass(String classname, Class resourceclass) {
    mappingcontext.classnamemanager.registerClass(classname, resourceclass);
  }

  public String toString(Object towrite) {
    DeSAXalizer desaxalizer = (DeSAXalizer) desaxalizergetter.get();
    String resourcename = mappingcontext.classnamemanager.getClassName(towrite.getClass());
    if (resourcename == null) {
      throw new UniversalRuntimeException("Object of unknown type " 
          + towrite.getClass() + " supplied to writeXML");
    }
    try {
      return desaxalizer.toString(towrite, resourcename);
    }
    catch (Throwable t) {
      throw UniversalRuntimeException.accumulate(t,
          "Error writing object of class " + towrite.getClass());
    }
  }

  private Class findClass(String classname) {
    Class objclass = mappingcontext.classnamemanager.findClazz(classname);
    if (objclass == null) {
      throw new UniversalRuntimeException("Root tag " + classname + " had no entry for object type");
    }
    return objclass;
  }
  
  public Object fromString(String toread) {
    int roottagi = toread.indexOf('<');
    if (roottagi == -1) {
      throw new UniversalRuntimeException("Couldn't find root tag in string "+ toread);
    }
    int roottagt = roottagi + 1;
    
    for (; roottagt < toread.length(); ++ roottagt) {
      char c = toread.charAt(roottagt);
      if (c == '>') break;
    }
    if (roottagt == toread.length()) {
      throw new UniversalRuntimeException("Couldn't find root tag in string "+ toread);
    }
    String roottag = toread.substring(roottagi + 1, roottagt);
    Class objclass = findClass(roottag);
    
    StringReader sr = new StringReader(toread);
    SAXalizerHelper saxalizer = (SAXalizerHelper) saxalizergetter.get();
    try {
      return saxalizer.produceSubtree(objclass.newInstance(), sr);
    }
    catch (Throwable t) {
      throw UniversalRuntimeException.accumulate(t,
          "Error reading object of class " + objclass);
    }
  }

}