/*
 * Created on Oct 6, 2004
 */
package uk.org.ponder.saxalizer.support;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;

import uk.org.ponder.saxalizer.DeSAXalizer;
import uk.org.ponder.saxalizer.SAXalizerHelper;
import uk.org.ponder.saxalizer.SAXalizerMappingContext;
import uk.org.ponder.saxalizer.mapping.MappableXMLProvider;
import uk.org.ponder.saxalizer.mapping.SAXalizerMapperEntry;
import uk.org.ponder.streamutil.read.RISReader;
import uk.org.ponder.streamutil.read.ReadInputStream;
import uk.org.ponder.util.UniversalRuntimeException;

/**
 * @author Antranig Basman (antranig@caret.cam.ac.uk)
 *  
 */
public class SAXalXMLProvider implements MappableXMLProvider {
  private SAXalizerMappingContext mappingcontext;
  private EntityResolverStash entityresolverstash;
  
  public void setEntityResolverStash(EntityResolverStash entityresolverstash) {
    this.entityresolverstash = entityresolverstash;
  }
  
  public SAXalizerMappingContext getMappingContext() {
    return mappingcontext;
  }

  private ThreadLocal saxalizergetter = new ThreadLocal() {
    public Object initialValue() {
      SAXalizerHelper togo = new SAXalizerHelper(mappingcontext);
      togo.setEntityResolverStash(entityresolverstash);
      return togo;
    }
  };
  
  private DeSAXalizer getDeSAXalizer() {
    return new DeSAXalizer(mappingcontext); 
  }

  public SAXalXMLProvider(SAXalizerMappingContext mappingcontext) {
    this.mappingcontext = mappingcontext;
  }

  public void writeObject(Object towrite, OutputStream os) {
    DeSAXalizer desaxalizer = getDeSAXalizer();
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

  public Object readObject(Object classorobject, ReadInputStream ris) {
    Reader r = new RISReader(ris);
    return readObject(classorobject, null, r);
  }
  
  public Object readObject(Object classorobject, InputStream is) {
    return readObject(classorobject, is, null);
  }
  
  public Object readObject(Object classorobject, InputStream is, Reader reader) {
    SAXalizerHelper saxalizer = (SAXalizerHelper) saxalizergetter.get();
    Class objclass = classorobject == null? null : classorobject instanceof Class? (Class)classorobject : classorobject.getClass();
    try {
      Object toread = classorobject == null? null : classorobject == objclass? objclass.newInstance() : classorobject;
      return is == null? saxalizer.produceSubtree(toread, reader) :
          saxalizer.produceSubtree(toread, is);
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
    SAXalizerMapperEntry entry = (SAXalizerMapperEntry)readObject(SAXalizerMapperEntry.class, is);
    mappingcontext.mapper.addEntry(entry);
  }

  public void registerClass(String classname, Class resourceclass) {
    mappingcontext.classnamemanager.registerClass(classname, resourceclass);
  }

  public String toString(Object towrite) {
    return toString(towrite, true);
  }

  public String toString(Object towrite, boolean compactmode) {
    DeSAXalizer desaxalizer = getDeSAXalizer();
    String resourcename = mappingcontext.classnamemanager.getClassName(towrite.getClass());
    if (resourcename == null) {
      throw new UniversalRuntimeException("Object of unknown type " 
          + towrite.getClass() + " supplied to writeXML");
    }
    try {
      return desaxalizer.toString(towrite, resourcename, compactmode);
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
    
    int endtag = toread.indexOf('>', roottagi);
    if (endtag == -1) {
      throw new UniversalRuntimeException("Unterminated root tag in string " + toread);
    }
    
    for (; roottagt < endtag; ++ roottagt) {
      char c = toread.charAt(roottagt);
      // QQQQQ technically not correct, XML whitespace does not agree with Java.
      if (Character.isWhitespace(c)) break;
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